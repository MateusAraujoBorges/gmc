 package edu.udel.cis.vsl.gmc.simplemc;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.udel.cis.vsl.gcmc.ConcurrentEnablerIF;
import edu.udel.cis.vsl.gcmc.util.Lock;
import edu.udel.cis.vsl.gcmc.util.Pair;

public class Enabler implements ConcurrentEnablerIF<State, Transition, TransitionSequence>{

	@Override
	public TransitionSequence enabledTransitions(State source) {
		long time = System.currentTimeMillis();
		TransitionSequence transitionSequence = new TransitionSequence(source);
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<Pair<Transition, State>> selection = new LinkedList<>();
		
		selection.add(new Pair<Transition, State>(new Transition((int)time%2 + 1), null));
		selection.add(new Pair<Transition, State>(new Transition((int)time%2*-1 - 1), null));
		transitionSequence.addSelection(selection);
		
		List<Pair<Transition, State>> notInAmpleSet = new LinkedList<>();
		notInAmpleSet.add(new Pair<Transition, State>(new Transition(3), null));
		notInAmpleSet.add(new Pair<Transition, State>(new Transition(-3), null));
		transitionSequence.addNotInAmpleSet(notInAmpleSet);
		
		return transitionSequence;
	}

	@Override
	public State source(TransitionSequence sequence) {
		return sequence.state();
	}

	@Override
	public boolean hasNext(TransitionSequence sequence) {
		// method only for sequential dfs
		return false;
	}

	@Override
	public Transition next(TransitionSequence sequence) {
		// method only for sequential dfs
		return null;
	}

	@Override
	public Transition peek(TransitionSequence sequence) {
		// method only for sequential dfs
		return null;
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
	public boolean removeTransition(int id, TransitionSequence ts, Pair<Transition, State> p) {
		synchronized (Lock.printLock) {
			int temp = id;
			while(temp > 1){
				System.out.print("                  ");
				temp--;
			}
			System.out.println("transition:" + p.getLeft().getOffset());
		}
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

	@Override
	public void addSuccessor(TransitionSequence transitionSequence, State state) {
		transitionSequence.addSuccessor(state);
	}

	@Override
	public Iterator<State> successorIterator(TransitionSequence transitionSequence) {
		return transitionSequence.successorIter();
	}
}
