package edu.udel.cis.vsl.gmc.simplemc;

import java.util.HashMap;
import java.util.Map;

import edu.udel.cis.vsl.gmc.concurrent.util.Configuration;

public class StateFactory {
	private static Map<String, State> states = new HashMap<>();
	// private static int stateNum = 0;

	public static synchronized State getState(int[] value) {
		StringBuilder sb = new StringBuilder();
		int len = Configuration.lengthOfVector;
		for (int i = 0; i < len; i++) {
			if (i != len - 1) {
				sb.append(value[i] + " ");
			} else
				sb.append(value[i]);
		}
		String key = sb.toString();

		if (states.containsKey(key)) {
			State state = states.get(key);
			return state;
		} else {
			State state = new State(value);
			states.put(key, state);
			// stateNum ++;
			return state;
		}
	}
}
