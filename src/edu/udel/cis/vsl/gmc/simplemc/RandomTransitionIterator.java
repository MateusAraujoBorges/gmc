package edu.udel.cis.vsl.gmc.simplemc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.udel.cis.vsl.gmc.TransitionIterator;
import edu.udel.cis.vsl.gmc.TransitionSetIF;

public class RandomTransitionIterator extends TransitionIterator<State, Transition>{
	
	private TransitionSequence transitionSet;
	private List<Integer> indexes;
	private Random r;
	
	public RandomTransitionIterator(TransitionSequence transitionSet) {
		int size = transitionSet.getSize();
		
		this.transitionSet = transitionSet;
		r = new Random();
		indexes = new ArrayList<Integer>();
		for(int i=0; i<size; i++){
			indexes.add(i);
		}
	}

	@Override
	public boolean hasNext() {
		return indexes.size() > 0;
	}

	@Override
	public Transition next() {
		int randomIndex = indexes.remove(r.nextInt(indexes.size()));
		
		return transitionSet.get(randomIndex);
	}

	@Override
	public TransitionSetIF<State, Transition> getTransitionSet() {
		
		return transitionSet;
	}

	@Override
	public Transition peek() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int numConsumed() {
		// TODO Auto-generated method stub
		return 0;
	}
}
