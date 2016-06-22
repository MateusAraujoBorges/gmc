package edu.udel.cis.vsl.gmc.simplemc;

import java.io.PrintStream;

import edu.udel.cis.vsl.gmc.StateManagerIF;
import edu.udel.cis.vsl.gmc.TraceStepIF;

public class StateManager implements StateManagerIF<State, Transition>{

	@Override
	public TraceStepIF<Transition, State> nextState(State state, Transition transition) {
		State newState = StateFactory.getState(state.getValue() + transition.getOffset());
		return new TraceStep(newState);
	}

	@Override
	public void setSeen(State state, boolean value) {
		state.setSeen(value);
	}

	@Override
	public boolean seen(State state) {
		return state.isSeen();
	}

	@Override
	public void setOnStack(State state, boolean value) {
		state.setOnStack(value);
	}

	@Override
	public boolean onStack(State state) {
		return state.isOnStack();
	}

	@Override
	public void printStateShort(PrintStream out, State state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printStateLong(PrintStream out, State state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printTransitionShort(PrintStream out, Transition transition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printTransitionLong(PrintStream out, Transition transition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printAllStatesShort(PrintStream out) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printAllStatesLong(PrintStream out) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getDepth(State state) {
		return state.getDepth();
	}

	@Override
	public void setDepth(State state, int value) {
		state.setDepth(value);
	}

	@Override
	public boolean setseen(State state) {
		synchronized (this) {
			if(state.isSeen())
				return false;
			else{
				state.setSeen(true);
				return true;
			}
		}
	}
	
}
