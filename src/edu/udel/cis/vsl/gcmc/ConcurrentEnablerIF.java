package edu.udel.cis.vsl.gcmc;

import java.util.Iterator;

import edu.udel.cis.vsl.gcmc.util.Pair;

/**
 * An EnablerIF tells you which transitions should be explored from a given
 * state. It might need to know things about the state of the search (such as
 * what states are currently on the DFS stack). Such information can be provided
 * at creation.
 * 
 * @author Stephen F. Siegel, University of Delaware
 */
public interface ConcurrentEnablerIF<STATE, TRANSITION, TRANSITIONSEQUENCE>
		extends SequentialEnablerIF<STATE, TRANSITION, TRANSITIONSEQUENCE>{

	/**
	 * Return the size of a TRANSITIONSEQUENCE
	 * 
	 * @return the size of transition sequence
	 */
	int size(TRANSITIONSEQUENCE transitionSequence);
	
	/**
	 * Remove TRANSITION t from TRANSITIONSEQUENCE ts
	 * 
	 * @return true if ts contains t.
	 */
	boolean removeTransition(int id, TRANSITIONSEQUENCE ts, Pair<TRANSITION, STATE> transition);
	
	/**
	 * Add TRANSITION t to TRANSITIONSEQUENCE ts
	 * 
	 * @return true if transitionSequence does not already contain transition.
	 */
	boolean addTransition(TRANSITIONSEQUENCE transitionSequence, Pair<TRANSITION, STATE> transition);
	
	void addSuccessor(TRANSITIONSEQUENCE transitionSequence, STATE state);
	
	Iterator<STATE> successorIterator(TRANSITIONSEQUENCE transitionSequence);
	
	/**
	 * @return the iterator of transitionSequence
	 */
	Iterator<Pair<TRANSITION, STATE>> iterator(TRANSITIONSEQUENCE transitionSequence);
}
