package edu.udel.cis.vsl.gmc.concurrent;

/**
 * TransitionSet is a container of TRANSITIONS, it should be iterable and
 * immutable.
 * 
 * @author yanyihao
 *
 * @param <STATE>
 * @param <TRANSITION>
 */
public interface TransitionSet<STATE, TRANSITION> extends Iterable<TRANSITION> {

	/**
	 * @return the source STATE
	 */
	STATE source();

	/**
	 * @return a random Iterator which will iterate the transitionSet in a
	 *         random manner.
	 */
	TransitionIterator<STATE, TRANSITION> randomIterator();
}
