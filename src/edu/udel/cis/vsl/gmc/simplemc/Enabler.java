package edu.udel.cis.vsl.gmc.simplemc;

import java.util.Random;

import edu.udel.cis.vsl.gmc.TransitionSetIF;
import edu.udel.cis.vsl.gmc.concurrent.ConcurrentEnablerIF;
import edu.udel.cis.vsl.gmc.concurrent.util.Configuration;

public class Enabler implements ConcurrentEnablerIF<State, Transition> {

	@Override
	public TransitionSequence ampleSet(State source) {
		Random r = new Random();
		TransitionSequence transitionSequence = Cache.getAmpleSet(source);

		if (transitionSequence != null)
			return transitionSequence;
		transitionSequence = new TransitionSequence(source);
		int ampleSize = Configuration.ampleSetSize;

		for (int i = 0; i < ampleSize; i++) {
			Transition t = new Transition(i);

			transitionSequence.addTransitions(t);
		}
		Cache.addAmpleSetCache(source, transitionSequence);

		// below simulating the computation of ample set
		int count = 0;
		while (count < 4000) {
			double rd = r.nextDouble();

			computeErrorRate(13.0 + rd, 11.0 + rd, 2.0 + rd);
			count++;
		}

		return transitionSequence;
	}

	@Override
	public TransitionSequence ampleSetComplement(State s) {
		int totalSize = Configuration.lengthOfVector;
		int ampleSetSize = Configuration.ampleSetSize;
		TransitionSequence ts = Cache.getNotInAmpleSet(s);

		if (ts != null) {
			return ts;
		}
		ts = new TransitionSequence(s);
		for (int i = ampleSetSize; i < totalSize; i++) {
			Transition t = new Transition(i);

			ts.addTransitions(t);
		}

		Cache.addNotInAmpleSetCache(s, ts);
		return ts;
	}

	@Override
	public TransitionSetIF<State, Transition> allEnabledTransitions(State state) {
		return null;
	}

	private double computeErrorRate(double e, double z, double n) {
		double denominator = 1 + z * z / n;
		double temp = e / n - e * e / n + z * z / (4 * n * n);
		double numerator = e + z * z / (2 * n) - z * Math.sqrt(temp);

		return numerator / denominator;
	}
}
