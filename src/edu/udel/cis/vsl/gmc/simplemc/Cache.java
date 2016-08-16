package edu.udel.cis.vsl.gmc.simplemc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
	public static Map<State, TransitionSequence> ampleSetCache = new ConcurrentHashMap<>(); 
	public static Map<State, TransitionSequence> notInAmpleSetCache = new ConcurrentHashMap<>();
	
	// TODO using hashtable, then no need to synchronize?
	public static void addAmpleSetCache(State source, TransitionSequence ampleSet){
		ampleSetCache.put(source, ampleSet.clone());
	}
	
	public static void addNotInAmpleSetCache(State source, TransitionSequence notInAmpleSet){
		notInAmpleSetCache.put(source, notInAmpleSet.clone());
	}
	
	public static TransitionSequence getAmpleSet(State state){
		TransitionSequence ampleSet = ampleSetCache.get(state);
		
		if(ampleSet != null)
			return ampleSet.clone();
		else return null;
	}
	
	public static TransitionSequence getNotInAmpleSet(State state){
		TransitionSequence notInAmpleSet = notInAmpleSetCache.get(state);
		
		if(notInAmpleSet != null)
			return notInAmpleSet.clone();
		else return null;
	}
}
