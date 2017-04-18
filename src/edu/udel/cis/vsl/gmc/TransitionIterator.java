package edu.udel.cis.vsl.gmc;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class implements the iterator which iterates a {@link TransitionSetIF}.
 * 
 * @author Yihao Yan (yanyihao)
 */
public abstract class TransitionIterator<STATE, TRANSITION>
		implements
			Iterator<TRANSITION> {
	/**
	 * If a successor is on stack, then it will have an index on the stack. This
	 * variable will store the minimum value among all the successors.
	 */
	private int minimumSuccessorStackIndex = Integer.MAX_VALUE;

	/**
	 * @return the {@link TransitionSetIF} that is iterated.
	 */
	public abstract TransitionSetIF<STATE, TRANSITION> getTransitionSet();

	/**
	 * @return the first element in the {@link TransitionSetIF} without removing
	 *         it.
	 * @throws NoSuchElementException
	 *             if this iterator has no next element.
	 */
	public abstract TRANSITION peek();

	/**
	 * @return the number of transitions in the {@link TransitionSetIF} that
	 *         have been consumed.
	 */
	public abstract int numConsumed();

	/**
	 * @return the minimum stack index among all the successors who are on
	 *         stack.
	 */
	public int getMinimumSuccessorStackIndex() {
		return minimumSuccessorStackIndex;
	}

	/**
	 * Update the minimum stack index to a smaller value.
	 * 
	 * @param minimumSuccessorStackIndex
	 *            The value that will be assigned to minimum stack index.
	 */
	public void setMinimumSuccessorStackIndex(int minimumSuccessorStackIndex) {
		this.minimumSuccessorStackIndex = minimumSuccessorStackIndex;
	}

}
