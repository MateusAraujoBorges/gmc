package edu.udel.cis.vsl.gmc.concurrent;

import java.util.Stack;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import edu.udel.cis.vsl.gmc.StatePredicateIF;

/**
 * This ConcurrentDfsSearcher is implemented based on Alfons Laarman's algorithm
 * in paper "Partial-Order Reduction for Multi-Core LTL Model Checking", but
 * there are improvements being made. Rather than a totally concurrent
 * depth-first search, it may be more appropriate to say that the algorithm
 * consists of multiple sequential depth-first search that are synchronized to
 * some extent.
 * 
 * The basic idea is using multiple threads to search through the state space,
 * each of them starts a depth-first search with its own stack. And try to make
 * them search different parts of the state space.
 * 
 * @author yanyihao
 *
 * @param <STATE>
 * @param <TRANSITION>
 * @param <TRANSITIONSEQUENCE>
 */
public class ConcurrentDfsSearcher<STATE, TRANSITION, TRANSITIONSEQUENCE> {
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
	private ForkJoinPool pool;

	/**
	 * The enabler, used to determine the set of enabled transitions at any
	 * state, among other things. Also used for other transitionSequence issues.
	 */
	private ConcurrentEnablerIF<STATE, TRANSITION, TRANSITIONSEQUENCE> enabler;

	/**
	 * The state manager, used to determine the next state, given a state and
	 * transition. Also used for other state management issues.
	 */
	private ConcurrentStateManagerIF<STATE, TRANSITION> manager;

	/**
	 * The predicate on states. This searching is searching for state that
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
	 * locks used to do statistics.
	 */
	// private Object transitionsLock = new Object();
	//
	// private Object matchedLock = new Object();
	//
	// private Object seenLock = new Object();

	private Object threadNumLock = new Object();

	/**
	 * A name to give this searcher, used only for printing out messages about
	 * the search, such as in debugging.
	 */
	private String name = null;

	/**
	 * When the stack is being summarized in debugging output, this is the upper
	 * bound on the number of stack entries (starting from the top and moving
	 * down) that will be printed.
	 */
	// private int summaryCutOff = 5;

	/**
	 * Are we searching for a minimal counterexample?
	 */
	private boolean minimize = false;

	public ConcurrentDfsSearcher(ConcurrentEnablerIF<STATE, TRANSITION, TRANSITIONSEQUENCE> enabler,
			ConcurrentStateManagerIF<STATE, TRANSITION> manager, StatePredicateIF<STATE> predicate, int N) {

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
		this.pool = new ForkJoinPool(N);
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

		TRANSITIONSEQUENCE transitionSequence = enabler.enabledTransitions(initialState);
		Stack<TRANSITIONSEQUENCE> stack = new Stack<>();
		SequentialDfsSearchTask task = new SequentialDfsSearchTask(stack);

		task.stack.push(transitionSequence);
		manager.setOnStack(initialState, task.getId(), true);
		pool.submit(task);
		while (!pool.isQuiescent())
			;
		pool.shutdown();
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

	private class SequentialDfsSearchTask extends ForkJoinTask<Integer> {

		private static final long serialVersionUID = -2011438813013648270L;

		private int id;

		/**
		 * The depth-first search stack. An element in this stack in a
		 * transition sequence, which encapsulates a state together with the
		 * transitions enabled at that state which have not yet been completely
		 * explored.
		 */
		protected Stack<TRANSITIONSEQUENCE> stack;
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

		public SequentialDfsSearchTask(Stack<TRANSITIONSEQUENCE> stack) {
			this.stack = stack;
			synchronized (threadNumLock) {
				id = N;
				N--;
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

		public int getId() {
			return id;
		}

		private boolean proceedToNewState(STATE s, TRANSITIONSEQUENCE transitionSequence) {
			while (enabler.hasNext(transitionSequence)) {
				TRANSITION t = enabler.randomNext(transitionSequence);
				STATE newState = manager.nextState(s, t).getFinalState();

				if (predicate.holdsAt(newState)) {
					predicateHold = true;
					return false;
				}
				if (manager.onStack(newState, id) && reportCycleAsViolation) {
					cycleFound = true;
					return false;
				}
				if (!manager.onStack(newState, id) && !manager.fullyExplored(newState)) {
					TRANSITIONSEQUENCE newTransitionSequence = enabler.enabledTransitions(newState);

					this.stack.push(newTransitionSequence);
					manager.setOnStack(newState, id, true);
					return true;
				}
			}
			if (manager.isInviolable(s) == 0 && enabler.size(enabler.transitionsNotInAmpleSet(s)) > 0) {
				boolean successorsOnStack = true;
				TRANSITIONSEQUENCE ampleSet = enabler.enabledTransitions(s);

				while (enabler.hasNext(ampleSet)) {
					TRANSITION t = enabler.next(ampleSet);
					STATE newState = manager.nextState(s, t).getFinalState();

					if (!manager.onStack(newState, id)) {
						successorsOnStack = false;
						break;
					}
				}
				manager.setInviolableCAS(s, successorsOnStack ? 1 : -1);
				if (manager.isInviolable(s) == 1) {
					enabler.expandToFull(transitionSequence);
					return true;
				}
			}
			return false;
		}

		@Override
		public Integer getRawResult() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void setRawResult(Integer value) {
			// TODO Auto-generated method stub
		}

		@Override
		protected boolean exec() {
			while (!stack.empty()) {
				// if other thread finds a cycle violation or a state that
				// satisfies the predicate, this thread should stop.
				if (cycleFound || predicateHold) {
					this.stack.clear();
					return true;
				}
				TRANSITIONSEQUENCE transitionSequence = stack.peek();
				STATE currentState = enabler.source(transitionSequence);
				int size = enabler.size(transitionSequence);
				// the # of new threads
				int n = (size - 1) > N ? N : size - 1;
				TRANSITION[] transitions = enabler.randomPeekN(transitionSequence, n);

				/*
				 * this while loop is to spawn new threads to run new branches.
				 */
				for (TRANSITION t : transitions) {
					STATE newState = manager.nextState(currentState, t).getFinalState();
					// clone the this.stack (deep clone)
					@SuppressWarnings("unchecked")
					Stack<TRANSITIONSEQUENCE> stackClone = (Stack<TRANSITIONSEQUENCE>) this.stack.clone();
					SequentialDfsSearchTask task = new SequentialDfsSearchTask(stackClone);
					TRANSITIONSEQUENCE newTransitionSequence = enabler.enabledTransitions(newState);

					task.stack.push(newTransitionSequence);
					manager.setOnStack(newState, task.getId(), true);
					pool.submit(task);
					size--;
				}

				if (proceedToNewState(currentState, transitionSequence))
					continue;
				// if this thread finds a cycle violation or a state that
				// satisfies the predicate this thread should stop.
				if (cycleFound || predicateHold) {
					this.stack.clear();
					return true;
				}

				manager.setFullyExplored(currentState, true);
				this.stack.pop();
				manager.setOnStack(currentState, id, false);
			}
			return true;
		}
	}
}
