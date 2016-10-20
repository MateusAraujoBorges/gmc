package edu.udel.cis.vsl.gmc.concurrent;

import java.util.Enumeration;
import java.util.Stack;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;

import edu.udel.cis.vsl.gmc.StatePredicateIF;

/**
 * <p>
 * This ConcurrentDfsSearcher is implemented based on Alfons Laarman's algorithm
 * in paper "Partial-Order Reduction for Multi-Core LTL Model Checking", but
 * there are improvements being made. Rather than a totally concurrent
 * depth-first search, it may be more appropriate to say that the algorithm
 * consists of multiple sequential depth-first search that are synchronized to
 * some extent.
 * </p>
 * <p>
 * The basic idea is using multiple threads to search through the state space,
 * each of them starts a depth-first search with its own stack. And try to make
 * them search different parts of the state space.
 * </p>
 * 
 * @author yanyihao
 *
 * @param <STATE>
 * @param <TRANSITION>
 */
public class ConcurrentDfsSearcher<STATE, TRANSITION> {
	private int threadId = 0;
	/**
	 * The lock object used to generate the thread Id.
	 */
	private Object threadIdGeneratorLock = new Object();
	/**
	 * The # of threads which can be used in the concurrent searcher.
	 */
	private int N;

	/**
	 * True iff a state in which the predicate holds is found.
	 */
	private boolean predicateHold;

	/**
	 * Thread pool to manage the threads.
	 */
	private MyThreadPool pool;

	/**
	 * A ConcurrentEnablerIF used to compute ampleSet, ampleSetComplement and
	 * allEnabledTransitions of a STATE.
	 */
	private ConcurrentEnablerIF<STATE, TRANSITION> enabler;

	/**
	 * The state manager, used to determine the next state, given a state and
	 * transition. Also used for other state management issues.
	 */
	private ConcurrentStateManagerIF<STATE, TRANSITION> manager;

	/**
	 * The predicate on states. This searcher is searching for state that
	 * satisfies this predicate. Typically, this predicate describes something
	 * "bad", like deadlock.
	 */
	private StatePredicateIF<STATE> predicate;

	/**
	 * If true, a cycle in the state space is reported as a violation.
	 */
	private boolean reportCycleAsViolation = false;

	/**
	 * If this searcher stopped because a cycle was found, this flag will be set
	 * to true, else it is false.
	 */
	private boolean cycleFound = false;

	/**
	 * The number of transitions executed since the beginning of the search.
	 */
	private int totalNumTransitions = 0;

	/**
	 * The number of states encountered which are recognized as having already
	 * been seen earlier in the search.
	 */
	private int totalNumStatesMatched = 0;

	/**
	 * The number of states seen in this search.
	 */
	private int totalNumStatesSeen = 1;

	/**
	 * A name to give this searcher, used only for printing out messages about
	 * the search, such as in debugging.
	 */
	private String name = null;

	/**
	 * Are we searching for a minimal counterexample?
	 */
	private boolean minimize = false;

	public ConcurrentDfsSearcher(ConcurrentEnablerIF<STATE, TRANSITION> enabler,
			ConcurrentStateManagerIF<STATE, TRANSITION> manager,
			StatePredicateIF<STATE> predicate, int N) {

		if (enabler == null) {
			throw new NullPointerException("null enabler");
		}
		if (manager == null) {
			throw new NullPointerException("null manager");
		}
		this.enabler = enabler;
		this.manager = manager;
		this.predicate = predicate;
		this.N = N;
		this.predicateHold = false;
		this.pool = new MyThreadPool(N);
	}

	public StatePredicateIF<STATE> predicate() {
		return predicate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	public void setMinimize(boolean value) {
		this.minimize = value;
	}

	public boolean getMinimize() {
		return minimize;
	}

	public boolean reportCycleAsViolation() {
		return this.reportCycleAsViolation;
	}

	/**
	 * If you want to check for cycles in the state space, and report the
	 * existence of a cycle as a violation, this flag should be set to true.
	 * Else set it to false. By default, it is false.
	 */
	public void setReportCycleAsViolation(boolean value) {
		this.reportCycleAsViolation = value;
	}

	public boolean cycleFound() {
		return cycleFound;
	}

	/**
	 * The number of states seen in this search.
	 * 
	 * @return the number of states seen so far
	 */
	public int totalNumStatesSeen() {
		return totalNumStatesSeen;
	}

	/**
	 * The number of transitions executed in the course of this search so far.
	 * 
	 * @return the number of transitions executed.
	 */
	public int totalNumTransitions() {
		return totalNumTransitions;
	}

	/**
	 * The number of states matched so far. A state is "matched" when the search
	 * determines the state has been seen before, earlier in the search. If the
	 * state has been seen before, it is not explored.
	 * 
	 * @return the number of states matched
	 */
	public int totalNumStatesMatched() {
		return totalNumStatesMatched;
	}

	/**
	 * Start a concurrent dfs task from a given state
	 * 
	 * @param initialState
	 *            The state the search starts from.
	 */
	public void search(STATE initialState) {
		if (predicate.holdsAt(initialState)) {
			predicateHold = true;
			return;
		}
		SequentialDfsSearchTask task = new SequentialDfsSearchTask(initialState,
				generateThreadId(), null, null);
		pool.submit(task);
		while (!pool.isQuiescent());
		pool.shutdown();
	}

	private class SequentialDfsSearchTask extends ForkJoinTask<Integer> {

		private static final long serialVersionUID = -2011438813013648270L;

		private AtomicInteger parentCounter = null;

		private int id;

		/**
		 * Each thread will have its own stack and elements in the stack are
		 * {@link TransitionIterator}.
		 */
		private Stack<TransitionIterator<STATE, TRANSITION>> stack;

		/**
		 * The number of transitions executed since the beginning of the search.
		 */
		private int numTransitions = 0;

		/**
		 * The number of states encountered which are recognized as having
		 * already been seen earlier in the search.
		 */
		private int numStatesMatched = 0;

		/**
		 * The number of states seen in this search.
		 */
		private int numStatesSeen = 1;

		/**
		 * Upper bound on stack depth.
		 */
		private int depthBound = Integer.MAX_VALUE;

		/**
		 * Place an upper bound on stack size (depth).
		 */
		private boolean stackIsBounded = false;

		/**
		 * Are we searching for a minimal counterexample?
		 */
		private boolean minimize = false;

		public SequentialDfsSearchTask(STATE initState, int id,
				SequentialDfsSearchTask parent, AtomicInteger counter) {
			TransitionSet<STATE, TRANSITION> ts = enabler.ampleSet(initState);
			TransitionIterator<STATE, TRANSITION> iter = ts.randomIterator();

			this.id = id;
			this.parentCounter = counter;
			this.stack = new Stack<>();
			this.stack.push(iter);
			manager.setOnStack(initState, id, true);

			if (parent != null) {
				Stack<TransitionIterator<STATE, TRANSITION>> parentStack = parent
						.stack();
				Enumeration<TransitionIterator<STATE, TRANSITION>> elements = parentStack
						.elements();

				while (elements.hasMoreElements()) {
					STATE s = elements.nextElement().getTransitionSet()
							.source();

					manager.setOnStack(s, id, true);
				}
			}
		}

		public StatePredicateIF<STATE> predicate() {
			return predicate;
		}

		public boolean isDepthBounded() {
			return stackIsBounded;
		}

		public void unboundDepth() {
			this.stackIsBounded = false;
			depthBound = Integer.MAX_VALUE;
		}

		public void boundDepth(int value) {
			depthBound = value;
			stackIsBounded = true;
		}

		public void restrictDepth() {
			depthBound = stack.size() - 1;
			stackIsBounded = true;
		}

		public void setMinimize(boolean value) {
			this.minimize = value;
		}

		public boolean getMinimize() {
			return minimize;
		}

		public Stack<TransitionIterator<STATE, TRANSITION>> stack() {
			return stack;
		}

		@Override
		public Integer getRawResult() {
			return null;
		}

		@Override
		protected void setRawResult(Integer value) {
		}

		@Override
		protected boolean exec() {
			Stack<Boolean> allOnStack = new Stack<>();
			Stack<AtomicInteger> counters = new Stack<>();

			allOnStack.push(true);
			counters.push(null);
			while (!stack.empty()) {
				boolean aos = allOnStack.pop();
				AtomicInteger counter = counters.peek();

				// if other thread finds a cycle violation or a state that
				// satisfies the predicate, this thread should stop.
				if (cycleFound || predicateHold) {
					this.stack.clear();
					return true;
				}

				TransitionIterator<STATE, TRANSITION> iterator = stack.peek();
				TransitionSet<STATE, TRANSITION> transitionSet = iterator
						.getTransitionSet();
				STATE currentState = transitionSet.source();
				TRANSITION transition = null;
				/*
				 * spawn new threads
				 */
				while (iterator.hasNext()) {
					transition = iterator.next();
					if (iterator.hasNext()) {
						synchronized (pool) {
							if (pool.getActiveNum() < N
									&& pool.getRunningNum() < pool
											.getMaxNumOfThread()) {
								STATE newState = manager.nextState(id, 1,
										currentState, transition)
										.getFinalState();

								if (predicate.holdsAt(newState)) {
									predicateHold = true;
									return true;
								}
								if (!manager.onStack(newState, id)) {
									aos = false;
									if (!manager.fullyExplored(newState)) {
										if (counter == null) {
											counter = new AtomicInteger(1);
										} else {
											counter.incrementAndGet();
										}

										SequentialDfsSearchTask newTask = new SequentialDfsSearchTask(
												newState, generateThreadId(),
												this, counter);
										// numOfRunningChildren.incrementAndGet();
										pool.submit(newTask);
									}
								} else {
									// newState is on the stack, then there is a
									// cycle
									if (reportCycleAsViolation) {
										cycleFound = true;
										return true;
									}
								}
							} else {
								break;
							}
						}
					} else {
						break;
					}
				}
				// move on to next state
				boolean continueDFS = false;
				do {
					if (transition != null) {
						STATE newState = manager
								.nextState(id, 2, currentState, transition)
								.getFinalState();

						if (predicate.holdsAt(newState)) {
							predicateHold = true;
							return true;
						}
						if (!manager.onStack(newState, id)) {
							aos = false;
							if (!manager.fullyExplored(newState)) {
								TransitionSet<STATE, TRANSITION> newTransitionSet = enabler
										.ampleSet(newState);

								this.stack.push(
										newTransitionSet.randomIterator());
								manager.setOnStack(newState, id, true);
								continueDFS = true;
								allOnStack.push(aos);
								allOnStack.push(true);
								counters.push(null);
								break;
							}
						} else {
							if (reportCycleAsViolation) {
								cycleFound = true;
								return true;
							}
						}
					}
					transition = null;
					if (iterator.hasNext())
						transition = iterator.next();
				} while (transition != null);
				if (continueDFS)
					continue;
				if (checkStackProviso(transitionSet, currentState, aos)) {
					allOnStack.push(aos);
					continue;
				}

				if (counter != null) {
					synchronized (counter) {
						while (counter.intValue() > 0) {
							try {
								System.out.println("waiting...");
								pool.incrementWaiting();
								counter.wait();
								pool.decrementWaiting();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
				manager.setFullyExplored(currentState, true);
				this.stack.pop();
				manager.setOnStack(currentState, id, false);
				counters.pop();
			}
			if (parentCounter != null) {
				synchronized (parentCounter) {
					parentCounter.decrementAndGet();
					parentCounter.notify();
				}
			}
			return true;
		}

		private boolean checkStackProviso(
				TransitionSet<STATE, TRANSITION> transitionSet,
				STATE currentState, boolean allOnStack) {
			if (manager.proviso(currentState) == ProvisoValue.UNKNOWN) {
				manager.setProvisoCAS(currentState,
						(allOnStack ? ProvisoValue.TRUE : ProvisoValue.FALSE));
				if (manager.proviso(currentState) == ProvisoValue.TRUE) {
					TransitionSet<STATE, TRANSITION> ampleComplement = enabler
							.ampleSetComplement(currentState);
					TransitionIterator<STATE, TRANSITION> iter = ampleComplement
							.randomIterator();

					if (iter.hasNext()) {
						stack.pop();
						stack.push(iter);
						return true;
					}
				}
			}
			return false;
		}
	}

	private int generateThreadId() {
		synchronized (threadIdGeneratorLock) {
			return threadId++;
		}
	}
}
