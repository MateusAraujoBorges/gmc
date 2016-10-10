package edu.udel.cis.vsl.gmc.simplemc;

import java.util.BitSet;

import edu.udel.cis.vsl.gmc.concurrent.ProvisoValue;

public class State {
	private int[] value;
	// private boolean onStack;
	private int depth;
	private boolean finalState;
	private BitSet onStack;
	// all the descendants in the graph are explored
	private boolean fullyExplored;
	private ProvisoValue prov;

	public State() {
		this.value = new int[4];
		value[0] = value[1] = value[2] = value[3] = 1;
		onStack = new BitSet();
		depth = 0;
		finalState = false;
		fullyExplored = false;
		prov = ProvisoValue.UNKNOWN;
	}

	public State(int[] value) {
		this.value = value;
		onStack = new BitSet();
		depth = 0;
		finalState = false;
		fullyExplored = false;
		prov = ProvisoValue.UNKNOWN;
	}

	public int[] getValue() {
		return value;
	}

	public int[] setValue(int index, int v) {
		int[] newValue = new int[4];
		for (int i = 0; i < 4; i++) {
			if (i != index) {
				newValue[i] = value[i];
			} else {
				newValue[i] = v;
			}
		}
		return newValue;
	}

	// public boolean isOnStack() {
	// return onStack;
	// }
	//
	// public void setOnStack(boolean onStack) {
	// this.onStack = onStack;
	// }

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public boolean isFinalState() {
		return finalState;
	}

	public void setFinalState(boolean finalState) {
		this.finalState = finalState;
	}

	public boolean isOnStack(int id) {
		return onStack.get(id);
	}

	public void setOnStack(int id, boolean value) {
		onStack.set(id, value);
	}

	public boolean isFullyExplored() {
		return fullyExplored;
	}

	public void setFullyExplored(boolean fullyExplored) {
		this.fullyExplored = fullyExplored;
	}

	public ProvisoValue prov() {
		return prov;
	}

	public void setProv(ProvisoValue prov) {
		this.prov = prov;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<4; i++){
			if(i != 3){
				sb.append(value[i] + " ");
			}else
				sb.append(value[i]);
		}
		
		return sb.toString();
	}

//	@Override
//	public int hashCode() {
//		int code = 0;
//		int len = value.length;
//
//		while (len > 0) {
//			code *= 31;
//			code += value[len - 1];
//		}
//		return code;
//	}
}
