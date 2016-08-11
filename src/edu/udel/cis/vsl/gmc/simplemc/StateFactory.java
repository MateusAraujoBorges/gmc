package edu.udel.cis.vsl.gmc.simplemc;

import java.util.HashMap;
import java.util.Map;

public class StateFactory {
	private static Map<Integer, State> states = new HashMap<>();
	private static int stateNum = 0;
	
	public static synchronized State getState(int value){
		System.out.println(stateNum + " "+ value);
		boolean finalState = stateNum >= 10000 ? true : false;
		
		if(states.containsKey(new Integer(value))){
			State state = states.get(value);
			state.setFinalState(finalState);
			return state;
		}else{
			State state = new State(value);
			state.setFinalState(finalState);
			states.put(value, state);
			stateNum ++;
			return state;
		}
	}
}
