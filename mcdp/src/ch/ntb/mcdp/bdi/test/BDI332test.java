package ch.ntb.mcdp.bdi.test;

import ch.ntb.mcdp.bdi.BDI332;
import ch.ntb.mcdp.bdi.BDIException;
import ch.ntb.mcdp.mc68332.IMCBTargetBoard;
import ch.ntb.mcdp.usb.DataPacket;
import ch.ntb.mcdp.usb.DispatchException;
import ch.ntb.mcdp.usb.USBDevice;
import ch.ntb.usb.USBException;

public class BDI332test {

	private static void testBdiTransaction() {
		// test bdi transaction
		DataPacket result = null;
		try {
			result = BDI332.transfer(0x0C00);
		} catch (USBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (DispatchException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result != null) {
			result.toString();
		}
	}

	private static void reset_target() {
		try {
			BDI332.reset_target();
		} catch (USBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (DispatchException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void freeze() {
		try {
			System.out
					.println("isFreezeAsserted: " + BDI332.isFreezeAsserted());
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void break_() {
		try {
			BDI332.break_();
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void go() {
		try {
			BDI332.go();
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void writeMem() {
		final int BASE_ADDR = 0x105624;
		try {
			BDI332.writeMem(BASE_ADDR, 0x123456, 4);
			BDI332.writeMem(BASE_ADDR + 4, 0x123457, 4);
			BDI332.writeMem(BASE_ADDR + 8, 0x123458, 4);
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void readMem() {
		final int BASE_ADDR = 0x105624;
		try {
			StringBuffer sb = new StringBuffer("0x"
					+ Integer.toHexString(BDI332.readMem(BASE_ADDR, 4)) + "\n");
			sb.append("0x"
					+ Integer.toHexString(BDI332.readMem(BASE_ADDR + 4, 4))
					+ "\n");
			sb.append("0x"
					+ Integer.toHexString(BDI332.readMem(BASE_ADDR + 8, 4))
					+ "\n");
			System.out.println(sb.toString());
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// private static void fastDownload() {
	// int[] testData = new int[120];
	// for (int i = 0; i < testData.length; i++) {
	// testData[i] = i;
	// }
	// try {
	// BDI332.startFastDownload(0x800000);
	// BDI332.fastDownload(testData, BDI332.MAX_NOF_WORDS_FAST_DOWNLOAD);
	// BDI332.stopFastDownload();
	// } catch (USBException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (DispatchException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (BDIException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// private static void readMemSeq() {
	// int startAddr = 0x800000;
	// try {
	// StringBuffer sb = new StringBuffer(0 + "\tData: 0x"
	// + Integer.toHexString(BDI332.readMem(startAddr, 4)) + "\n");
	// for (int i = 1; i < 120; i++) {
	// sb.append(i + "\tData: 0x"
	// + Integer.toHexString(BDI332.readMemSeq(4)) + "\n");
	// }
	// System.out.println(sb.toString());
	// } catch (USBException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (DispatchException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (BDIException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	public static void button1() {
		System.out.println("testBdiTransaction()");
		testBdiTransaction();
	}

	public static void button2() {
		System.out.println("reset_target()");
		reset_target();
	}

	public static void button3() {
		System.out.println("go()");
		go();
	}

	public static void button4() {
		System.out.println("break_()");
		break_();
	}

	public static void button5() {
		System.out.println("freeze()");
		freeze();
	}

	public static void button6() {
		System.out.println("writeMem()");
		writeMem();
	}

	public static void button7() {
		System.out.println("readMem()");
		readMem();
	}

	public static void button8() {

		final int BASE_ADDR = 0x105624;
		int[] result;

		System.out.println("dump()");
		try {
			System.out.println("Data: 0x"
					+ Integer.toHexString(BDI332.readMem(BASE_ADDR, 4)) + " ");
			result = BDI332.dumpMem(BDI332.MAX_NOF_LONGS_FILL);
			for (int i = 0; i < result.length; i++) {
				System.out.print("0x" + Integer.toHexString(result[i]) + " ");
			}
			System.out.println();

		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void button9() {
		try {
			BDI332.nop();
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void button10() {

		final int BASE_ADDR = 0x105624;

		System.out.println("fill");
		try {
			BDI332.writeMem(BASE_ADDR, 0, 4);
			int[] data = new int[BDI332.MAX_NOF_LONGS_FILL];
			for (int i = 0; i < data.length; i++) {
				data[i] = i;
			}
			BDI332.fillMem(data, data.length);
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void button11() {
		System.out.println("initTarget()");
		try {
			IMCBTargetBoard.init();
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void button12() {
		System.out.println("readMem()");
		readMem();
	}

	public static void button13() {
		final int BASE_ADDR = 0x105624;
		final int FIRST_VAL = 0xFF;
		final boolean DEBUG_ON = true;

		try {
			System.out.println("initialize data");
			BDI332.writeMem(BASE_ADDR, FIRST_VAL, 4);
			int[] data = new int[BDI332.MAX_NOF_LONGS_FILL];
			for (int i = 0; i < data.length; i++) {
				data[i] = 5;
			}
			BDI332.fillMem(data, data.length);

			System.out.println("write data");
			BDI332.writeMem(BASE_ADDR, FIRST_VAL, 4);
			data = new int[10];
			for (int i = 0; i < data.length; i++) {
				data[i] = i;
			}
			BDI332.fillMem(data, data.length);
			System.out.println("Fill done");
			System.out.println("read back data");
			int firstResult = BDI332.readMem(BASE_ADDR, 4);
			if (firstResult != FIRST_VAL) {
				System.out.println("Error at 0: 0x"
						+ Integer.toHexString(firstResult) + " instead of 0x"
						+ Integer.toHexString(FIRST_VAL));
			}
			if (DEBUG_ON) {
				System.out.println("Compare first 0x"
						+ Integer.toHexString(firstResult) + " == 0x"
						+ Integer.toHexString(FIRST_VAL));
			}
			int[] result = BDI332.dumpMem(BDI332.MAX_NOF_LONGS_FILL);
			for (int i = 0; i < result.length; i++) {
				if (DEBUG_ON) {
					System.out.println("Compare " + i + ": 0x"
							+ Integer.toHexString(result[i]));

				}
			}
			System.out.println("Dump done");
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void button14() {

		final int BASE_ADDR = 0x105624;
		final int FIRST_VAL = 0xFF;
		final boolean DEBUG_ON = true;

		System.out.println("write data");
		try {
			BDI332.writeMem(BASE_ADDR, FIRST_VAL, 4);
			int[] data = new int[BDI332.MAX_NOF_LONGS_FILL];
			for (int i = 0; i < data.length; i++) {
				data[i] = i;
			}
			BDI332.fillMem(data, data.length);
			System.out.println("Fill done");
			System.out.println("read back data");
			int firstResult = BDI332.readMem(BASE_ADDR, 4);
			if (firstResult != FIRST_VAL) {
				System.out.println("Error at 0: 0x"
						+ Integer.toHexString(firstResult) + " instead of 0x"
						+ Integer.toHexString(FIRST_VAL));
			}
			if (DEBUG_ON) {
				System.out.println("Compare first 0x"
						+ Integer.toHexString(firstResult) + " == 0x"
						+ Integer.toHexString(FIRST_VAL));
			}
			int[] result = BDI332.dumpMem(BDI332.MAX_NOF_LONGS_FILL);
			for (int i = 0; i < result.length; i++) {
				if (data[i] != result[i]) {
					System.out.println("Error at " + i + ": 0x"
							+ Integer.toHexString(result[i]) + " instead of 0x"
							+ Integer.toHexString(data[i]));
				}
				if (DEBUG_ON) {
					System.out.println("Compare " + i + ": 0x"
							+ Integer.toHexString(result[i]) + " == 0x"
							+ Integer.toHexString(data[i]));

				}
			}
			System.out.println("Dump done");
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void button15() {
		System.out.println("resetUSB()");
		try {
			USBDevice.reset();
			Thread.sleep(500);
			USBDevice.open();
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public static void main(String[] args) {
	// boolean testRunning = true;
	//
	// while (testRunning) {
	// // testBdiTransaction();
	// // reset_target();
	// // freeze();
	// // go();
	// // System.out.println();
	//
	// try {
	// Thread.sleep(5000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// }
}
