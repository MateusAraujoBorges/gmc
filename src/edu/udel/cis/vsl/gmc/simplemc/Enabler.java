package edu.udel.cis.vsl.gmc.simplemc;

import edu.udel.cis.vsl.gmc.concurrent.ConcurrentEnablerIF;

public class Enabler implements ConcurrentEnablerIF<State, Transition> {

	@Override
	public TransitionSequence enabledTransitions(State source) {
		if(source.getValue() >= 100 || source.getValue() <= -100){
			return new TransitionSequence(source);
		}
		TransitionSequence transitionSequence = Cache.getAmpleSet(source);

		if (transitionSequence != null)
			return transitionSequence;

//		long time = System.currentTimeMillis();
//		transitionSequence = new TransitionSequence(source);
//		int random;
//		if (time % 2 == 1) {
//			random = 1;
//		} else {
//			random = 0;
//		}
//		Transition t1 = new Transition(random + 2);
//		Transition t2 = new Transition(random * (-1) - 2);
//		transitionSequence.addTransitions(t1, t2);
//		Cache.addAmpleSetCache(source, transitionSequence);
		
		Transition t1 = new Transition(3);
//		Transition t2 = new Transition(-3);
		Transition t3 = new Transition(5);
//		Transition t4 = new Transition(-5);
		transitionSequence = new TransitionSequence(source);
		transitionSequence.addTransitions(t1, t3);
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return transitionSequence;
	}
	
	@Override
	public TransitionSequence ampleSetComplement(State s) {
		if(s.getValue() >= 100 || s.getValue() <= -100){
			return new TransitionSequence(s);
		}
		
		TransitionSequence ts = Cache.getNotInAmpleSet(s);
		if (ts != null) {
			return ts;
		}

		ts = new TransitionSequence(s);
//		Transition t1 = new Transition(-1);
		Transition t2 = new Transition(1);

		ts.addTransitions(t2);
		Cache.addNotInAmpleSetCache(s, ts);
		return ts;
	}
}
