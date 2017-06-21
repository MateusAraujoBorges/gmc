package edu.udel.cis.vsl.gmc.smc;

/**
 * The basic implementation of the interface {@link State}.
 * 
 * @author Wenhao Wu (wuwenhao@udel.edu)
 */
public class State {
	/**
	 * The unique identifier of each State instance.
	 */
	private int id = -1;

	/**
	 * The value of this state used by method: {@link #equals(Object)}
	 */
	private int value;

	public State(int value) {
		this.value = value;
	}

	/**
	 * @return the {@link #value} of <code>this</code> state.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return the unique {@link #id} of <code>this</code> state.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set {@link #id} of <code>this</code> state instance with the given
	 * <code>newStateId</code>.
	 * 
	 * @param newId
	 *            the new id of <code>this</code> state.
	 */
	public void setId(int newId) {
		this.id = newId;
	}

	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append("State<");
		sBuilder.append(value);
		sBuilder.append(">");
		return sBuilder.toString();
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(value);
	}

	@Override
	public boolean equals(Object o) {
		return this.value == ((State) o).value;
	}
}
