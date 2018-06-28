package edu.udel.cis.vsl.gmc.seq;

import edu.udel.cis.vsl.gmc.util.BigRational;

public interface StateQuantifier<STATE> {

	BigRational quantify(STATE state);
}
