package edu.udel.cis.vsl.gmc;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class implements the iterator which iterates a {@link TransitionSetIF}.
 * 
 * @author Yihao Yan (yanyihao)
 */
public interface TransitionIteratorIF<STATE, TRANSITION>
		extends
			Iterator<TRANSITION> {

	/**
	 * @return the {@link TransitionSetIF} that is iterated.
	 */
	TransitionSetIF<STATE, TRANSITION> getTransitionSet();

	/**
	 * @return the first element in the {@link TransitionSetIF} without removing
	 *         it.
	 * @throws NoSuchElementException
	 *             if this iterator has no next element.
	 */
	TRANSITION peek();

	/**
	 * @return the number of transitions in the {@link TransitionSetIF} that
	 *         have been consumed.
	 */
	int numConsumed();

}
