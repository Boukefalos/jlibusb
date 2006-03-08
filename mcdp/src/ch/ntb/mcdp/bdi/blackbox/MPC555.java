package ch.ntb.mcdp.bdi.blackbox;

import ch.ntb.mcdp.bdi.BDIException;
import ch.ntb.mcdp.usb.DispatchException;
import ch.ntb.mcdp.usb.USBDevice;
import ch.ntb.usb.USBException;

public class MPC555 {

	private static ch.ntb.mcdp.bdi.MPC555 bdi;

	/**
	 * Create a new BDI instance.
	 */
	static {
		bdi = new ch.ntb.mcdp.bdi.MPC555(USBDevice.getDevice());
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
		bdi.startFastDownload(startAddr);
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
		bdi.fastDownload(downloadData, dataLength);
	}

	/**
	 * Stop the fast download procedure.<br>
	 * Use this command after <code>startFastDownload(...)</code> and
	 * <code>fastDownload(...)</code>.
	 * 
	 * @throws USBException
	 * @throws DispatchException
	 * @throws BDIException
	 */
	public static void stopFastDownload() throws USBException,
			DispatchException, BDIException {
		bdi.stopFastDownload();
	}

	public static void writeMem(int addr, int value, int size)
			throws USBException, DispatchException, BDIException {
		bdi.writeMem(addr, value, size);
	}

	public static int readMem(int addr, int size) throws USBException,
			DispatchException, BDIException {
		return bdi.readMem(addr, size);
	}

	public static void writeMemSeq(int value, int size) throws USBException,
			DispatchException, BDIException {
		bdi.writeMemSeq(value, size);
	}

	public static int readMemSeq(int size) throws USBException,
			DispatchException, BDIException {
		return bdi.readMemSeq(size);
	}

	public static int readGPR(int gpr) throws USBException, DispatchException,
			BDIException {
		return bdi.readGPR(gpr);
	}

	public static void writeGPR(int gpr, int value) throws USBException,
			DispatchException, BDIException {
		bdi.writeGPR(gpr, value);
	}

	public static int readSPR(int spr) throws USBException, DispatchException,
			BDIException {
		return bdi.readSPR(spr);
	}

	public static void writeSPR(int spr, int value) throws USBException,
			DispatchException, BDIException {
		bdi.writeSPR(spr, value);
	}

	public static int readMSR() throws USBException, DispatchException,
			BDIException {
		return bdi.readMSR();
	}

	public static void writeMSR(int value) throws USBException,
			DispatchException, BDIException {
		bdi.writeMSR(value);
	}

	public static long readFPR(int fpr, int tmpMemAddr) throws USBException,
			DispatchException, BDIException {
		return bdi.readFPR(fpr, tmpMemAddr);
	}

	public static void writeFPR(int fpr, int tmpMemAddr, long value)
			throws USBException, DispatchException, BDIException {
		bdi.writeFPR(fpr, tmpMemAddr, value);
	}

	public static int readCR() throws USBException, DispatchException,
			BDIException {
		return bdi.readCR();
	}

	public static void writeCR(int value) throws USBException,
			DispatchException, BDIException {
		bdi.writeCR(value);
	}

	public static int readFPSCR() throws USBException, DispatchException,
			BDIException {
		return bdi.readFPSCR();
	}

	public static void writeFPSCR(int value) throws USBException,
			DispatchException, BDIException {
		bdi.writeFPSCR(value);
	}

	/**
	 * Return the last known state of the freeze signal. This value may not be
	 * up to date as the target state may have changed meanwhile. To get the up
	 * to date value use <code>isFreezeAsserted</code> which will issue an USB
	 * request, read the freeze signal and update the internal value returned by
	 * this method.
	 * 
	 * @return the last known state of the freeze signal
	 */
	public static boolean isTargetInDebugMode() {
		return bdi.isTargetInDebugMode();
	}

	/**
	 * Read the currently stored value of the GPR 30 register.<br>
	 * This value is updated when entering debug mode (break -> prologue).
	 * 
	 * @return the store value of this register
	 */
	public static int getGpr30() {
		return bdi.getGpr30();
	}

	/**
	 * Set the value of the GPR 30 register.<br>
	 * This value is written to the GPR30 register when the microcontroller
	 * resumes from debug mode (go -> epilogue).
	 * 
	 * @param value
	 *            value to write to the register
	 */
	public static void setGpr30(int value) {
		bdi.setGpr30(value);
	}

	/**
	 * Read the currently stored value of the GPR 31 register.<br>
	 * This value is updated when entering debug mode (break -> prologue).
	 * 
	 * @return the store value of this register
	 */
	public static int getGpr31() {
		return bdi.getGpr31();
	}

	/**
	 * Set the value of the GPR 31 register.<br>
	 * This value is written to the GPR31 register when the microcontroller
	 * resumes from debug mode (go -> epilogue).
	 * 
	 * @param value
	 *            value to write to the register
	 */
	public static void setGpr31(int value) {
		bdi.setGpr31(value);
	}
}
