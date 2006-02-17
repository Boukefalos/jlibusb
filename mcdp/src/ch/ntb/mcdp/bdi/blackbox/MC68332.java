package ch.ntb.mcdp.bdi.blackbox;

import ch.ntb.mcdp.bdi.BDIException;
import ch.ntb.mcdp.usb.DispatchException;
import ch.ntb.mcdp.usb.USBDevice;
import ch.ntb.usb.USBException;

public class MC68332 {

	private static ch.ntb.mcdp.bdi.MC68332 bdi;

	/**
	 * Create a new BDI instance. This procedure has to be called AFTER opening
	 * the USB device and BEFORE using any BDI commands.
	 */
	public static void initialise() {
		bdi = new ch.ntb.mcdp.bdi.MC68332(USBDevice.getDevice());
	}

	/**
	 * Sends NOPs to the target until a <code>STATUS_OK</code> result is
	 * received.
	 * 
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public static void nopsToLegalCmd() throws USBException, DispatchException,
			BDIException {
		bdi.nopsToLegalCmd();
	}

	/**
	 * Signals a breakpoint and enters debug mode.
	 * 
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public static void break_() throws USBException, DispatchException,
			BDIException {
		bdi.break_();
	}

	/**
	 * Resume from debug mode.
	 * 
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public static void go() throws USBException, DispatchException,
			BDIException {
		bdi.go();
	}

	/**
	 * Reset the target and put it into debug mode.
	 * 
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public static void reset_target() throws USBException, DispatchException,
			BDIException {
		bdi.reset_target();
	}

	/**
	 * Send the <b>RST</b> command (reset peripherals) to the microcontroller.
	 * 
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public static void reset_peripherals() throws USBException,
			DispatchException, BDIException {
		bdi.reset_peripherals();
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
	public static boolean isFreezeAsserted() throws USBException,
			DispatchException, BDIException {
		return bdi.isFreezeAsserted();
	}

	/**
	 * Fill large blocks of memory.<br>
	 * Fill is used in conjunction with the <code>writeMem</code> command. The
	 * maximal number of words is defined by
	 * <code>MAX_NOF_WORDS_FAST_DOWNLOAD</code> for 1 and 2 byte (word) data.
	 * For 4 byte (long) data, only half the size of
	 * <code>MAX_NOF_WORDS_FAST_DOWNLOAD</code> is available as 4 bytes of
	 * data has to be split in two packets (2 x 2 bytes).<br>
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
	public static void fillMem(int[] downloadData, int dataLength)
			throws BDIException, USBException, DispatchException {
		bdi.fillMem(downloadData, dataLength);
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
	public static int[] dumpMem(int nofData) throws USBException,
			DispatchException, BDIException {
		return bdi.dumpMem(nofData);
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
	public static void writeMem(int addr, int value, int size)
			throws USBException, DispatchException, BDIException {
		bdi.writeMem(addr, value, size);
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
	public static int readMem(int addr, int size) throws USBException,
			DispatchException, BDIException {
		return bdi.readMem(addr, size);
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
	public static int readUserReg(int reg) throws USBException,
			DispatchException, BDIException {
		return bdi.readUserReg(reg);
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
	public static void writeUserReg(int reg, int value) throws USBException,
			DispatchException, BDIException {
		bdi.writeUserReg(reg, value);
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
	public static int readSysReg(int reg) throws USBException,
			DispatchException, BDIException {
		return bdi.readSysReg(reg);
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
	public static void writeSysReg(int reg, int value) throws USBException,
			DispatchException, BDIException {
		bdi.writeSysReg(reg, value);
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
	public static boolean isTargetInDebugMode() {
		return bdi.isTargetInDebugMode();
	}
}
