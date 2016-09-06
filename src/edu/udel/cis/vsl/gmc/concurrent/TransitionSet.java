package edu.udel.cis.vsl.gmc.concurrent;

public interface TransitionSet<STATE, TRANSITION> {
	STATE source();
	
	TransitionIterator<STATE, TRANSITION> randomIterator();
}
