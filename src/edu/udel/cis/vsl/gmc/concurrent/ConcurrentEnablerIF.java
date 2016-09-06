package edu.udel.cis.vsl.gmc.concurrent;

public interface ConcurrentEnablerIF<STATE, TRANSITION> {

	/**
	 * Get the ample set of a given STATE.
	 * 
	 * @param source
	 *            source state
	 * @return the ample set of a given STATE
	 */
	TransitionSet<STATE, TRANSITION> enabledTransitions(STATE source);

	/**
	 * Get the transitions that are not in the ample set of a given STATE.
	 * 
	 * @param s
	 *            source STATE
	 * @return transitions that are not in the ample set of a give STATE.
	 */
	TransitionSet<STATE, TRANSITION> ampleSetComplement(STATE s);
}
