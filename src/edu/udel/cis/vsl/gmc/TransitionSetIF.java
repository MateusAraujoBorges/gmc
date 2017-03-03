package edu.udel.cis.vsl.gmc;

/**
 * TransitionSet is a container of TRANSITIONS, it should be iterable and
 * immutable.
 * 
 * @param <STATE>
 *            the type used to represent states in the state-transition system
 *            being analyzed
 * @param <TRANSITION>
 *            the type used to represent transitions in the state-transition
 *            system being analyzed
 * 
 * @author Yihao Yan (yanyihao)
 */
public interface TransitionSetIF<STATE, TRANSITION>
		extends
			Iterable<TRANSITION> {

	/**
	 * @return the source STATE
	 */
	STATE source();

	/**
	 * @return a iterator that will iterate the transition set in a fixed order.
	 */
	@Override
	TransitionIteratorIF<STATE, TRANSITION> iterator();

	// TODO get rid of it later.
	TransitionIteratorIF<STATE, TRANSITION> randomIterator();

	/**
	 * @return true iff this {@link TransitionSetIF} has more than one element.
	 */
	boolean hasMultiple();

	/**
	 * @return the size of this transition set.
	 */
	int size();
}
