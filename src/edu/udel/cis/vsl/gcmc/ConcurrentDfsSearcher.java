package edu.udel.cis.vsl.gcmc;

import java.util.Stack;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import edu.udel.cis.vsl.gmc.EnablerIF;
import edu.udel.cis.vsl.gmc.StateManagerIF;
import edu.udel.cis.vsl.gmc.StatePredicateIF;
import edu.udel.cis.vsl.gmc.TraceStepIF;



public class ConcurrentDfsSearcher<STATE, TRANSITION, TRANSITIONSEQUENCE> {
	private ForkJoinPool pool = null;
	
	/**
	 * The enabler, used to determine the set of enabled transitions at any
	 * state, among other things.
	 */
	private EnablerIF<STATE, TRANSITION, TRANSITIONSEQUENCE> enabler;
	
	/**
	 * The state manager, used to determine the next state, given a state and
	 * transition. Also used for other state management issues.
	 */
	private StateManagerIF<STATE, TRANSITION> manager;
	
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
	
	private Object transitionsLock = new Object();
	
	/**
	 * The number of states encountered which are recognized as having already
	 * been seen earlier in the search.
	 */
	private int totalNumStatesMatched = 0;
	
	private Object matchedLock = new Object();
	
	/**
	 * The number of states seen in this search.
	 */
	private int totalNumStatesSeen = 1;
	
	private Object seenLock = new Object();
	
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
//	private int summaryCutOff = 5;
	
	/**
	 * Are we searching for a minimal counterexample?
	 */
	private boolean minimize = false;
	
	public ConcurrentDfsSearcher(
			EnablerIF<STATE, TRANSITION, TRANSITIONSEQUENCE> enabler,
			StateManagerIF<STATE, TRANSITION> manager,
			StatePredicateIF<STATE> predicate,
			int paraLevel) {

		if (enabler == null) {
			throw new NullPointerException("null enabler");
		}
		if (manager == null) {
			throw new NullPointerException("null manager");
		}
		this.enabler = enabler;
		this.manager = manager;
		this.predicate = predicate;
		this.pool = new ForkJoinPool(paraLevel);
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
	 * start a Sequential dfs task from a given state
	 * @param initialState
	 * @return
	 */
	public void search(STATE initialState) {
		this.pool.submit(new SequentialDfsSearchTask(initialState));
		while (!this.pool.isQuiescent()) {
		}
		this.pool.shutdown();
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
	
	private class SequentialDfsSearchTask
		extends ForkJoinTask<Integer>{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2679070135945099605L;

		private STATE initialState;
		
		/**
		 * The depth-first search stack. An element in this stack in a transition
		 * sequence, which encapsulates a state together with the transitions
		 * enabled at that state which have not yet been completely explored.
		 */
		private Stack<TRANSITIONSEQUENCE> stack;
		/**
		 * The number of transitions executed since the beginning of the search.
		 */
		private int numTransitions = 0;
		/**
		 * The number of states encountered which are recognized as having already
		 * been seen earlier in the search.
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
		
		public SequentialDfsSearchTask(STATE state) {
			initialState = state;
			stack = new Stack<TRANSITIONSEQUENCE>();
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
		
		/** Returns the state at the top of the stack, without modifying the stack. */
		public STATE currentState() {
			if (stack.isEmpty()) {
				return null;
			} else {
				return enabler.source(stack.peek());
			}
		}
		
		/** Returns the stack used to perform the depth first search */
		public Stack<TRANSITIONSEQUENCE> stack() {
			return stack;
		}
		
		public boolean search(STATE initialState) {
			boolean result = true;
			
			//TODO manager.setSeen method needs to be atomic
			if(!manager.setseen(initialState))
				return false;
			
			if (minimize)
				manager.setDepth(initialState, 0);
			stack.push(enabler.enabledTransitions(initialState));
			manager.setOnStack(initialState, true);
			result = search();
			synchronized (seenLock) {
				totalNumStatesSeen += numStatesSeen;
			}
			synchronized (matchedLock) {
				totalNumStatesMatched += numStatesMatched;
			}
			synchronized(transitionsLock){
				totalNumTransitions += numTransitions;
			}
			return result;
		}
		
		public boolean search() {
			while (!predicate.holdsAt(currentState())) {
				if (!proceedToNewState()) {
					if (cycleFound) {
						return true;
					}
					return false;
				}
			}
			return true;
		}

		public boolean proceedToNewState() {
			while (!stack.isEmpty()) {
				TRANSITIONSEQUENCE sequence = stack.peek();
				STATE currentState = enabler.source(sequence);

				while ((!stackIsBounded || stack.size() < depthBound)
						&& enabler.hasNext(sequence)) {
					TRANSITION transition = enabler.next(sequence);
					if(!enabler.hasNext(sequence)){
						manager.setOnStack(enabler.source(sequence), false);
						stack.pop();
					}
					TraceStepIF<TRANSITION, STATE> traceStep = manager.nextState(
							currentState, transition);
					STATE newState = traceStep.getFinalState();

					numTransitions++;
					
					if (!manager.seen(newState)
							|| (minimize && stack.size() < manager
									.getDepth(newState))) {
						synchronized (pool) {
							if (pool.getParallelism() > pool.getActiveThreadCount()) {
								System.out.println("new Threads created !");
								pool.submit(new SequentialDfsSearchTask(newState));
								continue;
							}
						}
						assert !manager.onStack(newState);
						if (minimize)
							manager.setDepth(newState, stack.size());
						stack.push(enabler.enabledTransitions(newState));
						manager.setSeen(newState, true);
						manager.setOnStack(newState, true);
						numStatesSeen++;
						return true;
					}
					if (reportCycleAsViolation && manager.onStack(newState)) {
						cycleFound = true;
						return false;
					}
					numStatesMatched++;
//					enabler.next(sequence);
				}
			}
			return false;
		}
		
		@Override
		public Integer getRawResult() {
			return null;
		}

		@Override
		protected void setRawResult(Integer value) {}

		@Override
		protected boolean exec() {
			search(initialState);
			this.complete(0);
			return true;
		}
		
	}
	
}
