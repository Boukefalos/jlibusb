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
		fastDownload();
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

}
