package ch.ntb.mcdp.uart;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import ch.ntb.mcdp.usb.DataPacket;

public class UartInputStream extends InputStream {

	private int bufferPos = 0;

	LinkedList<DataPacket> bufferList = new LinkedList<DataPacket>();

	@Override
	public int read() throws IOException {
		while (!bufferList.isEmpty()) {
			if (bufferPos >= bufferList.element().data.length) {
				// remove element, reset bufferPos
				bufferList.remove();
				bufferPos = 0;
			} else {
				return bufferList.element().data[bufferPos++];
			}
		}
		return -1;
	}

	@Override
	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0)
				|| ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}
		
		// TODO: optimize
		int c = read();
		if (c == -1) {
			return -1;
		}
		b[off] = (byte) c;

		int i = 1;
		try {
			for (; i < len; i++) {
				c = read();
				if (c == -1) {
					break;
				}
				if (b != null) {
					b[off + i] = (byte) c;
				}
			}
		} catch (IOException ee) {
		}
		return i;
	}

	@Override
	public int available() throws IOException {
		if (bufferList.isEmpty())
			return 0;
		return bufferList.element().data.length;
	}

}
