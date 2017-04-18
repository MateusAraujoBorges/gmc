package edu.udel.cis.vsl.gmc;

import java.io.PrintStream;

/**
 * A StateManagerIF provides part of a generic interface to a state-transition
 * system. The primary method is {@link #nextState}, which, given a state and a
 * transition, returns the "next state", i.e., the state which results from
 * executing the transition from the given state. Other methods are provided
 * that are needed specifically for depth-first search, including methods to
 * mark a state as "seen before", and to make a state as
 * "currently on (or off) the stack". Still other methods are provided for
 * printing information abou states.
 * 
 * @author Stephen F. Siegel
 * @author Yihao Yan (yanyihao)
 * 
 * @param <STATE>
 *            the type used to represent states in the state-transition system
 *            being analyzed
 * @param <TRANSITION>
 *            the type used to represent transitions in the state-transition
 *            system being analyzed
 */
public interface StateManagerIF<STATE, TRANSITION> {

	/**
	 * Given a state and a transition, returns the trace step after executing
	 * the transition at the given state. See {@link TraceStepIF}.
	 * 
	 * @param state
	 *            a state in the state transition system
	 * @param transition
	 *            an execution which is enabled at the given state
	 * @return the trace step after executing the transition at the given state.
	 */
	TraceStepIF<STATE> nextState(STATE state, TRANSITION transition);

	/**
	 * Sets the "seen flag" in the given state to the given value. The method
	 * can be implemented in any way, as long as the corresponding method to get
	 * the seen flag returns the correct result. It might be implemented using a
	 * boolean field in the STATE class, or it might be implemented using a hash
	 * set in the manager class, or some other way.
	 * 
	 * The seen flag is intended to be used by a depth-first search algorithm,
	 * to mark that a state has been encountered in the search.
	 * 
	 * @param state
	 *            any state in the state transition system
	 * @param value
	 *            the value you want to assign to the seen flag associated to
	 *            that state
	 */
	void setSeen(STATE state, boolean value);

	/**
	 * Returns the value of the seen flag associated to the given state.
	 * 
	 * @param state
	 *            any state in the state transition system
	 * @return the value of that state's seen flag.
	 */
	boolean seen(STATE state);

	/**
	 * <p>
	 * Sets the "stack position" field in the given state to the given value.
	 * The method can be implemented in any way, as long as
	 * {@link #stackPosition(STATE)} returns the correct result. It might be
	 * implemented using a integer field in the STATE class, or it might be
	 * implemented using a hash set in the manager class, or some other way.
	 * </p>
	 * <p>
	 * The "stack position" field is intended to be used by a depth-first search
	 * algorithm, to mark that a state is currently on the depth-first search
	 * stack.
	 * </p>
	 * <p>
	 * The bottom element in the stack will have an index 0.
	 * </p>
	 * 
	 * @param state
	 *            any state in the state transition system
	 * @param value
	 *            the value you want to assign to the on-the-stack flag
	 *            associated to that state which is the index the state is at on
	 *            the stack.
	 */
	void setStackPosition(STATE state, int stackIndex);

	/**
	 * Get the position of this state on dfs stack or -1 if it is not on stack.
	 * 
	 * @param state
	 *            any state in the state transition system
	 * @return position of this state on dfs stack or -1
	 */
	int stackPosition(STATE state);

	/**
	 * @param state
	 *            The target state.
	 * @return true iff the target state has been expanded.
	 */
	boolean fullyExpanded(STATE state);

	/**
	 * Set the expanded flag of the target state.
	 * 
	 * @param state
	 *            The target state.
	 */
	void setFullyExpanded(STATE state, boolean value);

	/**
	 * Prints out a short human-readable representation of the state. This is
	 * intended to be something like "State 13", or something similar.
	 * 
	 * @param out
	 *            the stream to which to send the output
	 * @param state
	 *            any state in the state transition system
	 */
	void printStateShort(PrintStream out, STATE state);

	/**
	 * Prints out a long human-readable representation of the state. This is
	 * intended to show all the details of the state, e.g., the values of all
	 * variables, etc.
	 * 
	 * @param out
	 *            the stream to which to send the output
	 * @param state
	 *            any state in the state transition system
	 */
	void printStateLong(PrintStream out, STATE state);

	/**
	 * Prints out a short human-readable representation of the transition.
	 * 
	 * @param out
	 *            the stream to which to send the output
	 * @param transition
	 *            any transition in the state transition system
	 */
	void printTransitionShort(PrintStream out, TRANSITION transition);

	/**
	 * Prints out a long human-readable representation of the transition. This
	 * is intended to show all details of the transition.
	 * 
	 * @param out
	 *            the stream to which to send the output
	 * @param transition
	 *            any transition in the state transition system
	 */
	void printTransitionLong(PrintStream out, TRANSITION transition);

	/**
	 * Prints out all the states, in short form, currently "held" by this
	 * manager. It is up to each implementation to decide what states are
	 * "held".
	 * 
	 * @param out
	 *            the stream to which to send the output
	 */
	void printAllStatesShort(PrintStream out);

	/**
	 * Prints out all the states, in long form, currently "held" by this
	 * manager. It is up to each implementation to decide what states are
	 * "held".
	 * 
	 * @param out
	 *            the stream to which to send the output
	 */
	void printAllStatesLong(PrintStream out);

	/**
	 * Set the "all-successors-on-stack" of a state to certain value. If this
	 * flag is true, this state is supposed to be expanded.
	 * 
	 * @param state
	 *            the target state
	 * @param value
	 *            the boolean value that is going to be assigned to the
	 *            "all-successors-on-stack" flag
	 */
	void setExpand(STATE state, boolean value);

	/**
	 * Get the value of the "all-successors-on-stack" flag of a state.
	 * 
	 * @param state
	 *            the target state
	 * @return the value for "all-successors-on-stack" flag
	 */
	boolean expand(STATE state);

	// optional methods for minimal DFS algorithm

	int getDepth(STATE state);

	void setDepth(STATE state, int value);

}