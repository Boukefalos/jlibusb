package ch.ntb.mcdp.uart.blackbox.test;

import ch.ntb.mcdp.uart.UartDispatch;
import ch.ntb.mcdp.uart.blackbox.Uart0;

public class UartTest {

	static Thread reader;

	public static void init() {

		reader = new Thread() {
			public void run() {
				while (true) {
					try {
						byte[] result = Uart0.read();
						if (result != null) {
							for (int i = 0; i < result.length; i++) {
								System.out.print((char) result[i]);
							}
						}
						sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		reader.start();
	}

	static public void button1() {
		byte[] buffer = new byte[UartDispatch.MAX_UART_PAYLOAD];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) ('0' + (i % ('z' - '0')));
		}
		boolean done = Uart0.write(buffer, buffer.length);
		if (done) {
			System.out.println("success");
		} else {
			System.out.println("failed");
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
