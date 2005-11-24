package ch.ntb.mcdp.dict;

/**
 * Representation of a register.
 * 
 * @author schlaepfer
 */
public abstract class Register {

	/**
	 * Register specific type values. <br>
	 * The index of each type in the types array represents its numeric value.
	 * When the register is read from an xml file, its type is converted from a
	 * string (xml file) to an integer (index of types field). Therefore each
	 * type in the corresponding xml file must be included in the type array.<br>
	 * The type field must be initialised in the <code>static { }</code>
	 * section of the subclass.
	 */
	protected static String[] types = null;

	/**
	 * Name of the register. Registers are identified by this value.
	 */
	public String name = "NOT INITIALISED";

	/**
	 * Register specific type
	 */
	public int type;

	/**
	 * Address or a register specific value (e.g. BDI-identifier)
	 */
	public int value;

	/**
	 * Size in bytes (width)
	 */
	public int size;

	/**
	 * A string description of the register
	 */
	public String description;

	/**
	 * @param name
	 *            name of the register. Registers are identified by this value.
	 * @param type
	 *            register specific type
	 * @param value
	 *            address or a register specific value (e.g. BDI-identifier)
	 * @param size
	 *            size in bytes
	 * @param description
	 *            a string description of the register
	 */
	public void init(String name, int type, int value, int size,
			String description) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.size = size;
		this.description = description;
	}

	@Override
	public String toString() {
		return new String(name + "\t" + types[type] + "\t0x"
				+ Integer.toHexString(value) + "\t" + size + "\t" + description);
	}

	/**
	 * Get the register specific type strings.
	 * 
	 * @return types strings
	 */
	public static String[] getTypes() {
		return types;
	}
}
