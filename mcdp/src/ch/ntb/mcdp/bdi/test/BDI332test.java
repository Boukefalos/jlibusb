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

	public static MC68332 bdi;

	private static void testBdiTransaction() {
		// test bdi transaction
		DataPacket result = null;
		try {
			result = bdi.transfer(0x0C00);
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
			bdi.reset_target();
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
			logger.info("isFreezeAsserted: " + bdi.isFreezeAsserted());
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
			bdi.break_();
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
			bdi.go();
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
			bdi.writeMem(BASE_ADDR, 0x123456, 4);
			bdi.writeMem(BASE_ADDR + 4, 0x123457, 4);
			bdi.writeMem(BASE_ADDR + 8, 0x123458, 4);
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
					+ Integer.toHexString(bdi.readMem(BASE_ADDR, 4)) + "\n");
			sb.append("0x" + Integer.toHexString(bdi.readMem(BASE_ADDR + 4, 4))
					+ "\n");
			sb.append("0x" + Integer.toHexString(bdi.readMem(BASE_ADDR + 8, 4))
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
					+ Integer.toHexString(bdi.readMem(BASE_ADDR, 4)) + " ");
			result = bdi.dumpMem(bdi.getMaxNofLongs());
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
			bdi.nop();
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
			bdi.writeMem(BASE_ADDR, 0, 4);
			int[] data = new int[bdi.getMaxNofLongs()];
			for (int i = 0; i < data.length; i++) {
				data[i] = i;
			}
			bdi.fillMem(data, data.length);
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
			IMCBTargetBoard imcb = new IMCBTargetBoard(bdi);
			imcb.init();
		} catch (Exception e) {
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
			bdi.writeMem(BASE_ADDR, 0, 4);
			int[] data = new int[bdi.getMaxNofLongs()];
			for (int i = 0; i < data.length; i++) {
				data[i] = i + 1;
			}
			bdi.fillMem(data, data.length);
			logger.info("writing byte " + (OFFSET / 4) + " to "
					+ ((OFFSET / 4) + LENGTH) + " with 0x"
					+ Integer.toHexString(DATA));
			bdi.writeMem(BASE_ADDR + OFFSET, DATA, 4);
			for (int i = 0; i < LENGTH; i++) {
				data[i] = DATA;
			}
			bdi.fillMem(data, LENGTH);
			logger.info((LENGTH + 1) + " bytes written");
			logger.info("dump data");
			int firstInt = bdi.readMem(BASE_ADDR, 4);
			int[] result = bdi.dumpMem(bdi.getMaxNofLongs());
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
			bdi.writeMem(BASE_ADDR, FIRST_VAL, 4);
			int[] data = new int[bdi.getMaxNofLongs()];
			for (int i = 0; i < data.length; i++) {
				data[i] = 5;
			}
			bdi.fillMem(data, data.length);

			logger.info("write data");
			bdi.writeMem(BASE_ADDR, FIRST_VAL, 4);
			data = new int[10];
			for (int i = 0; i < data.length; i++) {
				data[i] = i;
			}
			bdi.fillMem(data, data.length);
			logger.info("Fill done");
			logger.info("read back data");
			int firstResult = bdi.readMem(BASE_ADDR, 4);
			if (firstResult != FIRST_VAL) {
				logger.warning("Error at 0: 0x"
						+ Integer.toHexString(firstResult) + " instead of 0x"
						+ Integer.toHexString(FIRST_VAL));
			}
			logger.fine("Compare first 0x" + Integer.toHexString(firstResult)
					+ " == 0x" + Integer.toHexString(FIRST_VAL));
			int[] result = bdi.dumpMem(bdi.getMaxNofLongs());
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
			bdi.writeMem(BASE_ADDR, FIRST_VAL, 4);
			int[] data = new int[bdi.getMaxNofLongs()];
			for (int i = 0; i < data.length; i++) {
				data[i] = i;
			}
			bdi.fillMem(data, data.length);
			logger.info("Fill done");
			logger.info("read back data");
			int firstResult = bdi.readMem(BASE_ADDR, 4);
			if (firstResult != FIRST_VAL) {
				logger.warning("Error at 0: 0x"
						+ Integer.toHexString(firstResult) + " instead of 0x"
						+ Integer.toHexString(FIRST_VAL));
			}
			logger.fine("Compare first 0x" + Integer.toHexString(firstResult)
					+ " == 0x" + Integer.toHexString(FIRST_VAL));
			int[] result = bdi.dumpMem(bdi.getMaxNofLongs());
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
		final int OFFSET = (bdi.getMaxNofLongs() - 2) * 4;
		final int LENGTH = 0x04;
		final int DUMP_BASE = BASE_ADDR + (bdi.getMaxNofLongs() / 2) * 4;

		try {
			logger.info("REPLACE at the end");
			logger.info("Fill first");
			bdi.writeMem(BASE_ADDR, 0, 4);
			int[] data = new int[bdi.getMaxNofLongs()];
			for (int i = 0; i < data.length; i++) {
				data[i] = i + 1;
			}
			bdi.fillMem(data, data.length);
			logger.info("Fill second");
			bdi.writeMem(BASE_ADDR + (bdi.getMaxNofLongs() + 1) * 4, 0, 4);
			for (int i = 0; i < data.length; i++) {
				data[i] = bdi.getMaxNofLongs() + i + 2;
			}
			bdi.fillMem(data, data.length);
			logger.info("Dump from base: 0x" + Integer.toHexString(DUMP_BASE));
			int firstInt = bdi.readMem(DUMP_BASE, 4);
			int[] result = bdi.dumpMem(bdi.getMaxNofLongs());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				sb.append("0x" + Integer.toHexString(result[i]) + " ");
			}
			logger.info("Data: 0x" + Integer.toHexString(firstInt) + " "
					+ sb.toString());

			logger.info("writing byte " + (OFFSET / 4) + " to "
					+ ((OFFSET / 4) + LENGTH) + " with 0x"
					+ Integer.toHexString(DATA));
			bdi.writeMem(BASE_ADDR + OFFSET, DATA, 4);
			for (int i = 0; i < LENGTH; i++) {
				data[i] = DATA;
			}
			bdi.fillMem(data, LENGTH);
			logger.info((LENGTH + 1) + " bytes written");
			logger.info("dump data from base: 0x"
					+ Integer.toHexString(DUMP_BASE));
			firstInt = bdi.readMem(DUMP_BASE, 4);
			result = bdi.dumpMem(bdi.getMaxNofLongs());
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
		logger.info("test read/write mem byte/word");
		final int BASE_ADDR = 0x105624;
		final int DATA = 0x12345678;

		try {
			logger.info("read 4 bytes at: 0x" + Integer.toHexString(BASE_ADDR)
					+ ", value: 0x"
					+ Integer.toHexString(bdi.readMem(BASE_ADDR, 4)));
			logger.info("read 2 bytes at: 0x" + Integer.toHexString(BASE_ADDR)
					+ ", value: 0x"
					+ Integer.toHexString(bdi.readMem(BASE_ADDR, 2)));
			logger.info("read 1 byte at: 0x" + Integer.toHexString(BASE_ADDR)
					+ ", value: 0x"
					+ Integer.toHexString(bdi.readMem(BASE_ADDR, 1)));
			logger.info("write 1 byte at: 0x" + Integer.toHexString(BASE_ADDR)
					+ ", value: 0x" + Integer.toHexString(DATA));
			bdi.writeMem(BASE_ADDR, DATA, 1);
			logger.info("read 4 bytes at: 0x" + Integer.toHexString(BASE_ADDR)
					+ ", value: 0x"
					+ Integer.toHexString(bdi.readMem(BASE_ADDR, 4)));
			logger.info("write 2 byte at: 0x" + Integer.toHexString(BASE_ADDR)
					+ ", value: 0x" + Integer.toHexString(DATA));
			bdi.writeMem(BASE_ADDR, DATA, 2);
			logger.info("read 4 bytes at: 0x" + Integer.toHexString(BASE_ADDR)
					+ ", value: 0x"
					+ Integer.toHexString(bdi.readMem(BASE_ADDR, 4)));
			logger.info("write 4 byte at: 0x" + Integer.toHexString(BASE_ADDR)
					+ ", value: 0x" + Integer.toHexString(DATA));
			bdi.writeMem(BASE_ADDR, DATA, 4);
			logger.info("read 4 bytes at: 0x" + Integer.toHexString(BASE_ADDR)
					+ ", value: 0x"
					+ Integer.toHexString(bdi.readMem(BASE_ADDR, 4)));
		} catch (USBException e) {
			e.printStackTrace();
		} catch (DispatchException e) {
			e.printStackTrace();
		} catch (BDIException e) {
			e.printStackTrace();
		}
		logger.info("test done");
	}

	private static void dump(int baseAddr, int size) throws USBException,
			DispatchException, BDIException {
		int dumpSize = 0;
		if (size > 2) {
			dumpSize = bdi.getMaxNofLongs();
		} else {
			dumpSize = bdi.getMaxNofBytesWords();
		}
		logger.info("read " + size + " byte(s) at 0x"
				+ Integer.toHexString(baseAddr) + ", value: "
				+ Integer.toHexString(bdi.readMem(baseAddr, size)));
		int[] result = bdi.dumpMem(dumpSize);
		StringBuffer sb = new StringBuffer("data: ");
		for (int i = 0; i < result.length; i++) {
			sb.append("0x" + Integer.toHexString(result[i]) + " ");
		}
		logger.info(sb.toString());
	}

	private static void fill(int baseAddr, int size) throws USBException,
			DispatchException, BDIException {
		int fillSize = 0;
		if (size > 2) {
			fillSize = bdi.getMaxNofLongs();
		} else {
			fillSize = bdi.getMaxNofBytesWords();
		}
		int[] data = new int[fillSize];
		for (int i = 0; i < data.length; i++) {
			data[i] = i;
		}
		logger.info("fill " + data.length + " integers with size " + size
				+ " byte(s)");
		bdi.writeMem(baseAddr, 0, size);
		bdi.fillMem(data, data.length);
	}

	public static void button18() {
		final int BASE_ADDR = 0x105624;

		int[] data = new int[bdi.getMaxNofBytesWords()];
		for (int i = 0; i < data.length; i++) {
			data[i] = i;
		}
		try {
			IMCBTargetBoard imcb = new IMCBTargetBoard(bdi);
			imcb.init();
			fill(BASE_ADDR, 4);
			// TODO: this does produce an error why???
			imcb.init();
			dump(BASE_ADDR, 4);
			imcb.init();
			fill(BASE_ADDR, 2);
			imcb.init();
			dump(BASE_ADDR, 2);
			imcb.init();
			fill(BASE_ADDR, 1);
			imcb.init();
			dump(BASE_ADDR, 1);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void button19() {
		logger.info("test read/write register");

		try {
			int REG = 0x8;
			int VALUE = 0x12345;
			logger.info("test SysReg (ATEMP)");
			bdi.writeSysReg(REG, VALUE);
			int result = bdi.readSysReg(REG);
			checkResult(VALUE, result);

			REG = 0x5;
			logger.info("test UserReg (D5)");
			bdi.writeUserReg(REG, VALUE);
			result = bdi.readUserReg(REG);
			checkResult(VALUE, result);

			REG = 0xD;
			logger.info("test UserReg (A5)");
			bdi.writeUserReg(REG, VALUE);
			result = bdi.readUserReg(REG);
			checkResult(VALUE, result);

			// Does only work after LoadRam!
			//
			// REG = 0xFFFFFA00;
			// logger.info("test ctrlReg (SIMCR)");
			// bdi.writeMem(REG, VALUE, 4);
			// result = bdi.readMem(REG, 4);
			// checkResult(VALUE, result);

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

	private static void checkResult(int value, int result) {
		if (value != result) {
			logger.severe("value: 0x" + Integer.toHexString(value)
					+ ", result: 0x" + Integer.toHexString(result));
		} else {
			logger.info("test ok: result: 0x" + Integer.toHexString(result));
		}
	}

	public static void button20() {
		final int BASE_ADDR = 0x01004E0;
		try {
			StringBuffer sb = new StringBuffer("0x"
					+ Integer.toHexString(bdi.readMem(BASE_ADDR, 4)) + "\n");
			sb.append("0x" + Integer.toHexString(bdi.readMem(BASE_ADDR + 4, 4))
					+ "\n");
			sb.append("0x" + Integer.toHexString(bdi.readMem(BASE_ADDR + 8, 4))
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
