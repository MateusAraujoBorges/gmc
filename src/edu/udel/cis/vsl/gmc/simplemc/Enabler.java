 package edu.udel.cis.vsl.gmc.simplemc;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.udel.cis.vsl.gcmc.ConcurrentEnablerIF;

public class Enabler implements ConcurrentEnablerIF<State, Transition, TransitionSequence>{

	@Override
	public TransitionSequence enabledTransitions(State source) {
		TransitionSequence transitionSequence = new TransitionSequence(source);
		Random rand = new Random();
		// 1 to 3
		int offset1 = rand.nextInt(3)+1;
		// -1 to -3
		int offset2 = rand.nextInt(3)-3;
		int offset3 = 100;
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<Transition> transitions = new LinkedList<>();
		
//		if(rand.nextInt(6) % 2 == 0){
//			System.out.println("offset1:"+offset1);
//			System.out.println("offset2:"+offset2);
			transitions.add(new Transition(offset1));
			transitions.add(new Transition(offset2));
			transitions.add(new Transition(offset3));
//		}else{
//			System.out.println("offset2:"+offset2);
//			System.out.println("offset1:"+offset1);
//			transitions.add(new Transition(offset2));
//			transitions.add(new Transition(offset1));
//		}
		transitionSequence.addAll(transitions);
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
	public boolean removeTransition(TransitionSequence ts, Transition t) {
		return ts.remove(t);
	}

	@Override
	public boolean addTransition(TransitionSequence transitionSequence, Transition transition) {
		return transitionSequence.add(transition);
	}

	@Override
	public TransitionSequence enabledTransitionsPOR(State state) {
		TransitionSequence transitionSequence = new TransitionSequence(state);
//		Random rand = new Random();
		// 1 to 3
		int offset1 = 1;
		// -1 to -3
		int offset2 = -3;
		int offset3 = 100;
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<Transition> transitions = new LinkedList<>();
		
//		if(rand.nextInt(6) % 2 == 0){
//			System.out.println("offset1:"+offset1);
//			System.out.println("offset2:"+offset2);
			transitions.add(new Transition(offset1));
			transitions.add(new Transition(offset2));
			transitions.add(new Transition(offset3));
//		}else{
//			System.out.println("offset2:"+offset2);
//			System.out.println("offset1:"+offset1);
//			transitions.add(new Transition(offset2));
//			transitions.add(new Transition(offset1));
//		}
		transitionSequence.addAll(transitions);
		return transitionSequence;
	}

	@Override
	public boolean contains(TransitionSequence transitionSequence1, TransitionSequence transitionSequence2) {
		return transitionSequence1.containsAll(transitionSequence2)
				&& transitionSequence1.size() > transitionSequence2.size();
	}

	@Override
	public boolean removeAll(TransitionSequence transitionSequence1,
			TransitionSequence transitionSequence2) {
		return transitionSequence1.removeAll(transitionSequence2);
	}

	@Override
	public boolean addTransitionSequence(TransitionSequence transitionSequence1, TransitionSequence transitionSequence2) {
		return transitionSequence1.addAll(transitionSequence2);
	}

	@Override
	public Iterator<Transition> iterator(TransitionSequence transitionSequence) {
		return transitionSequence.iterator();
	}
}
