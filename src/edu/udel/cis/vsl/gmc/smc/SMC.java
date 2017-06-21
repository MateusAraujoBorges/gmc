package edu.udel.cis.vsl.gmc.smc;

import static edu.udel.cis.vsl.gmc.smc.SMCConstants.DEFAULT_SOURCE_STATE_VALUE;

import edu.udel.cis.vsl.gmc.seq.DfsSearcher;
import edu.udel.cis.vsl.gmc.seq.EnablerIF;
import edu.udel.cis.vsl.gmc.seq.GMCConfiguration;
import edu.udel.cis.vsl.gmc.seq.StatePredicateIF;

/**
 * This is a simple sequential model checker (SMC) implementing general model
 * checker (GMC). <br>
 * The main purposes is for both testing interfaces of GMC and providing and
 * simple sequential implementation example for GMC.
 * 
 * @author Wenhao Wu (wuwenhao@udel.edu)
 */
public class SMC {
	/**
	 * The implementation of {@link EnablerIF} used for constructing the
	 * Transition-{@link State} system.
	 */
	private EnablerIF<State, String> enabler = null;

	/**
	 * The {@link SimpleStateManager} used for exploring successor {@link State}s.
	 */
	private SimpleStateManager stateManager = null;

	/**
	 * Determine whether the {@link DfsSearcher} will print debug info.
	 */
	private boolean debug = false;

	/**
	 * For a given {@link MatrixDirectedGraph} <code>graph</code>, a
	 * <code>predicate</code> implementing {@link StatePredicateIF} and a
	 * starting {@link State} <code>initialState</code>,<br>
	 * If the <code>predicate</code> is hold, <code>true</code> will be
	 * returned, else <code>false</code>.
	 * 
	 * @param graph
	 *            A {@link MatrixDirectedGraph} representing the transition map.
	 * @param predicate
	 *            A predicate of a property
	 * @param initialState
	 *            The starting {@link State}
	 * @return <code>true</code> iff there is a state in the violation state
	 *         list, else <code>false</code>.
	 */
	public boolean run(MatrixDirectedGraph graph,
			StatePredicateIF<State> predicate, State initialState,
			GMCConfiguration config) {
		DfsSearcher<State, String> searcher;

		this.enabler = new SMCEnabler(graph);
		this.stateManager = new SimpleStateManager(graph);
		if (debug)
			searcher = new DfsSearcher<>(enabler, stateManager, predicate,
					config, System.out);
		else
			searcher = new DfsSearcher<>(enabler, stateManager, predicate,
					config);

		searcher.setDebugging(debug);
		return !searcher.search(initialState);
	}

	/**
	 * For a given {@link MatrixDirectedGraph} <code>graph</code> and a
	 * <code>predicate</code> implementing {@link StatePredicateIF},<br>
	 * If the <code>predicate</code> is hold, <code>true</code> will be
	 * returned, else <code>false</code>.<br>
	 * Note that the default starting state is the state with id of
	 * <code>0</code>
	 * 
	 * @param graph
	 *            A {@link MatrixDirectedGraph} representing the transition map.
	 * @param predicate
	 *            A predicate of a property
	 * @return <code>true</code> iff there is a state in the violation state
	 *         list, else <code>false</code>.
	 */
	public boolean run(MatrixDirectedGraph graph,
			StatePredicateIF<State> predicate, GMCConfiguration config) {
		return run(graph, predicate, new State(DEFAULT_SOURCE_STATE_VALUE),
				config);
	}

	/**
	 * Set SMC to print debugging info, iff <code>isDebug</code> is
	 * <code>true</code>
	 */
	public void setDebug(boolean isDebug) {
		this.debug = isDebug;
	}
}
