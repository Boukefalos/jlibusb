package ch.ntb.mcdp.uart;

import java.io.IOException;
import java.io.OutputStream;

import ch.ntb.usb.USBException;

public class UartOutputStream extends OutputStream {
	
	private byte packetSubType;
	
	UartOutputStream(byte packetSubType) {
		this.packetSubType = packetSubType;
	}

	@Override
	public void write(int b) throws IOException {
		byte[] data = new byte[1];
		data[0] = (byte) b;
		try {
			UartDispatch.write(packetSubType, data, 1);
		} catch (USBException e) {
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0)
				|| ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		int newLen = 0;
		do {
			newLen = Math.min(len, UartDispatch.UART_BUF_LEN);
			byte[] data = new byte[newLen];
			for (int i = 0; i < newLen; i++) {
				data[i] = b[off + i];
			}
			try {
				UartDispatch.write(packetSubType, data, newLen);
			} catch (USBException e) {
				throw new IOException(e.getMessage());
			}
			len -= newLen;
		} while (len > UartDispatch.UART_BUF_LEN);
	}
}
