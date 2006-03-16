package ch.ntb.mcdp.dict;

/**
 * Representation of a register.
 * 
 * @author schlaepfer
 */
public abstract class Register {

	private static final String INIT_STRING = "***";

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
	 * Menemoic of the register. Registers are identified by this value.
	 */
	private String mnemonic = INIT_STRING;

	/**
	 * Alternative mnemonic of the register
	 */
	private String altmnemonic = INIT_STRING;

	/**
	 * Register specific type
	 */
	private int type;

	/**
	 * Address or a register specific value (e.g. BDI-identifier)
	 */
	private int value;

	/**
	 * Size in bytes (width)
	 */
	private int size;

	/**
	 * A string description of the register
	 */
	private String description;

	/**
	 * @return the mnemonic of this register
	 */
	public String getMnemonic() {
		return mnemonic;
	}

	/**
	 * Set the mnemonic of this register
	 * 
	 * @param name
	 */
	public void setMnemonic(String name) {
		this.mnemonic = name;
	}

	/**
	 * @return alternative mnemonic of the register
	 */
	public String getAltmnemonic() {
		return altmnemonic;
	}

	/**
	 * Set the alternative name of the register.
	 * 
	 * @param altname
	 */
	public void setAltmnemonic(String altname) {
		this.altmnemonic = altname;
	}

	/**
	 * @return the register description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the register description.
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the size in bytes (width)
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Set the size in bytes (width)
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the type of the register. This is the index of the static
	 *         <code>types</code> String array.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Set the type of the register. This is the index of the static
	 * <code>types</code> String array.
	 * 
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the address or a register specific value (e.g. BDI-identifier)
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Set the address or a register specific value (e.g. BDI-identifier).
	 * 
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * @return true if a mnemonic was set and the size is valid (size > 0), else
	 *         false
	 */
	public boolean isValid() {
		if ((mnemonic == INIT_STRING) || (size <= 0))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new String(mnemonic + "\t" + altmnemonic + "\t" + types[type]
				+ "\t0x" + Integer.toHexString(value) + "\t" + size + "\t"
				+ description);
	}

	/**
	 * Get the register specific type strings. This value has to be initialised
	 * in the <code>static</code> section of the derived Register class as
	 * this is Register specific.
	 * 
	 * @return types strings
	 */
	public static String[] getTypes() {
		return types;
	}
}
