package edu.udel.cis.vsl.gmc.concurrent;

import edu.udel.cis.vsl.gmc.TraceStepIF;

/**
 * <p>
 * A ConcurrentStateManagerIF provides part of a generic interface to a
 * state-transition system. There are two major responsibilities of a
 * ConcurrentStateManagerIF :
 * <ul>
 * <li>1. Method {@link #nextState(int, int, Object, Object)} return the next
 * STATE given a source STATE and a TRANSITION.</li>
 * <li>1. Operations on STATE.</li>
 * </ul>
 * </p>
 * <p>
 * A ConcurrentStateManagerIF will be shared by all threads, therefore, all its
 * methods should be thread-safe.
 * </p>
 * <p>
 * The result of method {@link #nextState(int, int, Object, Object)} may be
 * cached to improve performance.
 * </p>
 * 
 * 
 * @author yanyihao
 *
 * @param <STATE>
 * @param <TRANSITION>
 */
public interface ConcurrentStateManagerIF<STATE, TRANSITION> {

	/**
	 * Given a state and a transition, returns the trace step after executing
	 * the transition at the given state. See {@link TraceStepIF}.
	 * 
	 * @param state
	 *            a state in the state transition system
	 * @param transition
	 *            an execution which is enabled at the given state
	 * @return the trace step after executing the transition at the given state.
	 */
	public TraceStepIF<TRANSITION, STATE> nextState(int tid,
			int transaction, STATE state, TRANSITION transition);

	/**
	 * Indicate whether a STATE is on the stack of the thread with certain id.
	 * 
	 * @return true if state is on the stack of the thread(id).
	 */
	boolean onStack(STATE state, int tid);

	/**
	 * Indicate whether a STATE is fully explored (all its descendants have been
	 * visited). This is the same with telling whether a state is colored 'blue'
	 * in algorithm2 in <a href=
	 * "http://link.springer.com/chapter/10.1007%2F978-3-319-13338-6_20">Larrman
	 * 's paper</a>.
	 * 
	 * @return true if all state is fully explored.
	 */
	boolean fullyExplored(STATE state);

	/**
	 * Set "fullyExplored" field of a STATE to a certain boolean value.
	 */
	void setFullyExplored(STATE state, boolean value);

	/**
	 * Set the onStack field of a state.
	 * 
	 * @param state
	 *            A state in the STATE-TRANSITION system.
	 * @param id
	 *            The if of the Thread
	 * @param value
	 */
	void setOnStack(STATE state, int tid, boolean value);

	/**
	 * Set the proviso field of a state using atomic CAS operation.
	 * 
	 * @param state
	 * 
	 * @param value
	 */
	void setProvisoCAS(STATE state, ProvisoValue value);

	/**
	 * Tell whether a state satisfies the stack proviso or not.
	 * 
	 * @param state
	 *            A state in the STATE-TRANSITION system.
	 * @return
	 * 		<p>
	 *         {@link ProvisoValue.#UNKNOWN} if the proviso has not been checked
	 *         yet.
	 *         </p>
	 *         <p>
	 *         {@link ProvisoValue.#TRUE} if the proviso is satisfied.
	 *         </p>
	 *         <p>
	 *         {@link ProvisoValue.#FALSE} if the proviso is not satisfied
	 *         </p>
	 */
	ProvisoValue proviso(STATE state);
}