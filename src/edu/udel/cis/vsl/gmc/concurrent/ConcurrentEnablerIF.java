package edu.udel.cis.vsl.gmc.concurrent;

public interface ConcurrentEnablerIF<STATE, TRANSITION, TRANSITIONSEQUENCE> {

	/**
	 * Get the ample set of a given STATE.
	 * 
	 * @param source
	 *            source state
	 * @return the ample set of a given STATE
	 */
	TRANSITIONSEQUENCE enabledTransitions(STATE source);
	
	/**
	 * Get the source STATE of a TRANSITIONSEQUENCE.
	 * 
	 * @param transitionSequence
	 * 
	 * @return the source STATE of a TRANSITIONSEQUENCE.
	 */
	STATE source(TRANSITIONSEQUENCE transitionSequence);

	/**
	 * Return the size of a TRANSITIONSEQUENCE
	 * 
	 * I need to know the size of a TRANSITIONSEQUENCE when I spawn new threads.
	 * I also need to know the size of the notInAmpleSet when I check the stack
	 * proviso.
	 * 
	 * @return the size of TRANSITIONSEQUENCE
	 */
	int size(TRANSITIONSEQUENCE transitionSequence);

	/**
	 * Tell whether a TRANSITIONSEQUENCE contains at least one TRANSITION.
	 * 
	 * @param transitionSequence
	 * 
	 * @return true iff there are at least one TRANSITION in the
	 *         TRANSITIONSEQUENCE.
	 */
	boolean hasNext(TRANSITIONSEQUENCE transitionSequence);

	/**
	 * Get the next TRANSITION randomly.
	 * 
	 * @param transitionSequence
	 * 
	 * @return the next TRANSITION randomly.
	 */
	TRANSITION randomNext(TRANSITIONSEQUENCE transitionSequence);
	
	/**
	 * Peek n TRANSITIONs form a give TRANSITIONSEQUENCE.
	 * 
	 * @param transitionSequence
	 * 
	 * @return an array that contains the n TRANSITIONs that are peeked from a given TRANSITIONSEQUENCE.
	 */
	TRANSITION[] randomPeekN(TRANSITIONSEQUENCE transitionSequence, int n);

	/**
	 * Get the transitions that are not in the ample set of a give STATE.
	 * 
	 * @param s
	 *            source STATE
	 * @return transitions that are not in the ample set of a give STATE.
	 */
	TRANSITIONSEQUENCE transitionsNotInAmpleSet(STATE s);

	/**
	 * Get the next TRANSITION.
	 * 
	 * @param transitionSequence
	 * 
	 * @return the next TRANSIRION.
	 */
	TRANSITION next(TRANSITIONSEQUENCE transitionSequence);

	/**
	 * Put those TRANSITIONS that are not in the ample set into the
	 * TRANSITIONSEQUENCE.
	 * 
	 * @param transitionSequence
	 */
	void expandToFull(TRANSITIONSEQUENCE transitionSequence);
	
	TRANSITIONSEQUENCE clone(TRANSITIONSEQUENCE transitionSequence);
}
