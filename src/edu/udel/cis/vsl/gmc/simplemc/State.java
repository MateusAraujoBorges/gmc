package edu.udel.cis.vsl.gmc.simplemc;

public class State {
	private int  value;
	private boolean seen;
	private boolean onStack;
	private int depth;
	private boolean finalState;
	
	public State(int value){
		this.value = value;
		seen = false;
		onStack = false;
		depth = 0;
		finalState = false;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public boolean isOnStack() {
		return onStack;
	}

	public void setOnStack(boolean onStack) {
		this.onStack = onStack;
	}

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
}
