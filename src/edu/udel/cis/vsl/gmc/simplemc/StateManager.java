package edu.udel.cis.vsl.gmc.simplemc;

import java.io.PrintStream;
import edu.udel.cis.vsl.gcmc.ConcurrentStateManagerIF;
import edu.udel.cis.vsl.gmc.TraceStepIF;

public class StateManager implements ConcurrentStateManagerIF<State, Transition>{

	@Override
	public TraceStepIF<Transition, State> nextState(State state, Transition transition) {
		State newState = StateFactory.getState(state.getValue() + transition.getOffset());
		
		return new TraceStep(newState);
	}

	@Override
	public void setSeen(State state, boolean value) {
		state.setSeen(value);
		System.out.println(state.getValue());
	}

	@Override
	public boolean seen(State state) {
		return state.isSeen();
	}

//	@Override
//	public void setOnStack(State state, boolean value) {
//		state.setOnStack(value);
//	}

//	@Override
//	public boolean onStack(State state) {
//		return state.isOnStack();
//	}

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
		if(state.getInviolable() == 0){
			state.setInviolable(value);
		}
	}

	@Override
	public int isInviolable(State state) {
		return state.getInviolable();
	}
}
