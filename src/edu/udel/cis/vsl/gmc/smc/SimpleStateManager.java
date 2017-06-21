package edu.udel.cis.vsl.gmc.smc;

import java.io.PrintStream;

import edu.udel.cis.vsl.gmc.seq.StateManager;
import edu.udel.cis.vsl.gmc.seq.TraceStepIF;

/**
 * The implementation of the interface {@link StateManager} used by SMC.
 * 
 * @author Wenhao Wu (wuwenhao@udel.edu)
 */
public class SimpleStateManager extends StateManager<State, String> {
	/**
	 * The {@link MatrixDirectedGraph} represents the state-transition map.
	 */
	MatrixDirectedGraph graph;

	public SimpleStateManager(MatrixDirectedGraph graph) {
		this.graph = graph;
	}

	@Override
	public TraceStepIF<State> nextState(State state, String transition) {
		int destStateValue = graph.getDestStateValue(state.getValue(),
				transition);

		return new TraceStep(transition, new State(destStateValue));
	}

	@Override
	public int getId(State normalizedState) {
		return normalizedState.getId();
	}

	@Override
	public void normalize(TraceStepIF<State> traceStep) {
		// Do nothing
	}

	@Override
	public void printStateShort(PrintStream out, State state) {
		System.out.println("{" + state.getValue() + "}");
	}

	@Override
	public void printStateLong(PrintStream out, State state) {
		System.out.println(state);
	}

	@Override
	public void printTransitionShort(PrintStream out, String transition) {
		System.out.println(transition);
	}

	@Override
	public void printTransitionLong(PrintStream out, String transition) {
		System.err.println("smc.StateManager.printTransitionLong method"
				+ " is not implemented yet.");

	}

	@Override
	public void printAllStatesShort(PrintStream out) {
		System.err.println("smc.StateManager.printAllStatesShort method"
				+ " is not implemented yet.");

	}

	@Override
	public void printAllStatesLong(PrintStream out) {
		System.err.println("smc.StateManager.printAllStatesLong method"
				+ " is not implemented yet.");

	}

	@Override
	public void printTraceStep(State sourceState,
			TraceStepIF<State> traceStep) {
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append("SourceState:");
		sBuilder.append(sourceState);
		sBuilder.append("\n TraceStep:");
		sBuilder.append(traceStep);
		System.out.println(sBuilder);
	}

	@Override
	public void printTraceStepFinalState(State finalState, int normalizedID) {
		System.out.println("FinalState:" + finalState);
	}

}
