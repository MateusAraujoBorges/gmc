package edu.udel.cis.vsl.gmc.simplemc;

import edu.udel.cis.vsl.gmc.TraceStepIF;

public class TraceStep implements TraceStepIF<State>{
	private State state;
	
	TraceStep(State state){
		this.state = state;
	}
	
	@Override
	public State getFinalState() {
		return state;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
}
