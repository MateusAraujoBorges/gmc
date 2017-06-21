package edu.udel.cis.vsl.gmc.smc;

import static edu.udel.cis.vsl.gmc.smc.SMCConstants.DEFAULT_ERROR_BOUND_OPTION;
import static edu.udel.cis.vsl.gmc.smc.SMCConstants.DEFAULT_REPLAY_OUTPUT_DIR;
import static edu.udel.cis.vsl.gmc.smc.SMCConstants.DEFAULT_SOURCE_STATE_VALUE;

import java.io.File;
import java.io.PrintStream;

import edu.udel.cis.vsl.gmc.seq.EnablerIF;
import edu.udel.cis.vsl.gmc.seq.ErrorLog;
import edu.udel.cis.vsl.gmc.seq.GMCConfiguration;
import edu.udel.cis.vsl.gmc.seq.MisguidedExecutionException;
import edu.udel.cis.vsl.gmc.seq.RandomTransitionChooser;
import edu.udel.cis.vsl.gmc.seq.Replayer;
import edu.udel.cis.vsl.gmc.seq.StatePredicateIF;
import edu.udel.cis.vsl.gmc.seq.Trace;
import edu.udel.cis.vsl.gmc.seq.TransitionChooser;

public class SMCTracePlayer {

	/**
	 * The {@link GMCConfiguration} used for configuring <code>this</code> trace
	 * player.
	 */
	private GMCConfiguration config;

	/**
	 * The {@link PrintStream} used for printing normal info.
	 */
	private PrintStream out = null;

	/**
	 * The GMC {@link ErrorLog} used for printing error info.
	 */
	private ErrorLog log = null;

	/**
	 * The {@link SimpleStateManager} used for replaying the trace info.
	 */
	private SimpleStateManager stateManager = null;

	/**
	 * The implementation of {@link EnablerIF} used for constructing the
	 * Transition-{@link State} system.
	 */
	private EnablerIF<State, String> enabler = null;

	/**
	 * The {@link SMCTransitionChooser} used for constructing {@link #replayer}.
	 */
	private TransitionChooser<State, String> chooser = null;

	/**
	 * The sequential {@link Replayer} used for replaying the trace info.
	 */
	private Replayer<State, String> replayer = null;

	public SMCTracePlayer(GMCConfiguration config, PrintStream out) {
		this.config = config;
		this.out = out;
	}

	public Trace<String, State> run(MatrixDirectedGraph graph,
			StatePredicateIF<State> predicate)
			throws MisguidedExecutionException {
		return run(graph, predicate, new State(DEFAULT_SOURCE_STATE_VALUE));
	}

	public Trace<String, State> run(MatrixDirectedGraph graph,
			StatePredicateIF<State> predicate, State initialState)
			throws MisguidedExecutionException {
		this.enabler = new SMCEnabler(graph);
		this.stateManager = new SimpleStateManager(graph);
		this.log = new ErrorLog(new File(DEFAULT_REPLAY_OUTPUT_DIR),
				graph.toString(), out);
		this.log.setErrorBound((int) config.getAnonymousSection()
				.getValueOrDefault(DEFAULT_ERROR_BOUND_OPTION));
		this.replayer = new Replayer<State, String>(stateManager, out);
		this.replayer.setPrintAllStates(false);
		this.replayer.setQuiet(config.isQuiet());
		this.replayer.setPredicate(predicate);
		this.chooser = new RandomTransitionChooser<State, String>(enabler);

		Trace<String, State> trace = replayer.play(initialState, chooser,
				!config.isQuiet())[0];
		boolean violation = trace.violation();

		violation = violation || log.numErrors() > 0;
		if (violation && !replayer.isQuiet()) {
			out.println("Violation(s) found.");
			out.flush();
		}
		trace.setViolation(violation);
		return trace;
	}
}
