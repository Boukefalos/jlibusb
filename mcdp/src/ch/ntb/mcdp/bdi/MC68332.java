package ch.ntb.mcdp.bdi;

import ch.ntb.mcdp.usb.DataPacket;
import ch.ntb.mcdp.usb.Dispatch;
import ch.ntb.mcdp.usb.DispatchException;
import ch.ntb.mcdp.usb.USBDevice;
import ch.ntb.mcdp.utils.logger.LogUtil;
import ch.ntb.mcdp.utils.logger.McdpLogger;
import ch.ntb.usb.Device;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;

/**
 * Represents the Background Debugging Interface of the MC68332 microcontroller.
 * 
 * @author schlaepfer
 * 
 */
public class MC68332 {

	private static McdpLogger logger = LogUtil.ch_ntb_mcdp_bdi;

	// BDI subtypes
	/**
	 * 17 Bit Packet to BDI
	 */
	private static final byte STYPE_BDI_17IN = 0x10;

	/**
	 * 17 Bit Packet from BDI
	 */
	private static final byte STYPE_BDI_17OUT = 0x11;

	/**
	 * Fast Download Data to download to target (Fill)
	 */
	private static final byte STYPE_BDI_17FILL_BYTE_WORD = 0x12;

	/**
	 * Fast Download Data to download to target (Fill)
	 */
	private static final byte STYPE_BDI_17FILL_LONG = 0x13;

	/**
	 * Dump target memory (Dump)
	 */
	private static final byte STYPE_BDI_17DUMP = 0x14;

	/**
	 * Hard Reset
	 */
	private static final byte STYPE_BDI_HARD_RESET_332 = 0x07;

	/**
	 * Get status of Freeze-Signal
	 */
	private static final byte STYPE_BDI_GET_FREEZE = 0x08;

	/**
	 * Fast Download successfully finished (Fill)
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
	 * Fast Download Data received from target (Dump)
	 */
	private static final byte STYPE_BDI_DUMP_DATA = 0x63;

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
	 * Dump error
	 */
	private static final byte STYPE_BDI_DUMP_ERROR = 0x74;

	/**
	 * data length in bytes
	 */
	private static final int BDI_DATA17_LENGTH = 3;

	/**
	 * no operation
	 */
	private static final int NOP = 0x0000;

	/**
	 * asserts RESET for 512 clock cycles (CPU is not affected)
	 */
	private static final int RST = 0x0400;

	/**
	 * resume execution
	 */
	private static final int GO = 0x0C00;

	/**
	 * read data register
	 */
	private static final int RDREG = 0x2180;

	/**
	 * write data register
	 */
	private static final int WDREG = 0x2080;

	/**
	 * read system register
	 */
	private static final int RSREG = 0x2580;

	/**
	 * write system register
	 */
	private static final int WSREG = 0x2480;

	/**
	 * write memory byte
	 */
	private static final int WRITEB = 0x1800;

	/**
	 * write memory word
	 */
	private static final int WRITEW = 0x1840;

	/**
	 * write memory long
	 */
	private static final int WRITEL = 0x1880;

	/**
	 * read memory byte
	 */
	private static final int READB = 0x1900;

	/**
	 * read memory word
	 */
	private static final int READW = 0x1940;

	/**
	 * read memory long
	 */
	private static final int READL = 0x1980;

	/**
	 * fill memory byte
	 */
	private static final int FILLB = 0x1C00;

	/**
	 * fill memory word
	 */
	private static final int FILLW = 0x1C40;

	/**
	 * fill memory long
	 */
	private static final int FILLL = 0x1C80;

	/**
	 * dump memory long
	 */
	private static final int DUMPB = 0x1D00;

	/**
	 * dump memory long
	 */
	private static final int DUMPW = 0x1D40;

	/**
	 * dump memory long
	 */
	private static final int DUMPL = 0x1D80;

	/**
	 * BDI return Code: Command Complete; Status OK
	 */
	private static final int STATUS_OK = 0xFFFF;

	/**
	 * Maximal number of words or bytes (1 or 2 bytes) for one usb-packet to
	 * download (fill).
	 */
	private int maxNofBytesWordsFill;

	/**
	 * Maximal number of longs (4 bytes) for one usb-packet to download (fill).<br>
	 */
	private int maxNofLongsFill;

	/**
	 * Maximal number of words or bytes (1 or 2 bytes) to dump in one
	 * usb-packet.
	 */
	private int maxNofBytesWordsDump;

	/**
	 * Maximal number of longs (4 bytes) to dump in one usb-packet.
	 */
	private int maxNofLongsDump;

	/**
	 * The last known state on the freeze signal.
	 */
	private boolean targetInDebugMode = false;

	/**
	 * Temporary buffer of the data to send.
	 */
	private byte[] sendData;

	/**
	 * Sizes which are setup up when writing or reading memory data. This values
	 * are used in <code>fillMem</code> and <code>dumpMem</code>.
	 */
	private int readMemSize, writeMemSize;

	/**
	 * The USB device to connect to.
	 */
	private Device device;

	/**
	 * @param device
	 *            The USB device to connect to. Before using any methods which
	 *            communicate with the device, the device must be connected.
	 */
	public MC68332(Device device) {
		readMemSize = 0;
		writeMemSize = 0;
		sendData = new byte[USB.HIGHSPEED_MAX_BULK_PACKET_SIZE];
		this.device = device;
	}

	/**
	 * Transmit a packet to the USB-target. The length transmitted over USB is
	 * <code>dataLength + DataPacket.PACKET_MIN_LENGTH</code>.
	 * 
	 * @param STYPE
	 *            subtype of the packet <code>STYPE_BDI_*</code>
	 * @param dataLength
	 *            length of the data to be sent
	 * @return resulting response from target
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	private DataPacket transmit(byte STYPE, int dataLength)
			throws USBException, DispatchException, BDIException {
		// initialize packet
		sendData[0] = DataPacket.PACKET_HEADER;
		sendData[1] = Dispatch.MTYPE_BDI;
		sendData[2] = STYPE;
		sendData[3] = (byte) (dataLength / 0x100); // length
		sendData[4] = (byte) (dataLength & 0xFF);
		sendData[DataPacket.PACKET_DATA_OFFSET + dataLength] = DataPacket.PACKET_END;

		// write USB-command
		USBDevice
				.write_BDI(sendData, dataLength + DataPacket.PACKET_MIN_LENGTH);

		// read result
		DataPacket data = Dispatch.readBDI();
		if (data.subtype == STYPE_BDI_UNKNOWN_STYPE) {
			throw new BDIException("unknown subtype: " + data.subtype);
		}
		return data;
	}

	/**
	 * Inserts 17-bits of data (3 bytes) into the send bufferList.
	 * 
	 * @param data
	 *            17 bits (3 bytes) of data
	 * @param offset
	 *            the offset from the beginning of the data
	 */
	private void fillPacket(int data, int offset) {

		// refer to CPU32 Reference Manual, Section 7.2.7
		// bit16 = 0 + 16 bits of data (MSB .. LSB)
		sendData[DataPacket.PACKET_DATA_OFFSET + offset] = (byte) ((data >>> 9) & 0x7F);
		sendData[DataPacket.PACKET_DATA_OFFSET + offset + 1] = (byte) ((data >>> 1) & 0xFF);
		sendData[DataPacket.PACKET_DATA_OFFSET + offset + 2] = (byte) ((data & 0x01) << 7);
	}

	/**
	 * Send one BDI instruction (17 bits) to the target without any other data.
	 * 
	 * @param data
	 *            BDI instruction
	 * @return resulting response from target
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public DataPacket transfer(int data) throws USBException,
			DispatchException, BDIException {

		fillPacket(data, 0);

		return transmit(STYPE_BDI_17IN, BDI_DATA17_LENGTH);
	}

	/**
	 * Checks the result for error conditions and converts the byte data to an
	 * integer.
	 * 
	 * @param data
	 *            <code>DataPacket</code> to be parsed
	 * @return data received from the target as an integer
	 * @throws BDIException
	 * @throws USBException
	 * @throws DispatchException
	 */
	private int parseResult17(DataPacket data, int offset) throws BDIException,
			USBException, DispatchException {
		if ((data.subtype != STYPE_BDI_17OUT)
				&& (data.subtype != STYPE_BDI_DUMP_DATA)) {
			throw new BDIException("wrong subtype: " + data.subtype);
		}
		boolean statusControlBit = (data.data[0 + offset] & 0x80) > 0;
		int retValue = (((data.data[0 + offset] << 1) & 0xFF) + ((data.data[1 + offset] & 0x80) >>> 7)) << 8;
		retValue += ((data.data[1 + offset] << 1) & 0xFF)
				+ ((data.data[2 + offset] & 0x80) >>> 7);

		if (statusControlBit) {
			switch (retValue) {
			case 0x0000:
				// Not Ready
				return 0;
			case 0x0001:
				// Data Invalid
				throw new BDIException(
						"BERR Terminated Bus Cycle; Data Invalid");
			case 0xFFFF:
				// Illegal Command
				throw new BDIException("Illegal Command");
			default:
				// invalid case
				throw new BDIException("invalid case: 0x"
						+ Integer.toHexString(retValue));
			}
		}
		// retValue = 0xxxxx -> Valid Data Transfer
		// retValue = 0xFFFF -> Command Complete; Status OK
		return retValue;
	}

	/**
	 * Combines the <code>transfer(int data)</code> and
	 * <code>parseResult17(DataPacket data)</code> methods. <br>
	 * Use this for a normal BDI transfer.
	 * 
	 * @param data
	 * @return
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	private int transferAndParse17(int data) throws USBException,
			DispatchException, BDIException {
		return parseResult17(transfer(data), 0);
	}

	/**
	 * Sends NOPs to the target until a <code>STATUS_OK</code> result is
	 * received.
	 * 
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public void nopsToLegalCmd() throws USBException, DispatchException,
			BDIException {
		final int NOF_NOPS_TO_LEGAL_CMD = 4;
		for (int i = 0; i < NOF_NOPS_TO_LEGAL_CMD; i++) {
			if (transferAndParse17(NOP) == STATUS_OK) {
				return;
			}
		}
		throw new BDIException("timeout, tried " + NOF_NOPS_TO_LEGAL_CMD
				+ " times");
	}

	/**
	 * Signals a breakpoint and enters debug mode.
	 * 
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public void break_() throws USBException, DispatchException, BDIException {
		if (transferAndParse17(NOP) != STATUS_OK) {
			throw new BDIException("no STATUS_OK received");
		}
		targetInDebugMode = isFreezeAsserted();
	}

	/**
	 * Resume from debug mode.
	 * 
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public void go() throws USBException, DispatchException, BDIException {
		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}
		transferAndParse17(GO);
		targetInDebugMode = isFreezeAsserted();
	}

	/**
	 * Send a command to reset the microcontroller.<br>
	 * The reset is done electrically by the USB-controller.
	 * 
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	private void hard_reset() throws USBException, DispatchException,
			BDIException {
		DataPacket data = transmit(STYPE_BDI_HARD_RESET_332, 0);
		if (data == null) {
			throw new BDIException("no data from device");
		}
		if (data.subtype != STYPE_BDI_HARD_RESET_SUCCESS) {
			throw new BDIException("wrong subtype: " + data.subtype);
		}
	}

	/**
	 * Reset the target and put it into debug mode.
	 * 
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public void reset_target() throws USBException, DispatchException,
			BDIException {
		// hard reset
		hard_reset();
		// break
		break_();
	}

	/**
	 * Send the <b>RST</b> command (reset peripherals) to the microcontroller.
	 * 
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public void reset_peripherals() throws USBException, DispatchException,
			BDIException {
		// hard reset
		transferAndParse17(RST);
		// wait for 50ms
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// break
		break_();
	}

	/**
	 * Check if the freeze signal is asserted.<br>
	 * The freeze siganl is asserted if the target is in debug mode.
	 * 
	 * @return
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public boolean isFreezeAsserted() throws USBException, DispatchException,
			BDIException {
		DataPacket data = transmit(STYPE_BDI_GET_FREEZE, 0);
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
	 * Update the internal maximal values for the <code>fill</code> and
	 * <code>dump</code> commands.
	 */
	private void updateMaxValues() {
		// update the values (now the device should be connected)
		if ((maxNofLongsFill <= 0) | (maxNofBytesWordsFill <= 0)
				| (maxNofBytesWordsDump <= 0) | (maxNofLongsDump <= 0)) {
			maxNofBytesWordsDump = (device.getMaxPacketSize()
					- DataPacket.PACKET_MIN_LENGTH - 3/* NOP */) / 3;
			maxNofBytesWordsFill = (device.getMaxPacketSize()
					- DataPacket.PACKET_MIN_LENGTH - 3) / 6;
			maxNofLongsDump = maxNofBytesWordsFill;
			maxNofLongsFill = (device.getMaxPacketSize()
					- DataPacket.PACKET_MIN_LENGTH - 3) / 9;
			logger.finer("update maxNofLongs: " + maxNofLongsFill
					+ ", maxNofBytesWords: " + maxNofBytesWordsFill);
		}
	}

	/**
	 * Fill large blocks of memory.<br>
	 * Fill is used in conjunction with the <code>writeMem</code> command. The
	 * maximal number of words is defined by
	 * <code>MAX_NOF_WORDS_FAST_DOWNLOAD</code> for 1 and 2 byte (word) data.
	 * For 4 byte (long) data, only half the size of
	 * <code>MAX_NOF_WORDS_FAST_DOWNLOAD</code> is available as 4 bytes of
	 * data has to be split in two packets (2 x 3 bytes).<br>
	 * Befor using <code>fillMem</code>, <code>writeMem</code> has to be
	 * called to set up the start address and size.
	 * 
	 * @param downloadData
	 *            Data to be downloaded (size depending on size set up with
	 *            <code>writeMem</code>)
	 * @param dataLength
	 *            Number of bytes, words or longs (1, 2, 4 bytes)
	 * @throws BDIException
	 * @throws DispatchException
	 * @throws USBException
	 */
	public void fillMem(int[] downloadData, int dataLength)
			throws BDIException, USBException, DispatchException {
		// check if data fits into USB-packet
		int currentIndex = 0;
		DataPacket data;
		logger.finer("dataLength: " + dataLength);
		updateMaxValues();
		switch (writeMemSize) {
		case 1:
			if (dataLength > maxNofBytesWordsFill) {
				throw new BDIException("data length (" + dataLength
						+ ") larger than maxNofBytesWords ("
						+ maxNofBytesWordsFill + ")");
			}
			while (currentIndex < dataLength) {
				// FILLB
				fillPacket(FILLB, currentIndex * 6);
				// DATA
				fillPacket(downloadData[currentIndex] & 0xFF,
						currentIndex * 6 + 3);
				currentIndex++;
			}
			// send NOP to get result of last write
			fillPacket(NOP, currentIndex * 6);
			data = transmit(STYPE_BDI_17FILL_BYTE_WORD, dataLength * 6 + 3);
			break;
		case 2:
			if (dataLength > maxNofBytesWordsFill) {
				throw new BDIException("data length (" + dataLength
						+ ") larger than maxNofBytesWords ("
						+ maxNofBytesWordsFill + ")");
			}
			while (currentIndex < dataLength) {
				// FILLW
				fillPacket(FILLW, currentIndex * 6);
				// DATA
				fillPacket(downloadData[currentIndex], currentIndex * 6 + 3);
				currentIndex++;
			}
			// send NOP to get result of last write
			fillPacket(NOP, currentIndex * 6);
			data = transmit(STYPE_BDI_17FILL_BYTE_WORD, dataLength * 6 + 3);
			break;
		case 4:
			if (dataLength > (maxNofLongsFill)) {
				throw new BDIException("data length (" + dataLength
						+ ") larger than maxNofLongs (" + maxNofLongsFill + ")");
			}
			while (currentIndex < dataLength) {
				// FILLL
				fillPacket(FILLL, currentIndex * 9);
				// MS data
				fillPacket((downloadData[currentIndex] >>> 16),
						currentIndex * 9 + 3);
				// LS data
				fillPacket(downloadData[currentIndex], currentIndex * 9 + 6);
				currentIndex++;
			}
			// send NOP to get result of last write
			fillPacket(NOP, currentIndex * 9);
			data = transmit(STYPE_BDI_17FILL_LONG, dataLength * 9 + 3);
			logger.fine("FILL: Transmit: " + (dataLength * 9 + 3));
			break;
		default:
			throw new BDIException("invalid writeMemSize: " + writeMemSize);
		}

		if (data == null) {
			throw new BDIException("no data from device");
		}

		switch (data.subtype) {
		case STYPE_BDI_SUCCESS_FD:
			break;
		case STYPE_BDI_ERROR_FD_LENGTH:
			System.out.println("0x"
					+ Integer.toHexString(((data.data[0] & 0xff) << 8)
							+ (data.data[1] & 0xff)));
			throw new BDIException("length smaller than BDI_DATA17_LENGTH");
		case STYPE_BDI_ERROR_FD_GENERAL:
			System.out.println("0x"
					+ Integer.toHexString(((data.data[0] & 0xff) << 8)
							+ (data.data[1] & 0xff)));
			throw new BDIException("general fast download error");
		default:
			throw new BDIException("wrong subtype: " + data.subtype);
		}
	}

	/**
	 * Dump large blocks of memory. <br>
	 * Dump is used in conjunction with the <code>readMem(...)</code> command.
	 * The size depends on the size set up with <code>readMem(...)</code> and
	 * is internally stored.
	 * 
	 * @param nofData
	 *            number of bytes/words/longs to read (depends on the size set
	 *            up with <code>readMem(...)</code>)
	 * @return read values
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public int[] dumpMem(int nofData) throws USBException, DispatchException,
			BDIException {

		updateMaxValues();
		int dataSize;
		switch (readMemSize) {
		case 1:
			if (nofData > maxNofBytesWordsFill)
				nofData = maxNofBytesWordsFill;
			// fill the packet with {DUMPB} + 1 NOP at the end
			int i;
			for (i = 0; i < nofData; i++) {
				fillPacket(DUMPB, i * 3);
			}
			fillPacket(NOP, i * 3);
			dataSize = i * 3 + 3;
			break;
		case 2:
			if (nofData > maxNofBytesWordsFill)
				nofData = maxNofBytesWordsFill;
			// fill the packet with {DUMPW} + 1 NOP at the end
			for (i = 0; i < nofData; i++) {
				fillPacket(DUMPW, i * 3);
			}
			fillPacket(NOP, i * 3);
			dataSize = i * 3 + 3;
			break;
		case 4:
			if (nofData > maxNofLongsFill)
				nofData = maxNofLongsFill;
			// fill the packet with {DUMPL + NOP} + 1 NOP at the end
			for (i = 0; i < nofData; i++) {
				fillPacket(DUMPL, i * 6);
				fillPacket(NOP, i * 6 + 3);
			}
			fillPacket(NOP, i * 6);
			dataSize = i * 6 + 3;
			break;
		default:
			throw new BDIException("invalid readMemSize: " + readMemSize);
		}

		DataPacket res = transmit(STYPE_BDI_17DUMP, dataSize);
		/*
		 * The status of each transfer is checked on the USB-Controller. If an
		 * error occurs, a STYPE_BDI_DUMP_ERROR packet is returned.
		 */
		if (res == null) {
			throw new BDIException("no data from device");
		}

		int[] result;
		switch (res.subtype) {
		case STYPE_BDI_DUMP_DATA:
			switch (readMemSize) {
			case 1:
			case 2:
				result = new int[(res.data.length) / 3];
				// MS Result before LS Result
				int resIndex = 0;
				while (resIndex * 3 + 3 < res.data.length) {
					result[resIndex] = parseResult17(res, resIndex * 3);
					resIndex++;
				}
				return result;
			case 4:
				result = new int[(res.data.length) / 6];
				// MS Result before LS Result
				resIndex = 0;
				while (resIndex * 6 + 6 < res.data.length) {
					// MS Result
					result[resIndex] = parseResult17(res, resIndex * 6) << 16;
					// LS Result
					result[resIndex] += parseResult17(res, resIndex * 6 + 3);
					resIndex++;
				}
				return result;
			// the default case is handled above
			}
		case STYPE_BDI_DUMP_ERROR:
			// throws BDI exceptions, but not for "Not Ready"
			throw new BDIException("STYPE_BDI_DUMP_ERROR");
		default:
			throw new BDIException("wrong subtype: " + res.subtype);
		}
	}

	/**
	 * Write to a specified memory address.<br>
	 * 
	 * @param addr
	 *            address to write
	 * @param value
	 *            value to write
	 * @param size
	 *            number of bytes to read
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public void writeMem(int addr, int value, int size) throws USBException,
			DispatchException, BDIException {

		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}
		logger.info("addr: 0x" + Integer.toHexString(addr >>> 16) + " 0x"
				+ Integer.toHexString(addr & 0xFFFF) + "\tvalue: 0x"
				+ Integer.toHexString(value >>> 16) + " 0x"
				+ Integer.toHexString(value & 0xFFFF));

		writeMemSize = size;
		switch (size) {
		case 1:
			// put instr.
			transferAndParse17(WRITEB);
			// put MS ADDR
			transferAndParse17(addr >>> 16);
			// put LS ADDR
			transferAndParse17(addr & 0xFFFF);
			// put data (byte)
			transferAndParse17(value & 0xFFFF);
			break;
		case 2:
			// put instr.
			transferAndParse17(WRITEW);
			// put MS ADDR
			transferAndParse17(addr >>> 16);
			// put LS ADDR
			transferAndParse17(addr & 0xFFFF);
			// put data (word)
			transferAndParse17(value & 0xFFFF);
			break;
		case 4:
			// put instr.
			transferAndParse17(WRITEL);
			// put MS ADDR
			transferAndParse17(addr >>> 16);
			// put LS ADDR
			transferAndParse17(addr & 0xFFFF);
			// put MS data (long)
			transferAndParse17(value >>> 16);
			// put LS data (long)
			transferAndParse17(value & 0xFFFF);
			break;
		default:
			writeMemSize = 0;
			throw new BDIException("wrong size: " + size
					+ " (should be 1, 2 or 4)");
		}
		// check status
		if (transferAndParse17(NOP) != STATUS_OK) {
			// throw new BDIException("error on writeMem");
		}
	}

	/**
	 * Read the value of a specified memory address.<br>
	 * 
	 * @param addr
	 *            address to read
	 * @param size
	 *            number of bytes to read
	 * @return value of this memory address
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public int readMem(int addr, int size) throws USBException,
			DispatchException, BDIException {

		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		logger.info("addr: 0x" + Integer.toHexString(addr >>> 16) + " 0x"
				+ Integer.toHexString(addr & 0xFFFF));

		readMemSize = size;
		switch (size) {
		case 1:
			// put instr.
			transferAndParse17(READB);
			// put MS ADDR
			transferAndParse17(addr >>> 16);
			// put LS ADDR
			transferAndParse17(addr & 0xFFFF);
			// get data (byte)
			return transferAndParse17(NOP);
		case 2:
			// put instr.
			transferAndParse17(READW);
			// put MS ADDR
			transferAndParse17(addr >>> 16);
			// put LS ADDR
			transferAndParse17(addr & 0xFFFF);
			// get data (word)
			return transferAndParse17(NOP);
		case 4:
			// put instr.
			transferAndParse17(READL);
			// put MS ADDR
			transferAndParse17(addr >>> 16);
			// put LS ADDR
			transferAndParse17(addr & 0xFFFF);
			// get MS data (long)
			int valMS = transferAndParse17(NOP);
			// get LS data (long)
			return (valMS << 16) + transferAndParse17(NOP);
		default:
			readMemSize = 0;
			throw new BDIException("wrong size: " + size
					+ " (should be 1, 2 or 4 bytes)");
		}
	}

	/**
	 * Read a specified value from a user register. <br>
	 * See the <b>registerDictionary.xml</b> file for valid registers. This
	 * file can be found in the <b>mc68332 resource</b>-section.
	 * 
	 * @param reg
	 *            register to read
	 * @return value of register
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public int readUserReg(int reg) throws USBException, DispatchException,
			BDIException {

		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		// put instr.
		transferAndParse17(RDREG + (reg & 0x0F));
		// get MS data (long)
		int valMS = transferAndParse17(NOP);
		// get LS data (long)
		return (valMS << 16) + transferAndParse17(NOP);
	}

	/**
	 * Write a specified value to user register. <br>
	 * See the <b>registerDictionary.xml</b> file for valid registers. This
	 * file can be found in the <b>mc68332 resource</b>-section.
	 * 
	 * @param reg
	 *            register to write
	 * @param value
	 *            value to write to register
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public void writeUserReg(int reg, int value) throws USBException,
			DispatchException, BDIException {

		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		// put instr.
		transferAndParse17(WDREG + (reg & 0x0F));
		// put MS data (long)
		transferAndParse17(value >>> 16);
		// put LS data (long)
		transferAndParse17(value);
		// check status
		if (transferAndParse17(NOP) != STATUS_OK) {
			throw new BDIException("error on writeUserReg");
		}
	}

	/**
	 * Read a specified value from a system register. <br>
	 * See the <b>registerDictionary.xml</b> file for valid registers. This
	 * file can be found in the <b>mc68332 resource</b>-section.
	 * 
	 * @param reg
	 *            register to read
	 * @return value of register
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public int readSysReg(int reg) throws USBException, DispatchException,
			BDIException {

		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}

		// put instr.
		transferAndParse17(RSREG + (reg & 0x0F));
		// get MS data (long)
		int valMS = transferAndParse17(NOP);
		// get LS data (long)
		return (valMS << 16) + transferAndParse17(NOP);
	}

	/**
	 * Write a specified value to system register. <br>
	 * See the <b>registerDictionary.xml</b> file for valid registers. This
	 * file can be found in the <b>mc68332 resource</b>-section.
	 * 
	 * @param reg
	 *            register to write
	 * @param value
	 *            value to write to register
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public void writeSysReg(int reg, int value) throws USBException,
			DispatchException, BDIException {

		if (!targetInDebugMode) {
			throw new BDIException("target not in debug mode");
		}
		logger.info("register: 0x" + Integer.toHexString(reg) + ", value: "
				+ "0x" + Integer.toHexString(value) + "\tinstructions: 0x"
				+ Integer.toHexString(WSREG + (reg & 0xF)) + " 0x"
				+ Integer.toHexString(value >>> 16) + ", 0x"
				+ Integer.toHexString(value));

		// put instr.
		transferAndParse17(WSREG + (reg & 0x0F));
		// put MS data (long)
		transferAndParse17(value >>> 16);
		// put LS data (long)
		transferAndParse17(value);
		// check status
		if (transferAndParse17(NOP) != STATUS_OK) {
			throw new BDIException("error on writeUserReg");
		}
	}

	/**
	 * Return the last known state of the freeze signal.<br>
	 * This value may not be up to date as the target state may have changed
	 * meanwhile. To get the up to date value use <code>isFreezeAsserted</code>
	 * which will issue an USB request, read the freeze signal and update the
	 * internal value returned by this method.
	 * 
	 * @return the last known state of the freeze signal
	 */
	public boolean isTargetInDebugMode() {
		return targetInDebugMode;
	}

	/**
	 * Maximal number of words or bytes (1 or 2 bytes) for one usb-packet to
	 * download (fill).<br>
	 * This method will only return a valid result, if the USB device is
	 * connected.
	 * 
	 * @return
	 */
	public int getMaxNofBytesWordsFill() {
		updateMaxValues();
		return maxNofBytesWordsFill;
	}

	/**
	 * Maximal number of longs (4 bytes) for one usb-packet to download (fill)
	 * or read (dump).<br>
	 * This method will only return a valid result, if the USB device is
	 * connected.
	 * 
	 * @return
	 */
	public int getMaxNofLongsFill() {
		updateMaxValues();
		return maxNofLongsFill;
	}

	/**
	 * Maximal number of words or bytes (1 or 2 bytes) to dump in one
	 * usb-packet.<br>
	 * This method will only return a valid result, if the USB device is
	 * connected.
	 * 
	 * @return
	 */
	public int getMaxNofBytesWordsDump() {
		updateMaxValues();
		return maxNofBytesWordsDump;
	}

	/**
	 * Maximal number of longs (4 bytes) for one usb-packet to download (fill)
	 * or read (dump).<br>
	 * This method will only return a valid result, if the USB device is
	 * connected.
	 * 
	 * @return
	 */
	public int getMaxNofLongsDump() {
		updateMaxValues();
		return maxNofLongsDump;
	}
}
