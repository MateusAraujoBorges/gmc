 package edu.udel.cis.vsl.gmc.simplemc;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.udel.cis.vsl.gmc.EnablerIF;

public class Enabler implements EnablerIF<State, Transition, TransitionSequence>{

	@Override
	public TransitionSequence enabledTransitions(State source) {
		TransitionSequence transitionSequence = new TransitionSequence(source);
		Random rand = new Random();
		// 1 to 3
		int offset1 = rand.nextInt(3)+1;
		// -1 to -3
		int offset2 = rand.nextInt(3)-3;
		
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

	@Override
	public boolean hasNext(TransitionSequence sequence) {
		return ! (sequence.isEmpty());
	}

	@Override
	public Transition next(TransitionSequence sequence) {
		return sequence.pop();
	}

	@Override
	public Transition peek(TransitionSequence sequence) {
		return sequence.peek();
	}

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int numRemoved(TransitionSequence sequence) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
