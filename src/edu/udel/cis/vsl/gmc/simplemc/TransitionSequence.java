package edu.udel.cis.vsl.gmc.simplemc;

import java.util.Collection;
import java.util.LinkedList;

public class TransitionSequence {
	private LinkedList<Transition> transitions;
	private State state;
	
	public TransitionSequence(State state){
		this.state = state;
		transitions = new LinkedList<>();
	}
	
	public State state() {
		return state;
	}
	
	public void addAll(Collection<Transition> transitions) {
		this.transitions.addAll(transitions);
	}
	
	public int size() {
		return this.transitions.size();
	}
	
	public boolean isEmpty() {
		return this.transitions.isEmpty();
	}
	
	public Transition peek() {
		return this.transitions.peek();
	}
	
	public Transition pop() {
		return this.transitions.pop();
	}
	
	public Collection<Transition> transitions() {
		return transitions;
	}
}
