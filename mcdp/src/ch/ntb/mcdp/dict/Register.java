package ch.ntb.mcdp.dict;

public class Register {

	// Register Types
	protected static String[] types = null;

	public Register(String name, int type, int value, int size,
			String description) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.size = size;
		this.description = description;
	}

	public final String name;

	public final int type;

	public final int value;

	public final int size;

	public final String description;

	@Override
	public String toString() {
		return new String(name + "\t" + types[type] + "\t0x"
				+ Integer.toHexString(value) + "\t" + size + "\t" + description);
	}
	
	public String[] getTypes(){
		return types;		
	}
}
