package ch.ntb.mcdp.system.memMap;

/**
 * Attributes for <code>Device</code> and <code>Segment</code> objects.<br>
 * Use the predefined constants to set or unset an attribute.
 * 
 * @author schlaepfer
 * 
 */
public class MemAttributes {

	public static int NOF_ATTRIBUTES = 8;

	public static int read = 0, write = 1, const_ = 2, code = 3, var = 4,
			sysconst = 5, heap = 6, stack = 7;

	private boolean[] values = new boolean[NOF_ATTRIBUTES];

	MemAttributes() {
		for (int i = 0; i < values.length; i++) {
			values[i] = false;
		}
	}

	public void set(int index) {
		values[index] = true;
	}

	public void unset(int index) {
		values[index] = false;
	}

	public boolean isSet(int index) {
		return values[index];
	}

	public boolean isReadSet() {
		return values[read];
	}

	public boolean isWriteSet() {
		return values[write];
	}

	public boolean isConstSet() {
		return values[const_];
	}

	public boolean isCodeSet() {
		return values[code];
	}

	public boolean isVarSet() {
		return values[var];
	}

	public boolean isSysconstSet() {
		return values[sysconst];
	}

	public boolean isHeapSet() {
		return values[heap];
	}

	public boolean isStackSet() {
		return values[stack];
	}

	public void reset() {
		for (int i = 0; i < values.length; i++) {
			values[i] = false;
		}
	}

	public MemAttributes clone() {
		MemAttributes at = new MemAttributes();
		for (int i = 0; i < NOF_ATTRIBUTES; i++) {
			at.values[i] = values[i];
		}
		return at;
	}
}
