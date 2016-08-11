package edu.udel.cis.vsl.gmc.simplemc;

import edu.udel.cis.vsl.gmc.concurrent.ConcurrentEnablerIF;

public class Enabler implements ConcurrentEnablerIF<State, Transition, TransitionSequence>{

	@Override
	public TransitionSequence enabledTransitions(State source) {
		long time = System.currentTimeMillis();
		TransitionSequence transitionSequence = new TransitionSequence(source);
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		transitionSequence.addTransitions(new Transition((int)time%2 + 1)
				, new Transition((int)time%2*-1 - 1)
				);
		
		return transitionSequence;
	}

	@Override
	public State source(TransitionSequence sequence) {
		return sequence.state();
	}

	@Override
	public boolean hasNext(TransitionSequence sequence) {
		return sequence.hasNext();
	}

	@Override
	public Transition next(TransitionSequence sequence) {
		return sequence.next();
	}
	@Override
	public int size(TransitionSequence transitionSequence) {
		return transitionSequence.size();
	}

	@Override
	public Transition randomNext(TransitionSequence transitionSequence) {
		return transitionSequence.randomNext();
	}

	@Override
	public Transition[] randomPeekN(TransitionSequence transitionSequence, int n) {
		return transitionSequence.randomPeekN(n);
	}

	@Override
	public TransitionSequence transitionsNotInAmpleSet(State s) {
		TransitionSequence ts = new TransitionSequence(s);
		Transition t1 = new Transition(-3);
		Transition t2 = new Transition(3);
		
		ts.addTransitions(t1, t2);
		return ts;
	}

	@Override
	public void expandToFull(TransitionSequence transitionSequence) {
		System.out.println("expand full!");
		State state = source(transitionSequence);
		TransitionSequence ts = transitionsNotInAmpleSet(state);
		transitionSequence.addTransitionSequence(ts);
	}
}
