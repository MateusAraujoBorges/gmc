package edu.udel.cis.vsl.gcmc;

import java.util.Iterator;

import edu.udel.cis.vsl.gcmc.util.Pair;
import edu.udel.cis.vsl.gmc.intermediate.SequentialEnablerIF;

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
	 * transitionSequence.
	 * 
	 * Note that TRANSITIONSEQUENCE should store Pair<TRANSITION, STATE> because
	 * of the design of TransitionSelector. When picking a successor,
	 * TransitionSelector will try to pick one randomly from those who has not
	 * been visited by any thread, if there are not any, then randomly pick one
	 * from those who has been ever visited. During the process, all the
	 * successor states will be computed, then store the pair will avoid
	 * repetitive work. But this design is not necessary it the algorithm just
	 * randomly pick a successor regardless of whether the successor state has
	 * ever been visited.
 	 * 
	 * @return true if ts contains t.
	 */
	boolean removeTransition(int id, TRANSITIONSEQUENCE ts, Pair<TRANSITION, STATE> transition);

	/**
	 * Add TRANSITION t into TRANSITIONSEQUENCE ts
	 * 
	 * I need this method because after I remove a TRANSITION from
	 * TRANSITIONSEQUENCE, copy the stack and then spawn the new thread, I need
	 * to restore the TRANSITIONSEQUENCE.
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
	 * 
	 * Note: this method is not necessary, it is here for efficiency purpose.
	 * With this method, the algorithm does not need to compute ample set again
	 * when checking stack proviso.
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
