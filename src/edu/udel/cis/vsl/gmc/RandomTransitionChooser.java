package edu.udel.cis.vsl.gmc;

import java.util.ArrayList;
import java.util.Random;

public class RandomTransitionChooser<STATE, TRANSITION, TRANSITIONSEQUENCE>
		implements
			TransitionChooser<STATE, TRANSITION> {

	private long seed;

	private EnablerIF<STATE, TRANSITION> enabler;

	private Random generator;

	public RandomTransitionChooser(EnablerIF<STATE, TRANSITION> enabler,
			long seed) {
		this.enabler = enabler;
		this.seed = seed;
		this.generator = new Random(seed);
	}

	public RandomTransitionChooser(EnablerIF<STATE, TRANSITION> enabler) {
		this(enabler, System.currentTimeMillis());
	}

	@Override
	public TRANSITION chooseEnabledTransition(STATE state)
			throws MisguidedExecutionException {
		ArrayList<TRANSITION> transitions = new ArrayList<TRANSITION>();
		TransitionSetIF<STATE, TRANSITION> transitionSet = enabler
				.ampleSet(state);
		TransitionIterator<STATE, TRANSITION> iterator = transitionSet
				.iterator();
		int n, i;

		while (iterator.hasNext())
			transitions.add(iterator.next());
		n = transitions.size();
		if (n == 0)
			return null;
		i = generator.nextInt(n);
		return transitions.get(i);
	}

	public long getSeed() {
		return seed;
	}

}
