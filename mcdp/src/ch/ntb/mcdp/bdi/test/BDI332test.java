package ch.ntb.mcdp.bdi.test;

import ch.ntb.mcdp.bdi.BDIException;
import ch.ntb.mcdp.bdi.MC68332;
import ch.ntb.mcdp.mc68332.IMCBTargetBoard;
import ch.ntb.mcdp.usb.DataPacket;
import ch.ntb.mcdp.usb.DispatchException;
import ch.ntb.mcdp.usb.USBDevice;
import ch.ntb.mcdp.utils.logger.LogUtil;
import ch.ntb.mcdp.utils.logger.McdpLogger;
import ch.ntb.usb.USBException;

public class BDI332test {

	private static McdpLogger logger = LogUtil.ch_ntb_mcdp_bdi_test;

	private static void testBdiTransaction() {
		// test bdi transaction
		DataPacket result = null;
		try {
			result = MC68332.transfer(0x0C00);
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
			logger.info(result.toString());
		}
	}

	private static void reset_target() {
		try {
			MC68332.reset_target();
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
			logger.info("isFreezeAsserted: " + MC68332.isFreezeAsserted());
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
			MC68332.break_();
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
			MC68332.go();
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
			MC68332.writeMem(BASE_ADDR, 0x123456, 4);
			MC68332.writeMem(BASE_ADDR + 4, 0x123457, 4);
			MC68332.writeMem(BASE_ADDR + 8, 0x123458, 4);
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
					+ Integer.toHexString(MC68332.readMem(BASE_ADDR, 4)) + "\n");
			sb.append("0x"
					+ Integer.toHexString(MC68332.readMem(BASE_ADDR + 4, 4))
					+ "\n");
			sb.append("0x"
					+ Integer.toHexString(MC68332.readMem(BASE_ADDR + 8, 4))
					+ "\n");
			logger.info(sb.toString());
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
		logger.info("testBdiTransaction()");
		testBdiTransaction();
	}

	public static void button2() {
		logger.info("reset_target()");
		reset_target();
	}

	public static void button3() {
		logger.info("go()");
		go();
	}

	public static void button4() {
		logger.info("break_()");
		break_();
	}

	public static void button5() {
		logger.info("freeze()");
		freeze();
	}

	public static void button6() {
		logger.info("writeMem()");
		writeMem();
	}

	public static void button7() {
		logger.info("readMem()");
		readMem();
	}

	public static void button8() {

		final int BASE_ADDR = 0x105624;
		int[] result;

		logger.info("dump()");
		try {
			logger.info("Data: 0x"
					+ Integer.toHexString(MC68332.readMem(BASE_ADDR, 4)) + " ");
			result = MC68332.dumpMem(MC68332.MAX_NOF_LONGS_FILL);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				sb.append("0x" + Integer.toHexString(result[i]) + " ");
			}
			logger.info(sb.toString());

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
			MC68332.nop();
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

		logger.info("fill");
		try {
			MC68332.writeMem(BASE_ADDR, 0, 4);
			int[] data = new int[MC68332.MAX_NOF_LONGS_FILL];
			for (int i = 0; i < data.length; i++) {
				data[i] = i;
			}
			MC68332.fillMem(data, data.length);
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
		logger.info("initTarget()");
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

		final int BASE_ADDR = 0x105624;
		final int DATA = 0x00ff00ff;
		final int OFFSET = 0x06 * 4;
		final int LENGTH = 0x04;

		try {
			logger.info("Fill (1 to data.length)");
			MC68332.writeMem(BASE_ADDR, 0, 4);
			int[] data = new int[MC68332.MAX_NOF_LONGS_FILL];
			for (int i = 0; i < data.length; i++) {
				data[i] = i + 1;
			}
			MC68332.fillMem(data, data.length);
			logger.info("writing byte " + (OFFSET / 4) + " to "
					+ ((OFFSET / 4) + LENGTH) + " with 0x"
					+ Integer.toHexString(DATA));
			MC68332.writeMem(BASE_ADDR + OFFSET, DATA, 4);
			for (int i = 0; i < LENGTH; i++) {
				data[i] = DATA;
			}
			MC68332.fillMem(data, LENGTH);
			logger.info((LENGTH + 1) + " bytes written");
			logger.info("dump data");
			int firstInt = MC68332.readMem(BASE_ADDR, 4);
			int[] result = MC68332.dumpMem(MC68332.MAX_NOF_LONGS_FILL);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				sb.append("0x" + Integer.toHexString(result[i]) + " ");
			}
			logger.info("Data: 0x" + Integer.toHexString(firstInt) + " "
					+ sb.toString());
			logger.info("Done");
		} catch (BDIException e) {
			e.printStackTrace();
		} catch (USBException e) {
			e.printStackTrace();
		} catch (DispatchException e) {
			e.printStackTrace();
		}
	}

	public static void button13() {
		final int BASE_ADDR = 0x105624;
		final int FIRST_VAL = 0xFF;

		try {
			logger.info("initialize data");
			MC68332.writeMem(BASE_ADDR, FIRST_VAL, 4);
			int[] data = new int[MC68332.MAX_NOF_LONGS_FILL];
			for (int i = 0; i < data.length; i++) {
				data[i] = 5;
			}
			MC68332.fillMem(data, data.length);

			logger.info("write data");
			MC68332.writeMem(BASE_ADDR, FIRST_VAL, 4);
			data = new int[10];
			for (int i = 0; i < data.length; i++) {
				data[i] = i;
			}
			MC68332.fillMem(data, data.length);
			logger.info("Fill done");
			logger.info("read back data");
			int firstResult = MC68332.readMem(BASE_ADDR, 4);
			if (firstResult != FIRST_VAL) {
				logger.warning("Error at 0: 0x"
						+ Integer.toHexString(firstResult) + " instead of 0x"
						+ Integer.toHexString(FIRST_VAL));
			}
			logger.fine("Compare first 0x" + Integer.toHexString(firstResult)
					+ " == 0x" + Integer.toHexString(FIRST_VAL));
			int[] result = MC68332.dumpMem(MC68332.MAX_NOF_LONGS_FILL);
			for (int i = 0; i < result.length; i++) {
				logger.fine("Compare " + i + ": 0x"
						+ Integer.toHexString(result[i]));
			}
			logger.info("Dump done");
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

		logger.info("write data");
		try {
			MC68332.writeMem(BASE_ADDR, FIRST_VAL, 4);
			int[] data = new int[MC68332.MAX_NOF_LONGS_FILL];
			for (int i = 0; i < data.length; i++) {
				data[i] = i;
			}
			MC68332.fillMem(data, data.length);
			logger.info("Fill done");
			logger.info("read back data");
			int firstResult = MC68332.readMem(BASE_ADDR, 4);
			if (firstResult != FIRST_VAL) {
				logger.warning("Error at 0: 0x"
						+ Integer.toHexString(firstResult) + " instead of 0x"
						+ Integer.toHexString(FIRST_VAL));
			}
			logger.fine("Compare first 0x" + Integer.toHexString(firstResult)
					+ " == 0x" + Integer.toHexString(FIRST_VAL));
			int[] result = MC68332.dumpMem(MC68332.MAX_NOF_LONGS_FILL);
			for (int i = 0; i < result.length; i++) {
				if (data[i] != result[i]) {
					logger.warning("Error at " + i + ": 0x"
							+ Integer.toHexString(result[i]) + " instead of 0x"
							+ Integer.toHexString(data[i]));
				}
				logger.fine("Compare " + i + ": 0x"
						+ Integer.toHexString(result[i]) + " == 0x"
						+ Integer.toHexString(data[i]));

			}
			logger.info("Dump done");
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
		logger.info("resetUSB()");
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

	public static void button16() {
		final int BASE_ADDR = 0x105624;
		final int DATA = 0x00ff00ff;
		final int OFFSET = (MC68332.MAX_NOF_LONGS_FILL - 2) * 4;
		final int LENGTH = 0x04;
		final int DUMP_BASE = BASE_ADDR + (MC68332.MAX_NOF_LONGS_FILL / 2) * 4;

		try {
			logger.info("REPLACE at the end");
			logger.info("Fill first");
			MC68332.writeMem(BASE_ADDR, 0, 4);
			int[] data = new int[MC68332.MAX_NOF_LONGS_FILL];
			for (int i = 0; i < data.length; i++) {
				data[i] = i + 1;
			}
			MC68332.fillMem(data, data.length);
			logger.info("Fill second");
			MC68332.writeMem(BASE_ADDR + (MC68332.MAX_NOF_LONGS_FILL + 1) * 4,
					0, 4);
			for (int i = 0; i < data.length; i++) {
				data[i] = MC68332.MAX_NOF_LONGS_FILL + i + 2;
			}
			MC68332.fillMem(data, data.length);
			logger.info("Dump from base: 0x" + Integer.toHexString(DUMP_BASE));
			int firstInt = MC68332.readMem(DUMP_BASE, 4);
			int[] result = MC68332.dumpMem(MC68332.MAX_NOF_LONGS_FILL);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				sb.append("0x" + Integer.toHexString(result[i]) + " ");
			}
			logger.info("Data: 0x" + Integer.toHexString(firstInt) + " "
					+ sb.toString());

			logger.info("writing byte " + (OFFSET / 4) + " to "
					+ ((OFFSET / 4) + LENGTH) + " with 0x"
					+ Integer.toHexString(DATA));
			MC68332.writeMem(BASE_ADDR + OFFSET, DATA, 4);
			for (int i = 0; i < LENGTH; i++) {
				data[i] = DATA;
			}
			MC68332.fillMem(data, LENGTH);
			logger.info((LENGTH + 1) + " bytes written");
			logger.info("dump data from base: 0x"
					+ Integer.toHexString(DUMP_BASE));
			firstInt = MC68332.readMem(DUMP_BASE, 4);
			result = MC68332.dumpMem(MC68332.MAX_NOF_LONGS_FILL);
			sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				sb.append("0x" + Integer.toHexString(result[i]) + " ");
			}
			logger.info("Data: 0x" + Integer.toHexString(firstInt) + " "
					+ sb.toString());
			logger.info("Done");
		} catch (BDIException e) {
			e.printStackTrace();
		} catch (USBException e) {
			e.printStackTrace();
		} catch (DispatchException e) {
			e.printStackTrace();
		}

	}

	public static void button17() {

	}

	public static void button18() {

	}

	public static void button19() {

	}

	public static void button20() {
		final int BASE_ADDR = 0x01004E0;
		try {
			StringBuffer sb = new StringBuffer("0x"
					+ Integer.toHexString(MC68332.readMem(BASE_ADDR, 4)) + "\n");
			sb.append("0x"
					+ Integer.toHexString(MC68332.readMem(BASE_ADDR + 4, 4))
					+ "\n");
			sb.append("0x"
					+ Integer.toHexString(MC68332.readMem(BASE_ADDR + 8, 4))
					+ "\n");
			logger.info(sb.toString());
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
