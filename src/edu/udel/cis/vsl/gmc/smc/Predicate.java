package edu.udel.cis.vsl.gmc.smc;

import java.util.Collection;
import java.util.LinkedList;

import edu.udel.cis.vsl.gmc.seq.StatePredicateIF;

/**
 * The predicate used for detecting violation {@link State} defined in the given
 * list <code>states</code>. <br>
 * 
 * @author Wenhao Wu (wuwenhao@udel.edu)
 *
 */
public class Predicate implements StatePredicateIF<State> {
	/**
	 * The violation state array.<br>
	 * If any {@link State} is in this array, that state is considered as a
	 * violation.
	 */
	private Collection<Integer> violationValues = new LinkedList<>();

	/**
	 * Temporarily store the latest detected violation state for explaining.
	 */
	private State violatedState;

	public Predicate(int... stateValues) {
		for (int x : stateValues)
			violationValues.add(x);
	}

	/**
	 * {@inheritDoc}<br>
	 * <p>
	 * For the Violation State Predicate, if the given <code>state<state> is in
	 * the field <code>violationStates</code>, this function will return
	 * <code>true</code> (which will make the checker report a violation), else
	 * <code>false<code>.
	 * </p>
	 */
	@Override
	public boolean holdsAt(State state) {
		boolean result = violationValues.contains(state.getValue());

		if (result)
			this.violatedState = state;
		return result;
	}

	@Override
	public String explanation() {
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append("Violation type: ");
		sBuilder.append(Predicate.class.getName());
		sBuilder.append("\n");
		sBuilder.append(violatedState);
		sBuilder.append(" is in the violation state list: \n\t{");
		for (int val : violationValues) {
			sBuilder.append("<");
			sBuilder.append(val);
			sBuilder.append(">,");
		}
		sBuilder.append("}");
		return sBuilder.toString();
	}

	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append("'");
		sBuilder.append(Predicate.class.getName());
		sBuilder.append("'");
		return sBuilder.toString();
	}
}
