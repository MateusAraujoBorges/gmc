package edu.udel.cis.vsl.gmc.seq;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import edu.udel.cis.vsl.gmc.GMCConfiguration;
import edu.udel.cis.vsl.gmc.StatePredicateIF;
import edu.udel.cis.vsl.gmc.TraceStepIF;
import edu.udel.cis.vsl.gmc.util.BigRational;
import org.apache.commons.rng.UniformRandomProvider;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class StatisticalSearcher<STATE, TRANSITION> extends Searcher<STATE, TRANSITION> {

	private final Table<STATE, STATE, BigRational> transitionTable;

	private final Stack<StackEntry<STATE, TRANSITION>> trace;

	private BigRational domainCovered;

	private final StateQuantifier<STATE> quantifier;

	private final UniformRandomProvider rng;

	public StatisticalSearcher(EnablerIF<STATE, TRANSITION> enabler, StateManager<STATE, TRANSITION> manager,
	                           StatePredicateIF<STATE> predicate, StateQuantifier<STATE> quantifier,
	                           GMCConfiguration gmcConfig, UniformRandomProvider rng, PrintStream debugOut) {
		super(enabler, manager, predicate, gmcConfig, debugOut);

		// HashTables are backed by LinkedHashMaps - insertion order is preserved
		this.transitionTable = HashBasedTable.create();
		this.trace = new Stack<>();
		this.domainCovered = BigRational.ZERO;
		this.rng = rng;
		this.quantifier = quantifier;
	}

	@Override
	public void restrictDepth() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public STATE currentState() {
		if (trace.isEmpty()) {
			return null;
		}
		return trace.peek().getState();
	}

	private STATE root() {
		return trace.firstElement().getState();
	}

	// Starts the search
	@Override
	public boolean search(STATE initialState) {
		SequentialNode<STATE> initialNode = sequentialNodeFactory
						.getInitialNode(initialState);

		initialState = initialNode.getState();
		if (minimize)
			initialNode.setDepth(0);

		StackEntry<STATE, TRANSITION> stackEntry = sequentialNodeFactory
						.newStackEntry(initialNode, enabler.ampleSet(initialState), 0);

		trace.push(stackEntry);
		initialNode.setStackPosition(trace.size() - 1);
		if (debugging) {
			debugOut.println("Pushed initial state onto stack " + name + ":\n");
			manager.printStateLong(debugOut, initialState);
			debugOut.println();
			debugOut.flush();
		}
		return search();
	}

	// Resume the search.
	@Override
	public boolean search() {
		while (!predicate.holdsAt(currentState())) {
			debug("[statistical.search] Predicate does not hold at current state (" +
							currentState() + ") of " + name + ".");
			if (!proceedToNewState()) {
				if (cycleFound) {
					debug("Cycle found in state space.");
					return true;
				}
				debug("Search complete: predicate " + predicate
								+ " does not hold at " + "any reachable state of "
								+ name + ".\n Domain covered: " + domainCovered.doubleValue());
				assert domainCovered.compareTo(BigRational.ONE) <= 0;
				return false;
			}
			debugf("[statistical.search] current table: %s\n", transitionTable);
		}
		debug("Predicate " + predicate + " holds at current state of " + name
						+ ": terminating search.\n");
		return true;
	}

	// we need to keep SequentialNodes somewhere, or we would have to call
	// manager.nextState again
	private Map<STATE, SequentialNode<STATE>> nodeStorage = new HashMap<>();

	/**
	 * Pseudocode:
	 * - Get current state
	 * - If unexplored state, compute the ampleSet of Transitions
	 * - expand transitions into states with the enabler
	 * - Check satisfiability of each state; count the solutions of sat PCs
	 * - store state -> state -> count into the table
	 * - if state is final or target depth was reached:
	 * - wind the stack and subtract the trace count from each transition,
	 * pruning nodes as needed
	 *
	 * @return
	 */

	@Override
	public boolean proceedToNewState() {
		StackEntry<STATE, TRANSITION> currentStackEntry = trace.peek();
		SequentialNode<STATE> currentSequentialNode = currentStackEntry
						.getNode();
		STATE currentState = currentStackEntry.getState();
		currentSequentialNode.setSeen(true);

		// check bound
		if (stackOutOfBound()) {
			currentSequentialNode.setExpand(false);
		}

		// expand all transitions (PCs are stored in states)
		if (currentSequentialNode.getExpand() && !currentSequentialNode.getFullyExpanded()) {
			expandStateTransitions(currentStackEntry);
			currentSequentialNode.setFullyExpanded(true);
		}

		//select next node or jump back to beginning if there are no remaining transitions
		Map<STATE, BigRational> availableTransitions = transitionTable.row(currentState);
		if (availableTransitions.isEmpty()) {
			backtrackAndPrune();
			// check remaining transitions
			return !transitionTable.isEmpty();
		}
		sampleTransition(availableTransitions);
		return true;
	}

	private void expandStateTransitions(StackEntry<STATE, TRANSITION> stackEntry) {
		STATE state = stackEntry.getState();
		int currentNodeId = stackEntry.getNode().getId();
		int expanded = 0;
		if (debugging) {
			debugOut.printf("[statistical.expand] start expansion for state %s (node %d)" +
							" : \n--\n", state, currentNodeId);
		}

		for (TRANSITION transition : stackEntry.getTransitions()) {
			TraceStepIF<STATE> traceStep = manager.nextState(state,
							transition);

			// Let manager print the trace step (exclude final state):
			manager.printTraceStep(state, traceStep);

			STATE newState = traceStep.getFinalState();
			SequentialNode<STATE> newSequentialNode = sequentialNodeFactory
							.getNode(traceStep);

			newState = newSequentialNode.getState();
			// Let manager print the final state of the trace step:
			manager.printTraceStepFinalState(newState,
							newSequentialNode.getId());

			// for cycle detection, perhaps?
			int newStateStackIndex = newSequentialNode.getStackPosition();
			updateMinimumStackIndex(stackEntry, newStateStackIndex);

			numTransitions++;
			//new state
			if (!newSequentialNode.getSeen()) {
				assert newSequentialNode.getStackPosition() == -1;
				assert !transitionTable.contains(state, newState);
				if (debugging) {
					debugOut.println("[statistical.expand] new state: " + newState + ":");
					debugOut.println();
					manager.printStateLong(debugOut, newState);
					debugOut.println();
					debugOut.flush();
				}
			} else if (debugging) {
				debugOut.println("[statistical.expand] Already saw state " + newState);
			}
			// make sure we don't overwrite any nodes
			Object oldNode = nodeStorage.put(newState, newSequentialNode);
			assert oldNode == null;

			BigRational probability = quantifier.quantify(newState);
			transitionTable.put(state, newState, probability);
			expanded++;
			if (debugging) {
				debugOut.printf("[statistical.expand] Quantified transition between state " +
												"%s (node %d) and %s (node %s) : %f\n",
								state, stackEntry.getNode().getId(), newState,
								newSequentialNode.getId(), probability.doubleValue());
			}
		}
		if (debugging) {
			debugOut.printf("[statistical.expand] finished expanding %d states\n--\n", expanded);
		}
	}

	private void sampleTransition(Map<STATE, BigRational> availableTransitions) {
		BigRational totalProbability = availableTransitions.values().stream()
						.reduce(BigRational.ZERO, BigRational::add);
		BigRational sample = BigRational.valueOf(rng.nextDouble());
		STATE nextState = null;
		if (debugging) {
			debugOut.printf("[statistical.sample] choosing between states: \n\t%s\n",
							availableTransitions.entrySet());
		}
		for (Map.Entry<STATE, BigRational> entry : availableTransitions.entrySet()) {
			nextState = entry.getKey();
			break;
			// disabling sampling for now
//			BigRational normalizedTransitionProbability = entry.getValue().div(totalProbability);
//			sample = sample.sub(normalizedTransitionProbability);
//			if (sample.isNegative() || sample.isZero()) {
//				nextState = entry.getKey();
//				break;
//			}
		}

		assert nextState != null;
		Collection<TRANSITION> nextStateTransitionSet = enabler.ampleSet(nextState);
		SequentialNode<STATE> nextSequentialNode = nodeStorage.get(nextState);
		trace.push(sequentialNodeFactory
						.newStackEntry(nextSequentialNode, nextStateTransitionSet, 0));
		nextSequentialNode.setStackPosition(trace.size() - 1);
		numStatesSeen++;
		debugPrintStack("[statistical.sample] Pushed " + nextState + " onto the stack "
						+ name + ". ", false);
	}

	private void backtrackAndPrune() {
		BigRational pathDomainCoverage = null;
		do {
			StackEntry<STATE, TRANSITION> current = trace.pop();
			StackEntry<STATE, TRANSITION> parent = trace.peek();
			STATE currentState = current.getState();
			STATE parentState = parent.getState();
			debugf("[statistical.backtrack] unwinding transition %s -> %s\n",
							parentState, currentState);

			if (pathDomainCoverage == null) {
			 pathDomainCoverage = transitionTable.get(parentState,
							currentState);
			 domainCovered = domainCovered.add(pathDomainCoverage);
			}
			BigRational prunedCoverage = transitionTable.get(parentState,
							currentState).sub(pathDomainCoverage);

			assert !prunedCoverage.isNegative();
			if (prunedCoverage.isZero()) {
				transitionTable.remove(parentState, currentState);
			} else {
				transitionTable.put(parentState, currentState, prunedCoverage);
			}
			current.getNode().setStackPosition(-1);
		} while (trace.size() > 1);
	}


	// Implementations below are copied from DfsSearcher

	private boolean stackOutOfBound() {
		return stackIsBounded && trace.size() >= depthBound;
	}

	private void debugPrintStack(String s, boolean longFormat) {
		if (debugging) {
			debugOut.println(s + "  New stack for " + name + ":\n");
			printStack(debugOut, longFormat, true);
			debugOut.println("--");
		}
	}

	/**
	 * Prints the current stack in a human-readable format.
	 *
	 * @param out        the stream to which to print the stack
	 * @param longFormat if true, provide detailed information about each state
	 * @param summarize  if true, don't print out more than some fixed bound number of
	 *                   entries from the top of the stack; otherwise print the whole
	 *                   stack
	 */
	public void printStack(PrintStream out, boolean longFormat,
	                       boolean summarize) {
		int size = trace.size();
		out.println("[statistical] printStack starts ---------");
		if (size == 0) {
			out.println("  <EMPTY>");
		}
		for (int i = 0; i < size; i++) {
			StackEntry<STATE, TRANSITION> stackEntry = trace.elementAt(i);
			STATE state = stackEntry.getState();

			if (!summarize || i <= 1 || size - i < summaryCutOff - 1) {
				if (i > 0) {
					out.print(" -> ");
					manager.printStateShort(out, state);
					out.println();
				}
				if (longFormat) {
					out.println();
					manager.printStateLong(out, state);
					out.println();
				}
			}
			if (summarize && size - i == summaryCutOff - 1) {
				for (int j = 0; j < 3; j++)
					out.println("     .");
			}
			if (!summarize || i <= 0 || size - i < summaryCutOff) {
				out.print("Step " + (i + 1) + ": ");
				manager.printStateShort(out, state);
				if (stackEntry.hasNext()) {
					out.print(" --");
					manager.printTransitionShort(out, stackEntry.peek());
				}
				out.flush();
			}
		}
		out.println("\n[statistical] printStack ends -----------");
		out.println();
		out.flush();
	}

	@Override
	public boolean checkStackTrace(StackEntry<STATE, TRANSITION> stackEntry) {
		int stackIndex = stackEntry.getMinimumSuccessorStackIndex();
		int size = trace.size();

		assert stackIndex != Integer.MAX_VALUE;

		for (int i = stackIndex; i < size - 1; i++) {
			if (trace.get(i).getNode().getFullyExpanded())
				return false;
		}
		return true;
	}

	@Override
	public void writeTrace(PrintStream stream) {
		int size = trace.size();
		int prevTid = 0;
		int count = 0;

		stream.println("LENGTH = " + size);
		for (int i = 0; i < size; i++) {
			int curTid = trace.elementAt(i).getTid();

			if (count == 0) {
				count++;
				prevTid = curTid;
			} else {
				if (curTid == prevTid) {
					count++;
				} else {
					stream.println(count + ":" + prevTid);
					count = 1;
					prevTid = curTid;
				}
			}
			if (i == size - 1) {
				stream.println(count + ":" + prevTid);
			}
		}
		stream.flush();
	}

	@Override
	public int traceSize() {
		return trace.size();
	}
}
