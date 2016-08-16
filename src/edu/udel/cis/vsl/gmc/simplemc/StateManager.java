package edu.udel.cis.vsl.gmc.simplemc;

import edu.udel.cis.vsl.gmc.TraceStepIF;
import edu.udel.cis.vsl.gmc.concurrent.ConcurrentStateManagerIF;
import edu.udel.cis.vsl.gmc.concurrent.util.Lock;
import edu.udel.cis.vsl.gmc.concurrent.util.Log;
import edu.udel.cis.vsl.gmc.concurrent.util.Transaction;

public class StateManager implements ConcurrentStateManagerIF<State, Transition>{
	private Object inviolableCASLock = new Object();
	
	/**
	 * transactionId = 1 : spawn new thread
	 * transactionId = 2 : proceed depth first search
	 * transactionId = 3 : check stack proviso
	 */
	@Override
	public TraceStepIF<Transition, State> nextState(int threadId, int transactionId, State state, Transition transition) {
		State newState = StateFactory.getState(state.getValue() + transition.getOffset());
		Transaction transaction = new Transaction(threadId, transactionId, state, transition, newState);
		Log.add(transaction);
		return new TraceStep(newState);
	}

	@Override
	public void setSeen(int id, State state, boolean value) {
		synchronized (Lock.printLock) {
			int temp = id;
			while(temp > 1){
				System.out.print("                  ");
				temp--;
			}
			System.out.println("state:"+state.getValue()+" ");
		}
		state.setSeen(value);
	}

	@Override
	public boolean onStack(State state, int id) {
		return state.isOnStack(id);
	}
	
	@Override
	public void setOnStack(State state, int id, boolean value) {
		synchronized (this) {
			state.setOnStack(id, value);
		}
	}

	@Override
	public boolean fullyExplored(State state) {
		return state.isFullyExplored();
	}

	@Override
	public void setFullyExplored(State state, boolean value) {
		state.setFullyExplored(value);
	}

	@Override
	public void setInviolableCAS(State state, int value) {
		synchronized (inviolableCASLock) {
			if(state.getInviolable() == 0){
				state.setInviolable(value);
			}
		}	
	}

	@Override
	public int isInviolable(State state) {
		return state.getInviolable();
	}
}
