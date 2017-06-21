package edu.udel.cis.vsl.gmc.smc;

import java.util.LinkedList;

/**
 * A simple directed graph implemented with a square matrix.
 * 
 * @author Ziqing Luo
 * @author Wenhao Wu
 *
 */
public class MatrixDirectedGraph {
	/**
	 * The square matrix which represents a directed graph
	 */
	private String[][] matrix;

	/**
	 * The length of a side of the square matrix
	 */
	private int numStates;

	/**
	 * Constructs a {@link MatrixDirectedGraph} with given
	 * <code>squareMatrix</code>. <br>
	 * The <code>squareMatrix</code> is a 2-dimensional String matrix.The row
	 * index is the id of the source {@link State}, the column index is the id
	 * of the destination {@link State}, and the String element in the matrix is
	 * the label of corresponding transition.<br>
	 * E.g., <br>
	 * 
	 * <pre>
	 * {@code
	 * Matrix:     Graph:
	 * X 0 1 2 3      0	
	 * 0   a b    'a'/ \'b'	
	 * 1       c    1   2
	 * 2       d  'c'\ /'d'	
	 * 3     		3	
	 * }
	 * </pre>
	 * 
	 * <br>
	 * For each {@link State} <code>s</code>, if <code>s</code> has <strong>at
	 * least one outgoing transition</strong> with a label starting with '@'
	 * then the ample set for <code>s</code> consists of all transitions
	 * departing from <code>s</code> whose labels start with '@'. If
	 * <code>s</code> has no outgoing transition with label beginning with '@'
	 * then the ample set consists of all transitions departing from
	 * <code>s</code>.<br>
	 * E.g., <br>
	 * 
	 * <pre>
	 * {@code
	 * Matrix:      		Graph:
	 * X 0  1  2  3  4  5	      0	
	 * 0    @a @b c   	'@a'/ |'@b'\'c'	
	 * 1             d  e	   1  2     3
	 * 2            	       'd'/ \'e'
	 * 3            	  	 4   5
	 * 4			- State 0 has an ample set consisting of '@a' and '@b'
	 * 5			- State 1 has an ample set consisting of 'd' and 'e'
	 * }
	 * </pre>
	 * 
	 * <strong>Preconditions:</strong><br>
	 * 1. The given <code>squareMatrix</code> must have a same value for its row
	 * number and column number.<br>
	 * 
	 * @param squareMatrix
	 *            an input 2-dimensional String matrix used for generating the
	 *            directed state-transition graph.
	 * @throws Exception
	 */
	public MatrixDirectedGraph(String[][] squareMatrix) throws Exception {
		// properties checking
		int colLength = squareMatrix.length;

		for (int i = 0; i < colLength; i++)
			if (squareMatrix[i].length != colLength)
				throw new Exception("Given matrix must be a square matrix.");

		this.matrix = squareMatrix;
		this.numStates = colLength;
	}

	/**
	 * Get all outgoing transition labels from the given source state value in
	 * <code>this</code> graph.
	 * 
	 * @param sourceStateValue
	 *            The value of a source node.
	 * @return a String array consisting of all transitions
	 */
	public String[] allTransitions(int sourceStateValue) {
		String[] transitions = new String[numStates];

		assert sourceStateValue >= 0 && sourceStateValue < numStates;
		for (int i = 0; i < numStates; i++) {
			transitions[i] = matrix[sourceStateValue][i];
		}
		return transitions;
	}

	/**
	 * Compute the state value of the destination {@link State} with given
	 * <code>sourceStateValue</code> and <code>transition</code>.
	 * 
	 * @param sourceStateValue
	 *            the value of the source state
	 * @param transition
	 *            the transition outgoing from the {@link State} with the state
	 *            value of <code>sourceStateValue</code>
	 * @return the state value of the destination state; if the destination
	 *         state is not found then {@link Integer#MIN_VALUE} will be
	 *         returned.
	 */
	public int getDestStateValue(int sourceStateValue, String transition) {
		assert sourceStateValue >= 0 && sourceStateValue < numStates;
		for (int i = 0; i < numStates; i++)
			if (matrix[sourceStateValue][i] != null
					&& matrix[sourceStateValue][i].equals(transition))
				return i;
		return Integer.MIN_VALUE;
	}

	/**
	 * Get all outgoing transition labels from the given source state value in
	 * <code>this</code> graph.
	 * 
	 * @param sourceStateValue
	 *            The state value of the given source state.
	 * @return a {@link LinkedList} of outgoing transitions
	 */
	public LinkedList<String> outgoingTransitions(int sourceStateValue) {
		LinkedList<String> transitions = new LinkedList<String>();

		assert sourceStateValue >= 0 && sourceStateValue < numStates;
		for (String transition : matrix[sourceStateValue])
			if (transition != null)
				transitions.add(transition);
		return transitions;
	}

	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append("The transition map is: \n\t");
		sBuilder.append("X\t");

		for (int i = 0; i < numStates; i++) {
			sBuilder.append(i);
			sBuilder.append("\t");
		}
		for (int i = 0; i < numStates; i++) {
			sBuilder.append("\n\t");
			sBuilder.append(i);
			sBuilder.append("\t");
			for (int j = 0; j < numStates; j++) {
				if (matrix[i][j] != null)
					sBuilder.append(matrix[i][j]);
				sBuilder.append("\t");
			}
		}
		sBuilder.append("\n");
		return sBuilder.toString();
	}
}
