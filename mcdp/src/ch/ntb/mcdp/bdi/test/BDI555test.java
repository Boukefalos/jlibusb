package ch.ntb.mcdp.bdi.test;

import java.util.logging.Level;

import ch.ntb.mcdp.bdi.BDIException;
import ch.ntb.mcdp.bdi.MPC555;
import ch.ntb.mcdp.usb.DispatchException;
import ch.ntb.mcdp.utils.logger.LogUtil;
import ch.ntb.mcdp.utils.logger.McdpLogger;
import ch.ntb.usb.USBException;

public class BDI555test {

	private static McdpLogger logger = LogUtil.ch_ntb_mcdp_bdi_test;

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
			MPC555.reset_target();
			// assign pin to Freeze output
			MPC555.writeMem(0x02FC000, 0x40000, 4);
			// enable bus monitor, disable watchdog timer
			MPC555.writeMem(0x02FC004, 0x0FFFFFF83, 4);
			// SCCR, switch off EECLK for download
			MPC555.writeMem(0x02FC280, 0x08121C100, 4);
			logger.info("Is freeze asserted: " + MPC555.isFreezeAsserted());
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
			logger.info("isFreezeAsserted: " + MPC555.isFreezeAsserted());
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
			MPC555.break_();
			logger.info("break");
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
			MPC555.go();
			logger.info("go");
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
		final int BASE_ADDR = 0x800000, VALUE = 0x123456;
		try {
			MPC555.writeMem(BASE_ADDR, VALUE, 4);
			logger.info("writeMem: BASE_ADDR = 0x"
					+ Integer.toHexString(BASE_ADDR) + ", value = 0x"
					+ Integer.toHexString(VALUE));
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
		final int BASE_ADDR = 0x800000;
		try {
			StringBuffer sb = new StringBuffer("readMem: BASE_ADDR = 0x"
					+ Integer.toHexString(BASE_ADDR) + ", value = ");
			for (int i = 0; i < 10; i++) {
				sb.append("0x"
						+ Integer.toHexString(MPC555.readMem(BASE_ADDR + i * 4,
								4)) + "\n");
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

	private static void fastDownload() {
		final int BASE_ADDR = 0x800000;
		int[] testData = new int[MPC555.MAX_NOF_WORDS_FAST_DOWNLOAD];
		for (int i = 0; i < testData.length; i++) {
			testData[i] = i;
		}
		try {
			logger.info("fastDownload at BASE_ADDR = 0x"
					+ Integer.toHexString(BASE_ADDR) + ", length = "
					+ MPC555.MAX_NOF_WORDS_FAST_DOWNLOAD);
			MPC555.startFastDownload(BASE_ADDR);
			MPC555.fastDownload(testData, MPC555.MAX_NOF_WORDS_FAST_DOWNLOAD);
			MPC555.stopFastDownload();
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
		final int BASE_ADDR = 0x800000;
		try {
			StringBuffer sb = new StringBuffer("readMemSeq: BASE_ADDR = 0x"
					+ Integer.toHexString(BASE_ADDR) + "\n" + 0 + "\tData: 0x"
					+ Integer.toHexString(MPC555.readMem(BASE_ADDR, 4)) + "\n");
			for (int i = 1; i < 120; i++) {
				sb.append(i + "\tData: 0x"
						+ Integer.toHexString(MPC555.readMemSeq(4)) + "\n");
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

	public static void button1() {
		// System.out.println("testBdiTransaction()");
		// testBdiTransaction();
	}

	public static void button2() {
		reset_target();
	}

	public static void button3() {
		go();
	}

	public static void button4() {
		break_();
	}

	public static void button5() {
		freeze();
	}

	public static void button6() {
		writeMem();
	}

	public static void button7() {
		readMem();
	}

	public static void button8() {
		readMemSeq();
	}

	public static void button9() {
		final int SPR = 158;
		final int VALUE = 0x12345;
		try {
			int result = MPC555.readSPR(SPR);
			logger.info("readSPR(" + SPR + ") = 0x"
					+ Integer.toHexString(result));
			MPC555.writeSPR(SPR, VALUE);
			logger.info("writeSPR(" + SPR + ", 0x" + Integer.toHexString(VALUE)
					+ ")");
			result = MPC555.readSPR(SPR);
			logger.info("readSPR(" + SPR + ") = 0x"
					+ Integer.toHexString(result));
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

		// logger.info("not implemented!");

		// logger.info("hard_reset()");
		// try {
		// MPC555.hard_reset();
		// } catch (USBException e) {
		// e.printStackTrace();
		// } catch (DispatchException e) {
		// e.printStackTrace();
		// } catch (BDIException e) {
		// e.printStackTrace();
		// }
	}

	public static void button10() {
		// fastDownload();
		button12();
	}

	public static void button11() {
		Level oldLevel = LogUtil.ch_ntb_mcdp_bdi.getLevel();
		LogUtil.setLevel(LogUtil.ch_ntb_mcdp_bdi, Level.ALL);
		try {
			logger.info("test SPR");

			// valid spr registers:
			// CMPA–CMPD SPR 144 – SPR 147
			// CMPE–CMPF SPR 152, 153
			// CMPG–CMPH SPR 154, 155
			// ICTRL SPR 158
			// LCTRL1 SPR 156
			// LCTRL2 SPR 157
			// COUNTA SPR 150
			// COUNTB SPR 151
			// ECR SPR 148
			// DER SPR 149

			int REG = 152;
			int VALUE = 0x12345;
			MPC555.writeSPR(REG, VALUE);
			int result = MPC555.readSPR(REG);
			checkResult(VALUE, result);

			logger.info("test GPR");
			REG = 5;
			MPC555.writeGPR(REG, VALUE);
			result = MPC555.readGPR(REG);
			checkResult(VALUE, result);

			logger.info("test FPR");
			int TMP_MEM_ADDR = 0x800000;
			long LONG_VAL = 0x12345012345L;
			MPC555.writeFPR(REG, TMP_MEM_ADDR, LONG_VAL);
			long fprResult = MPC555.readFPR(REG, TMP_MEM_ADDR);
			if (fprResult != LONG_VAL) {
				logger.severe("value: 0x" + Long.toHexString(LONG_VAL)
						+ ", result: 0x" + Long.toHexString(fprResult));
			} else {
				logger
						.info("test ok: result: 0x"
								+ Long.toHexString(fprResult));
			}

			logger.info("test MSR");
			MPC555.writeMSR(VALUE);
			result = MPC555.readMSR();
			checkResult(VALUE, result);

			logger.info("test CR");
			MPC555.writeCR(VALUE);
			result = MPC555.readCR();
			checkResult(VALUE, result);

			logger.info("test CtrlReg");
			int MEM_ADDR = 0x2FC100;
			MPC555.writeMem(MEM_ADDR, VALUE, 4);
			result = MPC555.readMem(MEM_ADDR, 4);
			checkResult(VALUE, result);

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

		LogUtil.setLevel(LogUtil.ch_ntb_mcdp_bdi, oldLevel);
	}

	private static void checkResult(int value, int result) {
		if (value != result) {
			logger.severe("value: 0x" + Integer.toHexString(value)
					+ ", result: 0x" + Integer.toHexString(result));
		} else {
			logger.info("test ok: result: 0x" + Integer.toHexString(result));
		}
	}

	public static void button12() {
		final int BASE_ADDR = 0x3f9bf0;

		int[] data = new int[MPC555.MAX_NOF_WORDS_FAST_DOWNLOAD];

		data[0] = 0x9421ffb0;
		data[1] = 0xbf810040;
		data[2] = 0x3be10038;
		data[3] = 0x3bc30000;
		data[4] = 0x3ba40000;
		data[5] = 0x3c600030;
		data[6] = 0x38836102;
		data[7] = 0x38a08000;
		data[8] = 0xb0a40000;
		data[9] = 0x2c9e0000;
		data[10] = 0x4085004c;
		data[11] = 0x3cc00030;
		data[12] = 0x38e66100;
		data[13] = 0x39000000;
		data[14] = 0xb1070000;
		data[15] = 0x1f9d2710;
		data[16] = 0x339cffff;
		data[17] = 0x2f1c0000;
		data[18] = 0x4199fff8;
		data[19] = 0x3d200030;
		data[20] = 0x39496100;
		data[21] = 0x39608000;
		data[22] = 0xb16a0000;
		data[23] = 0x1f9d2710;
		data[24] = 0x339cffff;
		data[25] = 0x2f9c0000;
		data[26] = 0x419dfff8;
		data[27] = 0x33deffff;
		data[28] = 0x4280ffb4;
		data[29] = 0x383fffc8;
		data[30] = 0xbb810040;
		data[31] = 0x38210050;
		data[32] = 0x4e800020;
		data[33] = 0x9421ffb0;
		data[34] = 0x7c0802a6;
		data[35] = 0x9001004c;
		data[36] = 0xbfc10044;
		data[37] = 0x3be10038;
		data[38] = 0x90410014;
		data[39] = 0x7fcc42e6;
		data[40] = 0x387e0000;
		data[41] = 0x42800008;
		data[42] = 0x7fe00008;
		data[43] = 0x383fffc8;
		data[44] = 0xbbc10044;
		data[45] = 0x8001004c;
		data[46] = 0x7c0803a6;
		data[47] = 0x38210050;
		data[48] = 0x4e800020;
		data[49] = 0x9421ffb8;
		data[50] = 0x7c0802a6;
		data[51] = 0x90010044;
		data[52] = 0xbfa10038;
		data[53] = 0x3be10038;
		data[54] = 0x90410014;
		data[55] = 0x3bc30000;
		data[56] = 0x3ba40000;
		data[57] = 0x4bffffa1;
		data[58] = 0x90620018;
		data[59] = 0x81820018;
		data[60] = 0x3c60000f;
		data[61] = 0x38834240;
		data[62] = 0x7cac23d7;
		data[63] = 0x40800008;
		data[64] = 0x30a5ffff;
		data[65] = 0x90be0000;
		data[66] = 0x80c20018;
		data[67] = 0x38e003e8;
		data[68] = 0x7d063bd7;
		data[69] = 0x40800008;
		data[70] = 0x3108ffff;
		data[71] = 0x392003e8;
		data[72] = 0x7d484bd6;
		data[73] = 0x7d4a49d6;
		data[74] = 0x7d4a4011;
		data[75] = 0x40800008;
		data[76] = 0x314a03e8;
		data[77] = 0xb15d0000;
		data[78] = 0x383fffc8;
		data[79] = 0xbba10038;
		data[80] = 0x80010044;
		data[81] = 0x7c0803a6;
		data[82] = 0x38210048;
		data[83] = 0x4e800020;
		data[84] = 0x9421ff98;
		data[85] = 0xbf21004c;
		data[86] = 0x3be10038;
		data[87] = 0x3bc30000;
		data[88] = 0x3ba40000;
		data[89] = 0x3b7e0000;
		data[90] = 0x3b800000;
		data[91] = 0x7c9be800;
		data[92] = 0x40840070;
		data[93] = 0xa33b0000;
		data[94] = 0x7f9ccb78;
		data[95] = 0x3b40000f;
		data[96] = 0x578b0001;
		data[97] = 0x4182000c;
		data[98] = 0x6b8c8000;
		data[99] = 0x6d9c0810;
		data[100] = 0x57830801;

		try {
			StringBuffer sb = new StringBuffer("dumpData: BASE_ADDR = 0x"
					+ Integer.toHexString(BASE_ADDR) + "\n" + 0 + "\tData: 0x"
					+ Integer.toHexString(MPC555.readMem(BASE_ADDR, 4)) + "\n");
			for (int i = 1; i < 120; i++) {
				sb.append(i + "\tData: 0x"
						+ Integer.toHexString(MPC555.readMemSeq(4)) + "\n");
			}
			logger.info(sb.toString());

			logger.info("fastDownload at BASE_ADDR = 0x"
					+ Integer.toHexString(BASE_ADDR) + ", length = "
					+ MPC555.MAX_NOF_WORDS_FAST_DOWNLOAD);
			MPC555.startFastDownload(BASE_ADDR);
			MPC555.fastDownload(data, MPC555.MAX_NOF_WORDS_FAST_DOWNLOAD);
			MPC555.stopFastDownload();

			sb = new StringBuffer("dumpData: BASE_ADDR = 0x"
					+ Integer.toHexString(BASE_ADDR) + "\n" + 0 + "\tData: 0x"
					+ Integer.toHexString(MPC555.readMem(BASE_ADDR, 4)) + "\n");
			for (int i = 1; i < 120; i++) {
				sb.append(i + "\tData: 0x"
						+ Integer.toHexString(MPC555.readMemSeq(4)) + "\n");
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

	private static int[] getSampleData(int length) {
		int[] data = new int[length];
		for (int i = 0; i < data.length; i++) {
			data[i] = (int) (Math.random() * Integer.MAX_VALUE);
		}
		return data;
	}

	private static void testFill(int baseAddr) throws USBException,
			DispatchException, BDIException {
		// int length = (int) (1 + Math.random()
		// * (MPC555.MAX_NOF_WORDS_FAST_DOWNLOAD - 1));
		int length = 101;
		int[] data = getSampleData(length);
		logger.info("BaseAddr: 0x" + Integer.toHexString(baseAddr)
				+ ", dataLength: " + data.length);
		// download data
		MPC555.startFastDownload(baseAddr);
		MPC555.fastDownload(data, data.length);
		MPC555.stopFastDownload();
		// read back data
		int[] compare = new int[data.length];
		compare[0] = MPC555.readMem(baseAddr, 4);
		for (int i = 1; i < compare.length; i++) {
			compare[i] = MPC555.readMemSeq(4);
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < compare.length; i++) {
			if (compare[i] != data[i]) {
				sb.append("Error at " + i + ", addr: 0x"
						+ Integer.toHexString(baseAddr + i * 4)
						+ ", expected: 0x" + Integer.toHexString(data[i])
						+ ", read: " + Integer.toHexString(compare[i]) + "\n");
			}
		}
		if (sb.length() > 0) {
			logger.warning(sb.toString());
		}
	}

	public static void runFillTests(int nofRuns) {
		try {
			// BR0 = 01000003H(* 16777219*)
			// OR0 = 0FFC00020H(* -4194272*)
			// BR1 = 0800003H(* 8388611*)
			// OR1 = 0FFE00020H(* -2097120*)
			// DMBR = 03H(* 3*)
			// DMOR = 07E000000H(* 2113929216*)
			// ICTRL = 07H(* 7*)
			// RSR = 00H(* 0*)
			// DER = 031C7400FH(* 835141647*)
			// SRR1 = 03802H(* 14338*)

			MPC555.writeMem(0x2FC100, 0x01000003, 4);
			MPC555.writeMem(0x2FC104, 0x0FFC00020, 4);
			MPC555.writeMem(0x2FC108, 0x0800003, 4);
			MPC555.writeMem(0x2FC10C, 0x0FFE00020, 4);
			MPC555.writeMem(0x2FC140, 3, 4);
			MPC555.writeMem(0x2FC144, 0x7E000000, 4);
			MPC555.writeSPR(158, 0x07);
			MPC555.writeMem(0x2FC288, -1, 4);
			MPC555.writeSPR(149, 0x031C7400F);
			MPC555.writeSPR(27, 0x03802);

			for (int i = 0; i < nofRuns; i++) {
				testFill(0x03F9800);
				testFill(0x03F9BF0);
				testFill(0x03F9D84);
				testFill(0x03F9AA8);
				testFill(0x03F9AE0);
				testFill(0x0);
				testFill(0x0594);
				testFill(0x0D94);
				testFill(0x01EA4);
				testFill(0x0802000);
				testFill(0x0802C10);
				testFill(0x0803480);
			}
		} catch (USBException e) {
			e.printStackTrace();
		} catch (DispatchException e) {
			e.printStackTrace();
		} catch (BDIException e) {
			e.printStackTrace();
		}
	}

	public static void button13() {
		logger.info("extensive fill test");
		runFillTests(100);
		logger.info("test done");
	}

	public static void button14() {

		try {
			logger.info("start FillTest");
			FillTest.doFill();
			logger.info("start compare");
			FillTest.doCompare();
			logger.info("FillTest done");
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void button15() {

		try {
			logger.info("start FillShort");
			FillTest.doFill2();
			logger.info("start compare");
			FillTest.doCompare();
			logger.info("FillShort done");
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
