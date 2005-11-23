package ch.ntb.mcdp.uart;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class Uart {

	private UartOutputStream out;

	UartInputStream in;

	Uart() {
		UartDispatch.getUartList().add(this);
		out = new UartOutputStream(getSTYPE_IN());
		in = new UartInputStream();
		// if the read-Thread is already started, this statement has no effect
		UartDispatch.start();
	}

	/**
	 * Get the stream to write to the target device.
	 * 
	 * @return OutputStream to write to target device
	 */
	public OutputStream getOutputStream() {
		return out;
	}

	/**
	 * Get the stream to read from the target device.
	 * 
	 * @return InputStream to read from target device
	 */
	public InputStream getInputStream() {
		return in;
	}

	/**
	 * The packet subtype specified for this UART packet (from target to PC).
	 * This constant is defined in <code>Dispatch.h</code>.<br>
	 * Note: This direction is different from the input/output direction of the
	 * streams.
	 * 
	 * @return packet subtype
	 */
	abstract byte getSTYPE_OUT();

	/**
	 * The packet subtype specified for this UART packet (from PC to target).
	 * This constant is defined in <code>Dispatch.h</code>.<br>
	 * Note: This direction is different from the input/output direction of the
	 * streams.
	 * 
	 * @return packet subtype
	 */
	abstract byte getSTYPE_IN();

}
