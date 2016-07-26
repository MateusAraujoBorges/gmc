package edu.udel.cis.vsl.gcmc;

import edu.udel.cis.vsl.gmc.StateManagerIF;

/**
 * A StateManagerIF provides part of a generic interface to a state-transition
 * system. The primary method is {@link #nextState}, which, given a state and a
 * transition, returns the "next state", i.e., the state which results from
 * executing the transition from the given state. Other methods are provided
 * that are needed specifically for depth-first search, including methods to
 * mark a state as "seen before", and to make a state as
 * "currently on (or off) the stack". Still other methods are provided for
 * printing information about states.
 * 
 * @author Stephen F. Siegel
 * 
 * @param <STATE>
 *            the type used to represent states in the state-transition system
 *            being analyzed
 * @param <TRANSITION>
 *            the type used to represent transitions in the state-transition
 *            system being analyzed
 */
public interface ConcurrentStateManagerIF<STATE, TRANSITION> extends StateManagerIF<STATE, TRANSITION>{

	/**
	 * Indicate whether a STATE is on the stack of the thread with certain id.
	 * 
	 * @return true if state is on the stack of the thread(id).
	 */
	boolean onStack(STATE state, int id);

	/**
	 * Indicate whether a STATE is fully explored (all its descendants have been
	 * visited).
	 * 
	 * @return true if all state is fully explored.
	 */
	boolean fullyExplored(STATE state);
	
	/**
	 * Set "fullyExplored" field of a STATE to a certain boolean value
	 */
	void setFullyExplored(STATE state, boolean value);
	
	void setOnStack(STATE state, int id, boolean value);
	
	void setInviolableCAS(STATE state, int value);
	
	int isInviolable(STATE state);
	
	void setSeen(int id, STATE state, boolean value);
}