package edu.udel.cis.vsl.gmc.simplemc;

import edu.udel.cis.vsl.gmc.concurrent.ConcurrentDfsSearcher;

public class UserInterface {
	public static void main(String[] args) {
		int paraLen = args.length;
		long startTime, endTime;
		Enabler enabler = new Enabler();
		StateManager manager = new StateManager();
		StatePredicate predicate = new StatePredicate();
		int[] initVector = new int[4];
		int numOfCore = paraLen == 0 ? 6 : Integer.parseInt(args[0]);
		
		initVector[0] = initVector[1] = initVector[2] = initVector[3] = 1;
		State initState = StateFactory.getState(initVector);
		ConcurrentDfsSearcher<State, Transition> cdfs = new ConcurrentDfsSearcher<>(enabler, manager, predicate, numOfCore);

		startTime = System.currentTimeMillis();
		cdfs.search(initState);
		endTime = System.currentTimeMillis();
		long timePassed = endTime - startTime;
		
//		if(paraLen == 0){
//			System.out.println("log size:" + Log.transactions.size());
//			Log.analyzeLog();
//		}
		System.out.println("concurrent time with "+ numOfCore +" cores: " + timePassed);
	}
}
