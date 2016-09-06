package edu.udel.cis.vsl.gmc.concurrent;

import java.util.Iterator;

public interface TransitionIterator<STATE, TRANSITION> extends Iterator<TRANSITION>{
	TransitionSet<STATE, TRANSITION> getTransitionSet();
}
