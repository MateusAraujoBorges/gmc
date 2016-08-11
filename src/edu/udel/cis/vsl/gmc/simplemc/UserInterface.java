package edu.udel.cis.vsl.gmc.simplemc;

import edu.udel.cis.vsl.gmc.concurrent.ConcurrentDfsSearcher;

public class UserInterface {
	public static void main(String[] args) {
		long startTime, endTime;
		Enabler enabler = new Enabler();
		StateManager manager = new StateManager();
		StatePredicate predicate = new StatePredicate();
//		DfsSearcher<State, Transition, TransitionSequence> dfsSearch 
//			= new DfsSearcher<>(enabler, manager, predicate);
		
		State initState = new State(0);
//		startTime = System.currentTimeMillis();
//		dfsSearch.search(initState);
//		endTime = System.currentTimeMillis();
//		System.out.println("sequential time: "+ (endTime - startTime));
//		
		ConcurrentDfsSearcher<State, Transition, TransitionSequence>
			cdfs = new ConcurrentDfsSearcher<>(enabler, manager, predicate, 3);
		startTime = System.currentTimeMillis();
		cdfs.search(initState);
		endTime = System.currentTimeMillis();
		System.out.println("concurrent time:" + (endTime - startTime));
	}
}
