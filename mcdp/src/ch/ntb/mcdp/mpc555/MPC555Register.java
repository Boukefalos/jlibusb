package ch.ntb.mcdp.mpc555;

import ch.ntb.mcdp.dict.Register;

/**
 * Representation of a MPC555 Register
 * 
 * @author schlaepfer
 * 
 */
public class MPC555Register extends Register {

	// Register Types
	static {
		types = new String[] { "GPR", "FPR", "SPR", "MSR", "CR", "FPSCR",
				"CtrlReg" };
	}

	static final int GPR = 0;

	static final int FPR = 1;

	static final int SPR = 2;

	static final int MSR = 3;

	static final int CR = 4;

	static final int FPSCR = 5;

	static final int CtrlReg = 6;

}
