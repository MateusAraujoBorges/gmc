package edu.udel.cis.vsl.gmc.concurrent;

import java.util.Iterator;

/**
 * This class implements the iterattor which iterates a
 * {@link TransitionSet}.
 * 
 * @author yanyihao
 *
 * @param <STATE>
 * @param <TRANSITION>
 */
public interface TransitionIterator<STATE, TRANSITION> extends Iterator<TRANSITION> {
	
	/**
	 * @return the {@link TransitionSet} that is iterated.
	 */
	TransitionSet<STATE, TRANSITION> getTransitionSet();
}
