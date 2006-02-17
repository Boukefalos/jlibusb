package ch.ntb.mcdp.uart.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.ntb.mcdp.uart.Uart0;
import ch.ntb.mcdp.uart.UartDispatch;

public class UartTest {

	static Thread reader;

	static OutputStream out;

	static InputStream in;

	static byte[] buffer = new byte[UartDispatch.UART_BUF_LEN];

	public static void init() {

		// create an uart object
		Uart0 uart = new Uart0();
		// get the streams
		out = uart.getOutputStream();
		in = uart.getInputStream();

		reader = new Thread() {
			public void run() {
				int readLen = 0;
				while (true) {
					try {
						readLen = in.read(buffer);
						if (readLen > 0) {
							for (int i = 0; i < readLen; i++) {
								System.out.print((char) buffer[i]);
							}
						}
						Thread.sleep(50);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		reader.start();
	}

	static public void button1() {
		byte[] buffer = new byte[UartDispatch.UART_BUF_LEN];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) ('0' + (i % ('z' - '0')));
		}
		try {
			out.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public void button2() {

	}

	static public void button3() {

	}

	static public void button4() {

	}

	static public void button5() {

	}

	static public void button6() {

	}

	static public void button7() {

	}

	static public void button8() {

	}

	static public void button9() {

	}

	static public void button10() {

	}

	static public void button11() {

	}

	static public void button12() {

	}

	static public void button13() {

	}

	static public void button14() {

	}

	static public void button15() {

	}
}
