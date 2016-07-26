package edu.udel.cis.vsl.gcmc;

import java.util.Iterator;

import edu.udel.cis.vsl.gcmc.util.Pair;

/**
 * Note: Even though ConcurrentEnablerIF extends SequentialEnablerIF, but the
 * methods (hasNext, next, peek) will not be used in a concurrentDfsSearcher. I
 * think that will be fine since many methods can be reused.
 */
public interface ConcurrentEnablerIF<STATE, TRANSITION, TRANSITIONSEQUENCE>
		extends SequentialEnablerIF<STATE, TRANSITION, TRANSITIONSEQUENCE> {

	/**
	 * Return the size of a TRANSITIONSEQUENCE
	 * 
	 * I need this method because when I spawn new threads to work on different
	 * branches, I need to know the size of the transitionSequence.
	 * 
	 * @return the size of transition sequence
	 */
	int size(TRANSITIONSEQUENCE transitionSequence);

	/**
	 * Remove TRANSITION t from TRANSITIONSEQUENCE ts
	 * 
	 * I need this method since I need to remove a random transition from
	 * transitionSequence and then copy the stack and spawn new thread, but
	 * after that I need to restore the transitionSequence.
	 * 
	 * @return true if ts contains t.
	 */
	boolean removeTransition(int id, TRANSITIONSEQUENCE ts, Pair<TRANSITION, STATE> transition);

	/**
	 * Add TRANSITION t into TRANSITIONSEQUENCE ts
	 * 
	 * I need this method because when I spawn a new
	 * 
	 * @return true if transitionSequence does not already contain transition.
	 */
	boolean addTransition(TRANSITIONSEQUENCE transitionSequence, Pair<TRANSITION, STATE> transition);

	/**
	 * Add a successor state into TransitionSequence.
	 *
	 * Store the successor states in Transition Sequence such that when I apply
	 * the stack proviso (all successor needs to on the stack), I don't need to
	 * get those successor states again.
	 */
	void addSuccessor(TRANSITIONSEQUENCE transitionSequence, STATE state);

	/**
	 * @return the iterator to iterate the successor Set in TransitionSequence.
	 * 
	 */
	Iterator<STATE> successorIterator(TRANSITIONSEQUENCE transitionSequence);

	/**
	 * @return the iterator of transitionSequence
	 * 
	 *         I need to iterate the transitionSequence to initialize the
	 *         TransitionSelector. The original interface (hasNext(), next(),
	 *         peek()) can not to iterate TransitionSequence without modifying
	 *         the TransitionSequence.
	 */
	Iterator<Pair<TRANSITION, STATE>> iterator(TRANSITIONSEQUENCE transitionSequence);
}
