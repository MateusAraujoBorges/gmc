 package edu.udel.cis.vsl.gmc.simplemc;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.udel.cis.vsl.gcmc.ConcurrentEnablerIF;
import edu.udel.cis.vsl.gcmc.util.Pair;

public class Enabler implements ConcurrentEnablerIF<State, Transition, TransitionSequence>{

	@Override
	public TransitionSequence enabledTransitions(State source) {
		TransitionSequence transitionSequence = new TransitionSequence(source);
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<Pair<Transition, State>> selection = new LinkedList<>();
		
//		if(rand.nextInt(6) % 2 == 0){
//			System.out.println("offset1:"+offset1);
//			System.out.println("offset2:"+offset2);
		selection.add(new Pair<Transition, State>(new Transition(3), null));
		selection.add(new Pair<Transition, State>(new Transition(6), null));
		selection.add(new Pair<Transition, State>(new Transition(100), null));
//		}else{
//			System.out.println("offset2:"+offset2);
//			System.out.println("offset1:"+offset1);
//			transitions.add(new Transition(offset2));
//			transitions.add(new Transition(offset1));
//		}
		
		transitionSequence.addSelection(selection);
		
		List<Pair<Transition, State>> notInAmpleSet = new LinkedList<>();
		notInAmpleSet.add(new Pair<Transition, State>(new Transition(50), null));
		notInAmpleSet.add(new Pair<Transition, State>(new Transition(-50), null));
		transitionSequence.addNotInAmpleSet(notInAmpleSet);
		
		return transitionSequence;
	}

	@Override
	public State source(TransitionSequence sequence) {
		return sequence.state();
	}

//	@Override
//	public boolean hasNext(TransitionSequence sequence) {
//		return ! (sequence.isEmpty());
//	}

//	@Override
//	public Transition next(TransitionSequence sequence) {
//		return sequence.pop();
//	}
//
//	@Override
//	public Transition peek(TransitionSequence sequence) {
//		return sequence.peek();
//	}

	@Override
	public void print(PrintStream out, TransitionSequence sequence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printRemaining(PrintStream out, TransitionSequence sequence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDebugging(boolean value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean debugging() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setDebugOut(PrintStream out) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PrintStream getDebugOut() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printFirstTransition(PrintStream out, TransitionSequence sequence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasMultiple(TransitionSequence sequence) {
		return sequence.size() > 1;
	}

	@Override
	public int numRemoved(TransitionSequence sequence) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int size(TransitionSequence transitionSequence) {
		return transitionSequence.size();
	}
	
	@Override
	public boolean removeTransition(TransitionSequence ts, Pair<Transition, State> p) {
		return ts.remove(p);
	}

	@Override
	public boolean addTransition(TransitionSequence transitionSequence, Pair<Transition, State> p) {
		return transitionSequence.add(p);
	}
	
	@Override
	public Iterator<Pair<Transition, State>> iterator(TransitionSequence transitionSequence) {
		return transitionSequence.iterator();
	}

	@Override
	public boolean fullyExpanded(TransitionSequence transitionSequence) {
		return transitionSequence.isNotInAmpleSetEmpty();
	}

	@Override
	public void expand(TransitionSequence transitionSequence) {
		transitionSequence.putAllInSelection();
	}
}
