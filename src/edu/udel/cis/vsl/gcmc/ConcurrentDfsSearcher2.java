package edu.udel.cis.vsl.gcmc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import edu.udel.cis.vsl.gmc.StatePredicateIF;
import edu.udel.cis.vsl.gmc.TraceStepIF;

public class ConcurrentDfsSearcher2<STATE, TRANSITION, TRANSITIONSEQUENCE> {
	/**
	 * The # of threads which can be used in the concurrent searcher.
	 */
	private int N;
	
	private boolean predicateHold;
	
	private ForkJoinPool pool;

	/**
	 * The enabler, used to determine the set of enabled transitions at any
	 * state, among other things.
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

	public ConcurrentDfsSearcher2(ConcurrentEnablerIF<STATE, TRANSITION, TRANSITIONSEQUENCE> enabler,
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
	 * start a concurrent dfs task from a given state
	 * 
	 * @param initialState
	 * @return
	 */
	public void search(STATE initialState) {
		if(predicate.holdsAt(initialState)) {
			predicateHold = true;
			return;
		}
		TRANSITIONSEQUENCE transitionSequence = enabler.enabledTransitionsPOR(initialState);
		System.out.println("size:"+enabler.size(transitionSequence));
		Stack<TRANSITIONSEQUENCE> stack = new Stack<>();
		SequentialDfsSearchTask task = new SequentialDfsSearchTask(stack);
		
		task.stack.push(transitionSequence);
		manager.setOnStack(initialState, task.getId(), true);
		pool.submit(task);
		
		while(!pool.isQuiescent()){}
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
		
		public int getId(){
			return id;
		}

		private boolean proceedToNewState(STATE s ,TRANSITIONSEQUENCE transitionSequence){
			TransitionSelector selector = new TransitionSelector(transitionSequence, s);
			
			while(selector.hasNext()){
				TRANSITION t = selector.next();
				
				enabler.removeTransition(transitionSequence, t);
				TraceStepIF<TRANSITION, STATE> traceStep = manager.nextState(s, t);
				STATE newState = traceStep.getFinalState();
				
				if(predicate.holdsAt(newState))
					predicateHold = true;
				
				if(manager.onStack(newState, id) && reportCycleAsViolation){
					cycleFound = true;
					return false;
				}
				// TODO, change stateManagerIF. onStack(STATE state, threadId) to indicate 
				// whether state is on the thread's stack. Use bit vector to implement it.
				// TODO, change stateManagerIF. Add fullyExplored(STATE, state)
				if(!manager.onStack(newState, id) && 
						!manager.fullyExplored(newState)){
					TRANSITIONSEQUENCE newTransitionSequence = enabler.enabledTransitionsPOR(newState);
					
					this.stack.push(newTransitionSequence);
					manager.setOnStack(newState, id, true);
					return true;
				}
			}
			
			TRANSITIONSEQUENCE tspor = enabler.enabledTransitionsPOR(s);
			TRANSITIONSEQUENCE ts = enabler.enabledTransitions(s);
			
			// TODO change stateManagerIF. expanded(STATE state) indicate whether a state has been fully expanded.
			// TODO change enablerIF. constains(TRANSITIONSEQUENCE ts1, TRANSITIONSEQUENCE ts2): 
			// ts1 nontrivially contains ts2
			if(!manager.fullyExpanded(s) && enabler.contains(ts, tspor)){
				boolean successorsOnStack = true;
				Iterator<TRANSITION> iter = enabler.iterator(tspor);
				
				while(iter.hasNext()){
					TRANSITION transition = iter.next();
					STATE state = manager.nextState(s, transition).getFinalState();
					
					if(!manager.onStack(state, id)){
						successorsOnStack = false;
						break;
					}
				}
				if(successorsOnStack){
					//TODO change enablerIF: remove(TRANSITIONSEQUENCE ts1, TRANSITIONSEQUENCE ts2)
					// remove transitions in ts2 from ts1.
					//TODO add(TRANSITIONSEQUENCE ts1, TRANSITIONSEQUENCE ts2):
					// add all transitions in ts2 to ts1.
					enabler.removeAll(ts, tspor);
					
					enabler.addTransitionSequence(transitionSequence, ts);
					// TODO change stateManagerIF: set the expanded field of a state
					manager.setExpanded(s, true);
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
				System.out.println("thread"+id);
				// if other thread finds a cycle violation or a state that satisfies the predicate, this thread
				// should stop.
				if(cycleFound || predicateHold){
					this.stack.clear();
					return true;
				}
				
				TRANSITIONSEQUENCE transitionSequence = stack.peek();
				STATE currentState = enabler.source(transitionSequence);

				manager.setSeen(currentState, true);
				// TODO change enabler interface
				int size = enabler.size(transitionSequence);
				/**
				 * TransitionSelector is designed to pick branches to be dealt
				 * with by other threads. It should pick those branch who has
				 * not been visited first.
				 */
				TransitionSelector selector = new TransitionSelector(transitionSequence, currentState);

				/**
				 * this while loop is to spawn new threads to run new branches.
				 */
				while (size > 1 && N > 0) {
					TRANSITION t = selector.next();
					TraceStepIF<TRANSITION, STATE> traceStep = manager.nextState(currentState, t);
					STATE newState = traceStep.getFinalState();
					// TODO, change enablerIF, remove a transition from
					// transitionSequence
					enabler.removeTransition(transitionSequence, t);
					// clone the this.stack (deep clone)
					@SuppressWarnings("unchecked")
					Stack<TRANSITIONSEQUENCE> stackClone = (Stack<TRANSITIONSEQUENCE>)this.stack.clone();
					// TODO change enablerIF, add a transition to
					// transitionSequence
					enabler.addTransition(transitionSequence, t);
					
					// TODO, change enablerIF, get the transitionSequence with
					// POR
					SequentialDfsSearchTask task = new SequentialDfsSearchTask(stackClone);
					TRANSITIONSEQUENCE newTransitionSequence = enabler.enabledTransitionsPOR(newState);
					
					task.stack.push(newTransitionSequence);
					manager.setOnStack(newState, task.getId(), true);
					pool.submit(task);
					size--;
				}
				
				if(proceedToNewState(currentState, transitionSequence))
					continue;
				
				// if this thread finds a cycle violation or a state that satisfies the predicate
				// this thread should stop.
				if(cycleFound || predicateHold){
					this.stack.clear();
					return true;
				}
				
				// TODO change StateManagerIF
				// setFullyExplored(STATE state, boolean value)
				manager.setFullyExplored(currentState, true);
				this.stack.pop();
				manager.setOnStack(currentState, id, false);
			}
			return true;
		}
	}
	
	class TransitionSelector{
		private List<TRANSITION> visited;
		private List<TRANSITION> unvisited;
		
		public TransitionSelector(TRANSITIONSEQUENCE transitionSequence, STATE state){
			visited = new ArrayList<>();
			unvisited = new ArrayList<>();
			Iterator<TRANSITION> iter = enabler.iterator(transitionSequence);
					
			while(iter.hasNext()){
				
				TRANSITION t = iter.next();
				STATE s = manager.nextState(state, t).getFinalState();
				
				if(manager.seen(s))
					visited.add(t);
				else
					unvisited.add(t);
			}
		}
		
		public boolean hasNext(){
			return visited.size() + unvisited.size() > 0;
		}
		
		public TRANSITION next(){
			int visitedSize = visited.size();
			int unvisitedSize = unvisited.size();
			Random r;
			
			if(unvisitedSize > 0){
				r = new Random();
				int pick = r.nextInt(unvisitedSize);
				
				return unvisited.remove(pick);
			}else if(visitedSize > 0){
				r = new Random();
				int pick = r.nextInt(visitedSize);
				
				return visited.remove(pick);
			}
				
			return null;
		}
	}
}


