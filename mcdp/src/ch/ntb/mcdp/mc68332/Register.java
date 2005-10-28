package ch.ntb.mcdp.mc68332;

class Register {

	public final static int UserReg = 0;

	public final static int SysReg = 1;

	public final static int CtrReg = 2;

	Register(String name, int type, int addr, int size, String description) {
		this.name = name;
		this.type = type;
		this.addr = addr;
		this.size = size;
		this.description = description;
	}

	public final String name;

	public final int type;

	/**
	 * Absolute address of this register. <br>
	 * For system and user register this value is used as BDI specific
	 * identifier.
	 */
	public final int addr;

	public final int size;

	public final String description;
}
