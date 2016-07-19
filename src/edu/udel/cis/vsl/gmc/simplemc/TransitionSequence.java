package edu.udel.cis.vsl.gmc.simplemc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TransitionSequence {
	private Set<Transition> transitions;
	private State state;
	
	public TransitionSequence(State state){
		this.state = state;
		transitions = new HashSet<>();
	}
	
	public State state() {
		return state;
	}
	
	public boolean addAll(TransitionSequence ts) {
		return this.transitions.addAll(ts.transitions());
	}
	
	public boolean addAll(Collection<Transition> transitions){
		return this.transitions.addAll(transitions);
	}
	
	public int size() {
		return this.transitions.size();
	}
	
	public boolean isEmpty() {
		return this.transitions.isEmpty();
	}
	
	public Iterator<Transition> iterator(){
		return transitions.iterator();
	}
	
	public Collection<Transition> transitions() {
		return transitions;
	}
	
	public boolean remove(Transition t){
		return transitions.remove(t);
	}
	
	public boolean add(Transition transition){
		return this.transitions.add(transition);
	}
	
	public boolean containsAll(TransitionSequence transitionSequence){
		return transitions.containsAll(transitionSequence.transitions());
	}
	
	public boolean removeAll(TransitionSequence transitionSequence){
		return transitions.removeAll(transitionSequence.transitions());
	}
}
