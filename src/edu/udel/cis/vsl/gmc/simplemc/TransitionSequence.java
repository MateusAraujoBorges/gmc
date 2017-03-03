package edu.udel.cis.vsl.gmc.simplemc;

import java.util.ArrayList;
import java.util.List;

import edu.udel.cis.vsl.gmc.TransitionIteratorIF;
import edu.udel.cis.vsl.gmc.TransitionSetIF;

public class TransitionSequence implements TransitionSetIF<State, Transition> {
	private State state;
	private List<Transition> transitions;

	public TransitionSequence(State state) {
		this.state = state;
		this.transitions = new ArrayList<>();
	}
	
	public void addTransitions(Transition... transitions) {
		for (Transition t : transitions) {
			this.transitions.add(t);
		}
	}
	
	public int getSize(){
		return transitions.size();
	}
	
	public Transition get(int index){
		return transitions.get(index);
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public void addTransitionSequence(TransitionSequence transitionSequence) {
		this.transitions.addAll(transitionSequence.getTransitions());
	}

	@Override
	public State source() {
		return state;
	}

	@Override
	public TransitionIteratorIF<State, Transition> randomIterator() {
		
		return new RandomTransitionIterator(this);
	}

	@Override
	public TransitionIteratorIF<State, Transition> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasMultiple() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
}
