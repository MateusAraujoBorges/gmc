package edu.udel.cis.vsl.gmc.simplemc;

import edu.udel.cis.vsl.gmc.TraceStepIF;
import edu.udel.cis.vsl.gmc.concurrent.ConcurrentStateManagerIF;
import edu.udel.cis.vsl.gmc.concurrent.ProvisoValue;
import edu.udel.cis.vsl.gmc.concurrent.util.Log;
import edu.udel.cis.vsl.gmc.concurrent.util.Transaction;

public class StateManager implements ConcurrentStateManagerIF<State, Transition> {
	private final int CONSTANT = 15;
	private Object inviolableCASLock = new Object();
	private Object onStackLock = new Object();

	/**
	 * transactionId = 1 : spawn new thread transactionId = 2 : proceed depth
	 * first search transactionId = 3 : check stack proviso
	 */
	@Override
	public TraceStepIF<Transition, State> nextState(int threadId, int transactionId, State state,
			Transition transition) {
		int[] newValue = null;
		int[] value = state.getValue();
		int ele1 = value[0];
		int ele2 = value[1];
		int ele3 = value[2];
		int ele4 = value[3];

		switch (transition.getId()) {
		case 0:
			newValue = state.setValue(0, (ele1 + ele2) % CONSTANT);
			break;
		case 1:
			newValue = state.setValue(1, (ele1 + ele2) % CONSTANT);
			break;
		case 2:
			newValue = state.setValue(2, (ele3 + 1) % CONSTANT);
			break;
		case 3:
			newValue = state.setValue(3, (ele4 + 1) % CONSTANT);
			break;
		default:
			System.out.println("Error! no such transition.");
		}

		State newState = StateFactory.getState(newValue);

		 Transaction transaction = new Transaction(threadId, transactionId,
		 state, transition, newState);
		 Log.add(transaction);

		return new TraceStep(newState);
	}

	@Override
	public boolean onStack(State state, int id) {
		return state.isOnStack(id);
	}

	@Override
	public void setOnStack(State state, int id, boolean value) {
		synchronized (onStackLock) {
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
	public void setProvisoCAS(State state, ProvisoValue value) {
		synchronized (inviolableCASLock) {
			if (state.prov() == ProvisoValue.UNKNOWN) {
				state.setProv(value);
			}
		}
	}

	@Override
	public ProvisoValue proviso(State state) {
		return state.prov();
	}
}
