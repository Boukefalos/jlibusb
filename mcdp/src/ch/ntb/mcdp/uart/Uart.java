package ch.ntb.mcdp.uart;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

public abstract class Uart {

	private UartOutputStream out;

	UartInputStream in;

	Uart(LinkedList<Uart> list) {
		list.add(this);
		out = new UartOutputStream(getSTYPE_IN());
		in = new UartInputStream();
	}

	/**
	 * Get the stream to write to the target device.
	 * 
	 * @return OutputStream to write to target device
	 */
	OutputStream getOutputStream() {
		return out;
	}

	/**
	 * Get the stream to read from the target device.
	 * 
	 * @return InputStream to read from target device
	 */
	InputStream getInputStream() {
		return in;
	}

	/**
	 * The packet subtype specified for this UART packet (from target to PC).
	 * <br>
	 * Note: This direction is different from the input/output direction of the
	 * streams.
	 * 
	 * @return packet subtype
	 */
	abstract byte getSTYPE_OUT();

	/**
	 * The packet subtype specified for this UART packet (from PC to target).
	 * <br>
	 * Note: This direction is different from the input/output direction of the
	 * streams.
	 * 
	 * @return packet subtype
	 */
	abstract byte getSTYPE_IN();

}
