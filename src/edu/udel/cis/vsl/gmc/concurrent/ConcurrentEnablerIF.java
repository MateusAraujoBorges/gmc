package edu.udel.cis.vsl.gmc.concurrent;

/**
 * <p>
 * An ConcurrentEnablerIF tells you which transitions should be explored from a
 * given state. An ConcurrentEnablerIF should be shared by all threads, so all
 * methods in this interface should be thread safe.
 * </p>
 * <p>
 * The result {@link TransitionSet} may be cached to improve performance.
 * </p>
 * 
 * @author yanyihao
 *
 * @param <STATE>
 * @param <TRANSITION>
 */
public interface ConcurrentEnablerIF<STATE, TRANSITION> {

	/**
	 * Get the ample set of a given STATE.
	 * 
	 * @param source
	 *            source state
	 * @return the ample set of a given STATE
	 */
	TransitionSet<STATE, TRANSITION> ampleSet(STATE source);

	/**
	 * Get the TRANSIRTIONs that are not in the ample set of a given STATE.
	 * 
	 * @param s
	 *            source STATE
	 * @return TRANSITIONs that are not in the ample set of a give STATE.
	 */
	TransitionSet<STATE, TRANSITION> ampleSetComplement(STATE s);

	/**
	 * Get all the TRANSIRTIONs that are enabled at a STATE
	 * 
	 * @param state
	 *            source STATE
	 * @return all the TRANSITIONs enabled by a given STATE
	 */
	TransitionSet<STATE, TRANSITION> allEnabledTransitions(STATE state);
}
