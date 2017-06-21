package edu.udel.cis.vsl.gmc.smc;

import edu.udel.cis.vsl.gmc.seq.TraceStepIF;

/**
 * The implementation of the interface {@link TraceStepIF} used by SMC.
 * 
 * @author Wenhao Wu (wuwenhao@udel.edu)
 */
public class TraceStep implements TraceStepIF<State> {
	/**
	 * The transition related with <code>this</code> trace step.
	 */
	private String transition;

	/**
	 * The final {@link State} of <code>this</code> trace-steps
	 */
	private State finalState;

	/**
	 * Construct an instance of {@link TraceStep} with given
	 * <code>transition</code> and <code>finalState</code>
	 * 
	 * @param transition
	 *            the transition related with <code>this</code> trace step.
	 * @param finalState
	 *            the final {@link State} of <code>this</code> trace step.
	 */
	public TraceStep(String transition, State finalState) {
		this.transition = transition;
		this.finalState = finalState;
	}

	@Override
	public State getFinalState() {
		return this.finalState;
	}

	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append("['");
		sBuilder.append(transition);
		sBuilder.append("'=>");
		sBuilder.append(finalState);
		sBuilder.append("]");
		return sBuilder.toString();
	}
}
