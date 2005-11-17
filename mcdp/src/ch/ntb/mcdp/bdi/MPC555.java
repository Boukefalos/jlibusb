package ch.ntb.mcdp.bdi;

import ch.ntb.mcdp.usb.DataPacket;
import ch.ntb.mcdp.usb.Dispatch;
import ch.ntb.mcdp.usb.DispatchException;
import ch.ntb.mcdp.usb.USBDevice;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;

public class MPC555 {

	// BDI subtypes
	/**
	 * 35 Bit Packet to BDI
	 */
	private static final byte STYPE_BDI_35IN = 0x01;

	/**
	 * 35 Bit Packet from BDI
	 */
	private static final byte STYPE_BDI_35OUT = 0x02;

	/**
	 * Fast Download Data
	 */
	private static final byte STYPE_BDI_35FD_DATA = 0x05;

	/**
	 * Hard Reset
	 */
	private static final byte STYPE_BDI_HARD_RESET_555 = 0x06;

	/**
	 * Get status of Freeze-Signal
	 */
	private static final byte STYPE_BDI_GET_FREEZE = 0x08;

	/**
	 * Fast Download successfully finished
	 */
	private static final byte STYPE_BDI_SUCCESS_FD = 0x60;

	/**
	 * HARD_RESET done
	 */
	private static final byte STYPE_BDI_HARD_RESET_SUCCESS = 0x61;

	/**
	 * Freeze-Signal status (length = 1 byte)
	 */
	private static final byte STYPE_BDI_FREEZE_RESULT = 0x62;

	/**
	 * Unknown STYPE
	 */
	private static final byte STYPE_BDI_UNKNOWN_STYPE = 0x70;

	/**
	 * Error if length in FD (Fast Download) packet too small
	 */
	private static final byte STYPE_BDI_ERROR_FD_LENGTH = 0x71;

	/**
	 * General FD Error
	 */
	private static final byte STYPE_BDI_ERROR_FD_GENERAL = 0x72;

	/**
	 * General FD Error
	 */
	private static final byte STYPE_BDI_ERROR_TIMEOUT = 0x73;

	private static final int BDI_DATA35_LENGTH = 5;

	/**
	 * no operation, nop = ori 0,0,0
	 */
	private static final int NOP = 0x60000000;

	/**
	 * return from interrupt
	 */
	private static final int RFI = 0x4C000064;

	/**
	 * mtspr DPDR, Rx
	 */
	private static final int MRTDPDR = 0x7C169BA6;

	/**
	 * mfspr Rx, DPDR
	 */
	private static final int MDPDRTR = 0x7C169AA6;

	/**
	 * mtspr spr, R30
	 */
	private static final int MR30TSPR = 0x7FC003A6;

	/**
	 * mfspr R30, spr
	 */
	private static final int MSPRTR30 = 0x7FC002A6;

	/**
	 * mftbr R30, tbr
	 */
	private static final int MTBRTR30 = 0x7FC002E6;

	private static final int NULL_INDICATION = -1;

	public static final int MAX_NOF_WORDS_FAST_DOWNLOAD = ((USB.MAX_DATA_SIZE - DataPacket.PACKET_MIN_LENGTH) / BDI_DATA35_LENGTH);

	private static boolean fastDownloadStarted = false;

	private static boolean targetInDebugMode = false;

	private static byte[] sendData;

	private static int gpr30, gpr31, ecr;

	static {
		sendData = new byte[USB.MAX_DATA_SIZE];
	}

	private static void initPacket(byte STYPE, int dataLength) {
		sendData[0] = Dispatch.PACKET_HEADER;
		sendData[1] = Dispatch.MTYPE_BDI;
		sendData[2] = STYPE;
		sendData[3] = (byte) (dataLength / 0x100); // length
		sendData[4] = (byte) (dataLength & 0xFF);
		sendData[Dispatch.PACKET_DATA_OFFSET + dataLength] = Dispatch.PACKET_END;
	}

	private static DataPacket transmit(int dataLength) throws USBException,
			DispatchException, BDIException {
		// write USB-command
		USBDevice.write(sendData, dataLength + DataPacket.PACKET_MIN_LENGTH);

		// read result
		DataPacket data = Dispatch.readBDI();
		if (data.subtype == STYPE_BDI_UNKNOWN_STYPE) {
			throw new BDIException("unknown subtype: " + data.subtype);
		} else if (data.subtype == STYPE_BDI_ERROR_TIMEOUT) {
			// receive invalid packet
			Dispatch.readBDI();
			throw new BDIException("timeout error (DSDO not low)");
		}
		return data;
	}

	private static void fillPacket(boolean modeBit, boolean controlBit,
			int data, int offset) {

		// startBit = 1 | modeBit | controlBit | 32 bits of data
		byte b = (byte) 0x80;
		if (modeBit) {
			b |= 0x40;
		}
		if (controlBit) {
			b |= 0x20;
		}
		b |= (byte) (data >>> 27);
		sendData[Dispatch.PACKET_DATA_OFFSET + offset] = b;
		sendData[Dispatch.PACKET_DATA_OFFSET + offset + 1] = (byte) ((data >>> 19) & 0xFF);
		sendData[Dispatch.PACKET_DATA_OFFSET + offset + 2] = (byte) ((data >>> 11) & 0xFF);
		sendData[Dispatch.PACKET_DATA_OFFSET + offset + 3] = (byte) ((data >>> 3) & 0xFF);
		sendData[Dispatch.PACKET_DATA_OFFSET + offset + 4] = (byte) ((data & 0x07) << 5);
	}

	private static DataPacket transfer(boolean modeBit, boolean controlBit,
			int data) throws USBException, DispatchException, BDIException {

		initPacket(STYPE_BDI_35IN, BDI_DATA35_LENGTH);

		fillPacket(modeBit, controlBit, data, 0);

		return transmit(BDI_DATA35_LENGTH);
	}

	private static int parseResult35(DataPacket data) throws BDIException {
		if (data.subtype != STYPE_BDI_35OUT) {
			throw new BDIException("wrong subtype: " + data.subtype);
		}
		// 1 bit is always 0 (Ready Bit)
		// 2 + 3 bit are status bits
		// 32 data bits
		if ((data.data[0] & 0x80) > 0) {
			throw new BDIException("ready bit is not 0: 0x"
					+ Integer.toHexString(data.data[0] & 0xFF));
		}
		switch ((data.data[0] >>> 5) & 0x03) {
		case 0:
			// first byte
			int retValue = (((data.data[0] << 3) & 0xFF) + ((data.data[1] & 0xFF) >>> 5)) << 24;
			// second byte
			retValue += (((data.data[1] << 3) & 0xFF) + ((data.data[2] & 0xFF) >>> 5)) << 16;
			// third byte
			retValue += (((data.data[2] << 3) & 0xFF) + ((data.data[3] & 0xFF) >>> 5)) << 8;
			// fourth byte
			retValue += ((data.data[3] << 3) & 0xFF)
					+ ((data.data[4] & 0xFF) >>> 5);
			return retValue;
		case 1:
			throw new BDIException("Sequencing Error: " + data.toString());
		case 2:
			throw new BDIException("CPU Exception: " + data.toString());
		case 3:
			return NULL_INDICATION;
		default:
			throw new BDIException("invalid case");
		}
	}

	private static int transferAndParse35(boolean modeBit,
			boolean controlBit, int data) throws USBException,
			DispatchException, BDIException {
		return parseResult35(transfer(modeBit, controlBit, data));
	}

	private static void epilogue() throws USBException, DispatchException,
			BDIException {
		// restore GPR30
		// put instr mfspr: GPR30, DPDR
		transferAndParse35(false, false, 0x7FF69AA6);
		// put GPR30 in DPDR
		transferAndParse35(false, true, gpr30);

		// restore GPR31
		// put instr. mfspr: GPR31, DPDR
		transferAndParse35(false, false, 0x7FF69AA6);
		// put GPR31 in DPDR
		transferAndParse35(false, true, gpr31);

		// return from interrupt - normal execution follows
		// put instr. rfi
		transferAndParse35(false, false, RFI);
	}

	private static void prologue() throws USBException, DispatchException,
			BDIException {
		final int EBRK_bit = 1;

		// save GPR30
		// put instr. mtspr DPDR, GPR30
		transferAndParse35(false, false, 0x7FD69BA6);

		// nop
		gpr30 = transferAndParse35(false, false, NOP);

		// read ECR for exception cause
		// put instr. mfspr GPR30, ECR
		transferAndParse35(false, false, 0x7FD422A6);
		// put instr. mtspr DPDR, GPR30
		transferAndParse35(false, false, 0x7FD69BA6);

		// save GPR31
		// put instr. mtspr DPDR, GPR31
		ecr = transferAndParse35(false, false, 0x7FF69BA6);
		// nop
		gpr31 = transferAndParse35(false, false, NOP);

		if ((ecr & (EBRK_bit * 2)) > 0) {
			// TODO: change exception string
			System.err.println("Exception Cause Register = " + "0x"
					+ Integer.toHexString(ecr));
		}
	}

	public static void break_() throws USBException, DispatchException,
			BDIException {
		// assert maskable breakpoint
		// TODO: check this
		if (transferAndParse35(true, true, 0x7E000000) == NULL_INDICATION) {
			prologue();
		}
		targetInDebugMode = isFreezeAsserted();
		// negate breakpoint
		transferAndParse35(true, true, 0x3E000000);
	}

	public static void go() throws USBException, DispatchException,
			BDIException {

		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		epilogue();
		targetInDebugMode = isFreezeAsserted();
	}

	private static void hard_reset() throws USBException, DispatchException,
			BDIException {
		initPacket(STYPE_BDI_HARD_RESET_555, 0);
		DataPacket data = transmit(0);
		if (data == null) {
			throw new BDIException("no data from device");
		}
		if (data.subtype != STYPE_BDI_HARD_RESET_SUCCESS) {
			throw new BDIException("wrong subtype: " + data.subtype);
		}
		fastDownloadStarted = false;
		targetInDebugMode = isFreezeAsserted();
	}

	public static void reset_target() throws USBException, DispatchException,
			BDIException {
		// hard reset
		hard_reset();
		// read ECR for exception cause
		// put instr. mfspr GPR30, ECR
		transferAndParse35(false, false, 0x7FD422A6);
		// put instr. mtspr DPDR, GPR30
		transferAndParse35(false, false, 0x7FD69BA6);
		// nop
		ecr = transferAndParse35(false, false, NOP);
		// check if entry into debug mode was because of DPI
		if (ecr != 0x01) {
			throw new BDIException(
					"Wrong debug enable cause (not due to DPI): Exception Cause Register = 0x"
							+ Integer.toHexString(ecr));
		}
	}

	public static boolean isFreezeAsserted() throws USBException,
			DispatchException, BDIException {
		initPacket(STYPE_BDI_GET_FREEZE, 0);
		DataPacket data = transmit(0);
		if (data == null) {
			throw new BDIException("no data from device");
		}
		if (data.subtype != STYPE_BDI_FREEZE_RESULT) {
			throw new BDIException("wrong subtype: " + data.subtype);
		}
		if ((data.data[0] != 0) && (data.data[0] != 1)) {
			throw new BDIException("wrong data: " + data.data[0]);
		}
		targetInDebugMode = data.data[0] == 1;
		return targetInDebugMode;
	}

	/**
	 * Called to start the fast download procedure.
	 * 
	 * @param startAddr
	 *            Address to which the data will be downloaded.
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public static void startFastDownload(int startAddr) throws USBException,
			DispatchException, BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}
		// put instr. mfspr GPR30, DPDR
		transferAndParse35(false, false, 0x7FD69AA6);
		// put adr into DPDR
		transferAndParse35(false, true, startAddr - 4);
		// start fast download
		transferAndParse35(true, true, 0xC6000000);
		fastDownloadStarted = true;
	}

	/**
	 * Fill one USB-Packet with data to download. The maximal number of words is
	 * defined by <code>MAX_NOF_WORDS_FAST_DOWNLOAD</code><br>
	 * <code>startFastDownload</code> has to be called before to set up the
	 * start address.
	 * 
	 * @param downloadData
	 *            Data to be downloaded (32 bit wide)
	 * @param dataLength
	 *            Length of the data to download (words)
	 * @throws BDIException
	 * @throws DispatchException
	 * @throws USBException
	 */
	public static void fastDownload(int[] downloadData, int dataLength)
			throws BDIException, USBException, DispatchException {

		if (!fastDownloadStarted) {
			throw new BDIException("start fast download first");
		}
		// check if data fits into USB-packet
		if (dataLength > MAX_NOF_WORDS_FAST_DOWNLOAD) {
			throw new BDIException(
					"data larger than MAX_NOF_WORDS_FAST_DOWNLOAD");
		}
		int currentIndex = 0;
		initPacket(STYPE_BDI_35FD_DATA, dataLength * BDI_DATA35_LENGTH);
		while (currentIndex < dataLength) {
			fillPacket(false, true, downloadData[currentIndex], currentIndex
					* BDI_DATA35_LENGTH);
			currentIndex++;
		}

		DataPacket data = transmit(dataLength * BDI_DATA35_LENGTH);
		if (data == null) {
			throw new BDIException("no data from device");
		}

		switch (data.subtype) {
		case STYPE_BDI_SUCCESS_FD:

			break;
		case STYPE_BDI_ERROR_FD_LENGTH:
			throw new BDIException("length smaller than BDI_DATA35_LENGTH");
		case STYPE_BDI_ERROR_FD_GENERAL:
			throw new BDIException("general fast download error");
		default:
			throw new BDIException("wrong subtype: " + data.subtype);
		}
	}

	public static void stopFastDownload() throws USBException,
			DispatchException, BDIException {
		fastDownloadStarted = false;

		// stop fast download
		int result = transferAndParse35(true, true, 0x86000000);
		System.out.println("stopFastDownload: result: 0x"
				+ Integer.toHexString(result));
		// if (result != 0x5F) {
		// // TODO: change exception string
		// throw new BDIException("result != 0x5F: " + result);
		// }
		// terminate gracefully (DATA transaction)
		result = transferAndParse35(false, true, 0x0);
		System.out.println("stopFastDownload: result 2: 0x"
				+ Integer.toHexString(result));
	}

	public static void writeMem(int addr, int value, int size)
			throws USBException, DispatchException, BDIException {

		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		// put instr. mfspr GPR30, DPDR
		transferAndParse35(false, false, 0x7FD69AA6);
		// put adr into DPDR
		transferAndParse35(false, true, addr);
		// put instr. mfspr GPR31, DPDR
		transferAndParse35(false, false, 0x7FF69AA6);
		// put val into DPDR
		transferAndParse35(false, true, value);
		switch (size) {
		case 1:
			// put instr. stbu 31, 0(30)
			transferAndParse35(false, false, 0x9FFE0000);
			break;
		case 2:
			// put instr. sthu 31, 0(30)
			transferAndParse35(false, false, 0x0B7FE0000);
			break;
		case 4:
			// put instr. stwu 31, 0(30)
			transferAndParse35(false, false, 0x97FE0000);
			break;
		default:
			throw new BDIException("wrong size: " + size
					+ " (should be 1, 2 or 4)");
		}
	}

	public static int readMem(int addr, int size) throws USBException,
			DispatchException, BDIException {

		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		// put instr. mfspr GPR30, DPDR
		transferAndParse35(false, false, 0x7FD69AA6);
		// put adr into DPDR
		transferAndParse35(false, true, addr);
		switch (size) {
		case 1:
			// put instr. lbzu 31, 0(30)
			transferAndParse35(false, false, 0x8FFE0000);
			break;
		case 2:
			// put instr. lhzu 31, 0(30)
			transferAndParse35(false, false, 0x0A7FE0000);
			break;
		case 4:
			// put instr. lwzu 31, 0(30)
			transferAndParse35(false, false, 0x87FE0000);
			break;
		default:
			throw new BDIException("wrong size: " + size
					+ " (should be 1, 2 or 4)");
		}
		// put instr. mtspr DPDR, GPR31
		transferAndParse35(false, false, 0x7FF69BA6);
		// put instr. nop
		return transferAndParse35(false, false, NOP);
	}

	public static void writeMemSeq(int value, int size) throws USBException,
			DispatchException, BDIException {

		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		// put instr. mfspr GPR30, DPDR
		transferAndParse35(false, false, 0x7FF69AA6);
		// put val into DPDR
		transferAndParse35(false, true, value);
		switch (size) {
		case 1:
			// put instr. stbu 31, 1(30)
			transferAndParse35(false, false, 0x9FFE0001);
			break;
		case 2:
			// put instr. sthu 31, 2(30)
			transferAndParse35(false, false, 0x0B7FE0002);
			break;
		case 4:
			// put instr. stwu 31, 4(30)
			transferAndParse35(false, false, 0x97FE0004);
			break;
		default:
			throw new BDIException("wrong size: " + size
					+ " (should be 1, 2 or 4)");
		}
	}

	public static int readMemSeq(int size) throws USBException,
			DispatchException, BDIException {

		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		switch (size) {
		case 1:
			// put instr. lbzu 31, 1(30)
			transferAndParse35(false, false, 0x8FFE0001);
			break;
		case 2:
			// put instr. lhzu 31, 2(30)
			transferAndParse35(false, false, 0x0A7FE0002);
			break;
		case 4:
			// put instr. lwzu 31, 4(30)
			transferAndParse35(false, false, 0x87FE0004);
			break;
		default:
			throw new BDIException("wrong size: " + size
					+ " (should be 1, 2 or 4)");
		}
		// put instr. mtspr DPDR, GPR31
		transferAndParse35(false, false, 0x7FF69BA6);
		// put instr. nop
		return transferAndParse35(false, false, NOP);
	}

	public static int readGPR(int gpr) throws USBException, DispatchException,
			BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		if (gpr == 30) {
			return gpr30;
		} else if (gpr == 31) {
			return gpr31;
		}
		int cmd = MRTDPDR + (gpr * 0x200000);

		// put instr. mtspr DPDR, GPRx
		transferAndParse35(false, false, cmd);
		// put instr. nop
		return transferAndParse35(false, false, NOP);
	}

	public static void writeGPR(int gpr, int value) throws USBException,
			DispatchException, BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		if (gpr == 30) {
			gpr30 = gpr;
			return;
		} else if (gpr == 31) {
			gpr31 = gpr;
			return;
		}
		int cmd = MDPDRTR + (gpr * 0x200000);

		// put instr. mfspr GPRx, DPDR
		transferAndParse35(false, false, cmd);
		// put data
		transferAndParse35(false, true, value);
	}

	public static int readSPR(int spr) throws USBException, DispatchException,
			BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		int cmd = ((spr & 0xFF) * 0x20 + (spr >>> 6)) * 0x800;
		if ((spr == 268) || (spr == 269)) {
			cmd += MTBRTR30;
		} else {
			cmd += MSPRTR30;
		}

		// put instr. mfspr GPR30, SPRxxx
		transferAndParse35(false, false, cmd);
		// put instr. mtspr DPDR, GPR30
		transferAndParse35(false, false, 0x7FD69BA6);
		// put instr. nop
		return transferAndParse35(false, false, NOP);
	}

	public static void writeSPR(int spr, int value) throws USBException,
			DispatchException, BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}
		int cmd = MR30TSPR + ((spr & 0xFF) * 0x20 + (spr >>> 6)) * 0x800;

		// put instr. mfspr GPR30, DPDR
		transferAndParse35(false, false, 0x7FD69AA6);
		// put data
		transferAndParse35(false, true, value);
		// put instr. mtspr SPRxxx, GPR30
		transferAndParse35(false, false, cmd);
	}

	public static int readMSR() throws USBException, DispatchException,
			BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		// put instr. mfmsr GPR30, MSR
		transferAndParse35(false, false, 0x7FC000A6);
		// put instr. mtspr DPDR, GPR30
		transferAndParse35(false, false, 0x7FD69BA6);
		// put instr. nop
		return transferAndParse35(false, false, NOP);
	}

	public static void writeMSR(int value) throws USBException,
			DispatchException, BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}
		// put instr. mfspr GPR30, DPDR
		transferAndParse35(false, false, 0x7FD69AA6);
		// put data
		transferAndParse35(false, true, value);
		// put instr. mtmsr MSR, GPR30
		transferAndParse35(false, false, 0x7FC00124);
	}

	public static long readFPR(int fpr, int tmpMemAddr) throws USBException,
			DispatchException, BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}
		// set FP bit
		writeMSR(0x2002);

		// set r30 to tmpMemAddr
		// put instr. mfspr r30, DPDR
		transferAndParse35(false, false, MDPDRTR + (30 * 0x200000));
		// put tmpMemAddr
		transferAndParse35(false, false, tmpMemAddr);

		// put instr. stfd frS, 0(r30)
		transferAndParse35(false, false, 0x0D81E0000 + fpr * 0x200000);
		// read data from tmpMemAddr
		return ((long) readMem(tmpMemAddr, 4) << 32)
				+ readMem(tmpMemAddr + 4, 4);
	}

	public static void writeFPR(int fpr, int tmpMemAddr, long value)
			throws USBException, DispatchException, BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}
		// set FP bit
		writeMSR(0x2002);

		// write data to tmpMemAddr
		writeMem(tmpMemAddr, (int) (value >>> 32), 4);
		writeMem(tmpMemAddr, (int) value, 4);

		// set r30 to tmpMemAddr
		// put instr. mfspr r30, DPDR
		transferAndParse35(false, false, MDPDRTR + (30 * 0x200000));
		// put tmpMemAddr
		transferAndParse35(false, true, tmpMemAddr);

		// put instr. lfd frS, 0(r30)
		transferAndParse35(false, false, 0x0C81E0000 + fpr * 0x200000);
		// nop
		transferAndParse35(false, false, NOP);
	}

	public static int readCR() throws USBException, DispatchException,
			BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		// put instr. mfcr GPR30
		transferAndParse35(false, false, 0x7FC00026);
		// put instr. mtspr DPDR, GPR30
		transferAndParse35(false, false, 0x7FD69BA6);
		// nop
		return transferAndParse35(false, false, NOP);
	}

	public static void writeCR(int value) throws USBException,
			DispatchException, BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}
		// put instr. mfspr GPR30, DPDR
		transferAndParse35(false, false, 0x7FD69AA6);
		// put data
		transferAndParse35(false, true, value);

		// put instr. mtcrf CRM=all, GPR30
		transferAndParse35(false, false, 0x7FCFF120);
	}

	public static int readFPSCR() throws USBException, DispatchException,
			BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}
		// save CR
		int cr = readCR();
		// set FP bit
		writeMSR(0x2002);
		// put instr. mcrfs crf0, crf0
		transferAndParse35(false, false, 0xFC000080);
		// put instr. mcrfs crf1, crf1
		transferAndParse35(false, false, 0xFC840080);
		// put instr. mcrfs crf2, crf2
		transferAndParse35(false, false, 0xFD080080);
		// put instr. mcrfs crf3, crf3
		transferAndParse35(false, false, 0xFD8C0080);
		// put instr. mcrfs crf4, crf4
		transferAndParse35(false, false, 0xFE100080);
		// put instr. mcrfs crf5, crf5
		transferAndParse35(false, false, 0xFE940080);
		// put instr. mcrfs crf6, crf6
		transferAndParse35(false, false, 0xFF180080);
		// put instr. mcrfs crf7, crf7
		transferAndParse35(false, false, 0xFF9C0080);
		int retVal = readCR();
		writeCR(cr);
		return retVal;
	}

	public static void writeFPSCR(int value) throws USBException,
			DispatchException, BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}
		// set FP bit
		writeMSR(0x2002);
		for (int i = 0; i < 7; i++) {
			int cmd = 0xFC00010C + (7 - i) * 0x800000
					+ ((value >>> i * 4) & 0xF) * 0x1000;
			// put instr. mtfsfi crfx, imm
			transferAndParse35(false, false, cmd);
		}
	}

	public static boolean isTargetInDebugMode() {
		return targetInDebugMode;
	}

	public static int getGpr30() {
		return gpr30;
	}

	public static void setGpr30(int gpr30) {
		MPC555.gpr30 = gpr30;
	}

	public static int getGpr31() {
		return gpr31;
	}

	public static void setGpr31(int gpr31) {
		MPC555.gpr31 = gpr31;
	}
}
