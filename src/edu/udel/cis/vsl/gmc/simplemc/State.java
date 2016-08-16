package edu.udel.cis.vsl.gmc.simplemc;

import java.util.BitSet;

public class State {
	private int  value;
//	private boolean onStack;
	private int depth;
	private boolean finalState;
	private BitSet onStack;
	// all the descendants in the graph are explored
	private boolean fullyExplored;
	// 1 means inviolable, -1 means violable and 0 means unknown.
	private int inviolable;
	
	public State(int value){
		this.value = value;
		onStack = new BitSet();
		depth = 0;
		finalState = false;
		fullyExplored = false;
		inviolable = 0;
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

	public int getInviolable() {
		return inviolable;
	}

	public void setInviolable(int inviolable) {
		this.inviolable = inviolable;
	}
	
	@Override
	public int hashCode() {
		return this.value;
	}
}
