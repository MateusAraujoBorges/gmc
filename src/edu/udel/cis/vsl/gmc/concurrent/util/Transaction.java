package edu.udel.cis.vsl.gmc.concurrent.util;

import edu.udel.cis.vsl.gmc.simplemc.State;
import edu.udel.cis.vsl.gmc.simplemc.Transition;

public class Transaction {
	private int threadId;
	/*
	 * transactionType = 1 : spawn new thread;
	 * transactionType = 2 : proceed depth first search;
	 * transactionType = 3 : check stack proviso;
	 */
	private int transactionType;
	private State sourceState;
	private Transition transition;
	private State desState;
	
	public Transaction(int tid, int tType, State sState, Transition t, State dState){
		threadId = tid;
		transactionType = tType;
		sourceState = sState;
		transition = t;
		desState = dState;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public int getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}

	public State getSourceState() {
		return sourceState;
	}

	public void setSourceState(State sourceState) {
		this.sourceState = sourceState;
	}

	public Transition getTransition() {
		return transition;
	}

	public void setTransition(Transition transition) {
		this.transition = transition;
	}

	public State getDesState() {
		return desState;
	}

	public void setDesState(State desState) {
		this.desState = desState;
	}
}
