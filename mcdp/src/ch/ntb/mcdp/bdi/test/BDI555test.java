package ch.ntb.mcdp.bdi.test;

import ch.ntb.mcdp.bdi.BDI555;
import ch.ntb.mcdp.bdi.BDIException;
import ch.ntb.mcdp.usb.DataPacket;
import ch.ntb.mcdp.usb.DispatchException;
import ch.ntb.usb.USBException;

public class BDI555test {

	// private static void testBdiTransaction() {
	// // test bdi transaction
	// DataPacket result = null;
	// try {
	// result = BDI555.transfer(false, false, 0x7FD69BA6);
	// result = BDI555.transfer(false, false, 0x60000000);
	// } catch (USBException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// } catch (DispatchException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// } catch (BDIException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// if (result != null) {
	// result.toString();
	// }
	// }

	private static void reset_target() {
		try {
			BDI555.reset_target();
			// assign pin to Freeze output
			BDI555.writeMem(0x02FC000, 0x40000, 4);
			// enable bus monitor, disable watchdog timer
			BDI555.writeMem(0x02FC004, 0x0FFFFFF83, 4);
			// SCCR, switch off EECLK for download
			BDI555.writeMem(0x02FC280, 0x08121C100, 4);
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
					.println("isFreezeAsserted: " + BDI555.isFreezeAsserted());
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
			BDI555.break_();
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
			BDI555.go();
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
		try {
			BDI555.writeMem(0x800000, 0x123456, 4);
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
		try {
			StringBuffer sb = new StringBuffer("0x"
					+ Integer.toHexString(BDI555.readMem(0x800000, 4)) + "\n");
			sb.append("0x" + Integer.toHexString(BDI555.readMem(0x800004, 4))
					+ "\n");
			sb.append("0x" + Integer.toHexString(BDI555.readMem(0x800008, 4))
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

	private static void fastDownload() {
		int[] testData = new int[120];
		for (int i = 0; i < testData.length; i++) {
			testData[i] = i;
		}
		try {
			BDI555.startFastDownload(0x800000);
			BDI555.fastDownload(testData, BDI555.MAX_NOF_WORDS_FAST_DOWNLOAD);
			BDI555.stopFastDownload();
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

	private static void readMemSeq() {
		int startAddr = 0x800000;
		try {
			StringBuffer sb = new StringBuffer(0 + "\tData: 0x"
					+ Integer.toHexString(BDI555.readMem(startAddr, 4)) + "\n");
			for (int i = 1; i < 120; i++) {
				sb.append(i + "\tData: 0x"
						+ Integer.toHexString(BDI555.readMemSeq(4)) + "\n");
			}
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

	public static void button1() {
		// System.out.println("testBdiTransaction()");
		// testBdiTransaction();
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
		System.out.println("readMemSeq()");
		readMemSeq();
	}

	public static void button9() {
		System.out.println("");
	}

	public static void button10() {
		System.out.println("fastDownload()");
		fastDownload();
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
