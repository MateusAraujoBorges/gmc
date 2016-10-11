package edu.udel.cis.vsl.gmc.simplemc;

import java.util.Random;

import edu.udel.cis.vsl.gmc.concurrent.ConcurrentEnablerIF;
import edu.udel.cis.vsl.gmc.concurrent.TransitionSet;

public class Enabler implements ConcurrentEnablerIF<State, Transition> {

	@Override
	public TransitionSequence ampleSet(State source) {
		Random r = new Random();
		TransitionSequence transitionSequence = Cache.getAmpleSet(source);

		if (transitionSequence != null)
			return transitionSequence;

		Transition t1 = new Transition(0);
		Transition t2 = new Transition(1);
		transitionSequence = new TransitionSequence(source);
		transitionSequence.addTransitions(t1, t2);
		Cache.addAmpleSetCache(source, transitionSequence);
		
		// below simulating the computation of ample set
		int count = 0;
		while (count < 10000) {
			double rd = r.nextDouble();
			computeErrorRate(13.0 + rd, 11.0 + rd, 2.0 + rd);
			count++;
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
		return null;
	}

	private double computeErrorRate(double e, double z, double n) {
		double denominator = 1 + z * z / n;
		double temp = e / n - e * e / n + z * z / (4 * n * n);
		double numerator = e + z * z / (2 * n) - z * Math.sqrt(temp);

		return numerator / denominator;
	}
}
