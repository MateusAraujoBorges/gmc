package edu.udel.cis.vsl.gmc.simplemc;

import edu.udel.cis.vsl.gmc.StatePredicateIF;

public class StatePredicate implements StatePredicateIF<State>{

	@Override
	public boolean holdsAt(State state) {
		if(state == null) return false;
		if(state.isFinalState())
			return true;
		return false;
	}

	@Override
	public String explanation() {
		// TODO Auto-generated method stub
		return null;
	}

}
