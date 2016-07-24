package edu.udel.cis.vsl.gmc.simplemc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.udel.cis.vsl.gcmc.util.Pair;

public class TransitionSequence {
	private Set<Pair<Transition, State>> selection;
	private Set<Pair<Transition, State>> notInAmpleSet;
	private State state;
	
	public TransitionSequence(State state){
		this.state = state;
		selection = new HashSet<>();
		notInAmpleSet = new HashSet<>();
	}
	
	public State state() {
		return state;
	}
	
	public boolean addSelection(Collection<Pair<Transition, State>> pairs) {
		return this.selection.addAll(pairs);
	}
	
	public boolean addNotInAmpleSet(Collection<Pair<Transition, State>> pairs){
		return this.notInAmpleSet.addAll(pairs);
	}
	
	public int size() {
		return this.selection.size();
	}
	
	public boolean isEmpty() {
		return this.selection.isEmpty();
	}
	
	public Iterator<Pair<Transition, State>> iterator(){
		return selection.iterator();
	}
	
	public Collection<Pair<Transition, State>> transitions() {
		return selection;
	}
	
	public boolean remove(Pair<Transition, State> p){
		return selection.remove(p);
	}
	
	public boolean add(Pair<Transition, State> p){
		return this.selection.add(p);
	}
	
	public boolean isNotInAmpleSetEmpty(){
		return notInAmpleSet.isEmpty();
	}
	
	public void putAllInSelection(){
		selection.addAll(notInAmpleSet);
		notInAmpleSet.clear();
	}
}
