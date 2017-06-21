package edu.udel.cis.vsl.gmc.smc;

import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;

import edu.udel.cis.vsl.gmc.seq.EnablerIF;

/**
 * The implementation of {@link EnablerIF} used by SMC.
 * 
 * @author Ziqing Luo
 * @author Wenhao Wu (wuwenhao@udel.edu)
 */
public class SMCEnabler implements EnablerIF<State, String> {

	/**
	 * The directed graph of the state-transition model
	 */
	private MatrixDirectedGraph graph;

	/**
	 * The boolean indicating whether debug info will be printed
	 */
	private boolean debug = false;

	/**
	 * The {@link PrintStream} used for printing debug info.
	 */
	private PrintStream debugStream = System.out;

	public SMCEnabler(MatrixDirectedGraph graph) {
		this.graph = graph;
	}

	/**
	 * {@inheritDoc}<br>
	 * <p>
	 * Note that, all transitions in a same {@link Collection} should share a
	 * same source {@link State}. (Because the SMC uses the fly-weight)
	 * </p>
	 */
	@Override
	public Collection<String> ampleSet(State source) {
		LinkedList<String> outgoingTransitions = graph
				.outgoingTransitions(source.getValue());
		LinkedList<String> ampleSet = new LinkedList<>();

		for (String transition : outgoingTransitions) {
			if (transition.startsWith("@")) {
				ampleSet.add(transition);
			}
		}
		if (ampleSet.isEmpty())
			return outgoingTransitions;
		return ampleSet;
	}

	@Override
	public Collection<String> fullSet(State state) {
		return graph.outgoingTransitions(state.getValue());
	}

	@Override
	public void setDebugging(boolean value) {
		this.debug = value;
	}

	@Override
	public boolean debugging() {
		return debug;
	}

	@Override
	public void setDebugOut(PrintStream out) {
		this.debugStream = out;
	}

	@Override
	public PrintStream getDebugOut() {
		return debugStream;
	}
}
