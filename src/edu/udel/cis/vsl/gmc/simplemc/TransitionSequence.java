package edu.udel.cis.vsl.gmc.simplemc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransitionSequence {
	private State state;
	private List<Transition> transitions;
	
	public TransitionSequence(State state){
		this.state = state;
		this.transitions = new ArrayList<>();
	}
	
	public State state() {
		return state;
	}
	
	public boolean hasNext(){
		return transitions.size() > 0;
	}
	
	public Transition next(){
		return transitions.remove(transitions.size() - 1);
	}
	
	public int size(){
		return transitions.size();
	}
	
	public Transition randomNext(){
		int size = transitions.size();
		Random r = new Random();
		int index = r.nextInt(size);
		
		return transitions.remove(index);
	}
	
	public Transition[] randomPeekN(int n){
		Transition[] ts = new Transition[n];
		int index = 0;
		
		while(index < n){
			ts[index++] = this.randomNext();
		}
		return ts;
	}
	
	public void addTransitions(Transition... transitions){
		for(Transition t : transitions){
			this.transitions.add(t);
		}
	}
	
	public List<Transition> getTransitions(){
		return transitions;
	}
	
	public void addTransitionSequence(TransitionSequence transitionSequence){
		this.transitions.addAll(transitionSequence.getTransitions());
	}
}
