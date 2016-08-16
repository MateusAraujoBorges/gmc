package edu.udel.cis.vsl.gmc.concurrent;

import edu.udel.cis.vsl.gmc.TraceStepIF;

/**
 * Note methods setOnStack(STATE state, boolean value), onStack(STATE state)
 * will not be used in a concurrentDfsSearcher.
 */
public interface ConcurrentStateManagerIF<STATE, TRANSITION> {
	
	public TraceStepIF<TRANSITION, STATE> nextState(int threadId, int transaction, STATE state, TRANSITION transition);
	/**
	 * Indicate whether a STATE is on the stack of the thread with certain id.
	 * 
	 * @return true if state is on the stack of the thread(id).
	 */
	boolean onStack(STATE state, int id);

	/**
	 * Indicate whether a STATE is fully explored (all its descendants have been
	 * visited). This is the same with telling whether a state is colored
	 * 'blue'.
	 * 
	 * @return true if all state is fully explored.
	 */
	boolean fullyExplored(STATE state);

	/**
	 * Set "fullyExplored" field of a STATE to a certain boolean value.
	 * 
	 * This is the same with setting a state 'blue'.
	 */
	void setFullyExplored(STATE state, boolean value);

	/**
	 * Set the onStack field of a state.
	 */
	void setOnStack(STATE state, int id, boolean value);

	/**
	 * Set the inviolable field of a state using atomic CAS operation.
	 */
	void setInviolableCAS(STATE state, int value);

	/**
	 * Tell whether a state is inviolable or not.
	 */
	int isInviolable(STATE state);

	/**
	 * This method is used for debug and can be replaced by setSeen(STATE,
	 * boolean).
	 */
	void setSeen(int id, STATE state, boolean value);
}