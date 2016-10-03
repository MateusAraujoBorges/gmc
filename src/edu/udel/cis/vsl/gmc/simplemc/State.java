package edu.udel.cis.vsl.gmc.simplemc;

import java.util.BitSet;
import edu.udel.cis.vsl.gmc.concurrent.ProvisoValue;

public class State {
	private int  value;
//	private boolean onStack;
	private int depth;
	private boolean finalState;
	private BitSet onStack;
	// all the descendants in the graph are explored
	private boolean fullyExplored;
	private ProvisoValue prov;
	
	public State(int value){
		this.value = value;
		onStack = new BitSet();
		depth = 0;
		finalState = false;
		fullyExplored = false;
		prov = ProvisoValue.UNKNOWN;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
//	public boolean isOnStack() {
//		return onStack;
//	}
//
//	public void setOnStack(boolean onStack) {
//		this.onStack = onStack;
//	}

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
	
	public boolean isOnStack(int id){
		return onStack.get(id);
	}
	
	public void setOnStack(int id, boolean value){
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
	public int hashCode() {
		return this.value;
	}
}
