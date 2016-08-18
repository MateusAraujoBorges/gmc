package edu.udel.cis.vsl.gmc.simplemc;

import edu.udel.cis.vsl.gmc.concurrent.ConcurrentDfsSearcher;
import edu.udel.cis.vsl.gmc.concurrent.util.Log;

public class UserInterface {
	public static void main(String[] args) {
		long startTime, endTime;
		Enabler enabler = new Enabler();
		StateManager manager = new StateManager();
		StatePredicate predicate = new StatePredicate();
		State initState = StateFactory.getState(0);
		ConcurrentDfsSearcher<State, Transition, TransitionSequence>
			cdfs = new ConcurrentDfsSearcher<>(enabler, manager, predicate, 1);
		
		startTime = System.currentTimeMillis();
		cdfs.search(initState);
		endTime = System.currentTimeMillis();
		System.out.println("concurrent time:" + (endTime - startTime));
		
		System.out.println("log size:"+ Log.transactions.size());
		Log.analyzeLog();
	}
}
