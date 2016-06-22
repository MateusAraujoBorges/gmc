package edu.udel.cis.vsl.gmc.simplemc;

import edu.udel.cis.vsl.gmc.ConcurrentDfsSearcher;
import edu.udel.cis.vsl.gmc.DfsSearcher;

public class UserInterface {
	public static void main(String[] args) {
		long startTime, endTime;
		Enabler enabler = new Enabler();
		StateManager manager = new StateManager();
		StatePredicate predicate = new StatePredicate();
		DfsSearcher<State, Transition, TransitionSequence> dfsSearch 
			= new DfsSearcher<>(enabler, manager, predicate);
		
		State initState = new State(0);
//		startTime = System.currentTimeMillis();
//		dfsSearch.search(initState);
//		endTime = System.currentTimeMillis();
//		System.out.println("sequential time: "+ (endTime - startTime));
//		
		ConcurrentDfsSearcher<State, Transition, TransitionSequence>
			cdfs = new ConcurrentDfsSearcher<>(enabler, manager, predicate, 5);
		startTime = System.currentTimeMillis();
		cdfs.search(initState);
		endTime = System.currentTimeMillis();
		System.out.println("concurrent time:" + (endTime - startTime));
	}
}
