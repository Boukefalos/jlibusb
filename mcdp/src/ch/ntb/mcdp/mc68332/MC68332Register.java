package ch.ntb.mcdp.mc68332;

import ch.ntb.mcdp.dict.Register;

/**
 * For system and user registers the <code>value</code> value is used as BDI
 * specific identifier (code specific to each register from the Technical
 * Reference Manual).
 */

public class MC68332Register extends Register {

	// Register Types
	static {
		types = new String[] { "UserReg", "SysReg", "CtrlReg" };
	}

	static final int UserReg = 0;

	static final int SysReg = 1;

	static final int CtrlReg = 2;

}
