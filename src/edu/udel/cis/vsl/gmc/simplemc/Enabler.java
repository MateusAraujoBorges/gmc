package edu.udel.cis.vsl.gmc.simplemc;

import edu.udel.cis.vsl.gmc.concurrent.ConcurrentEnablerIF;
import edu.udel.cis.vsl.gmc.concurrent.TransitionSet;

public class Enabler implements ConcurrentEnablerIF<State, Transition> {

	@Override
	public TransitionSequence ampleSet(State source) {
		TransitionSequence transitionSequence = Cache.getAmpleSet(source);

		if (transitionSequence != null)
			return transitionSequence;

		Transition t1 = new Transition(0);
		Transition t2 = new Transition(1);
		transitionSequence = new TransitionSequence(source);
		transitionSequence.addTransitions(t1, t2);

		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return transitionSequence;
	}

	@Override
	public TransitionSequence ampleSetComplement(State s) {
		TransitionSequence ts = Cache.getNotInAmpleSet(s);
		if (ts != null) {
			return ts;
		}

		ts = new TransitionSequence(s);
		Transition t1 = new Transition(2);
		Transition t2 = new Transition(3);

		ts.addTransitions(t1, t2);
		Cache.addNotInAmpleSetCache(s, ts);
		return ts;
	}

	@Override
	public TransitionSet<State, Transition> allEnabledTransitions(State state) {
		// TODO Auto-generated method stub
		return null;
	}
}
