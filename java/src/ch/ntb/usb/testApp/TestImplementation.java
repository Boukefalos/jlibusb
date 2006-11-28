/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <libusb@drip.ch>
 *
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.testApp;

import java.util.logging.Level;
import java.util.logging.Logger;

import ch.ntb.usb.Device;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;
import ch.ntb.usb.logger.LogUtil;

public class TestImplementation {

	private static final Logger logger = LogUtil.getLogger("ch.ntb.usb.test");

	public static String sendData = "0x5b 0x02 0x01 0x00 0x03 0x03 0xf0 0xf0 0x1f";

	public static short IdVendor = (short) 0x8235;

	public static short IdProduct = 0x0200;

	public static int TIMEOUT = 2000;

	public static int CONFIGURATION = 1;

	public static int INTERFACE = 0;

	public static int ALTINTERFACE = 0;

	public static int OUT_ENDPOINT = 0x01;

	public static int IN_ENDPOINT = 0x82;

	private static Device dev = null;

	static {
		logger.setLevel(Level.ALL);
	}

	static void openUsbDevice() {
		dev = USB.getDevice(IdVendor, IdProduct);
		try {
			dev.open(CONFIGURATION, INTERFACE, ALTINTERFACE);
			logger.info("device opened, interface claimed");
		} catch (USBException e) {
			e.printStackTrace();
		}
	}

	static void closeUsbDevice() {
		try {
			dev.close();
			logger.info("device closed");
		} catch (USBException e) {
			e.printStackTrace();
		}
	}

	static void resetUsbDevice() {
		try {
			dev.reset();
			logger.info("device reset");
		} catch (USBException e) {
			e.printStackTrace();
		}
	}

	static void write(byte[] data, int length) {
		int lenWritten = 0;
		try {
			lenWritten = dev.writeBulk(OUT_ENDPOINT, data, length, TIMEOUT,
					false);
			StringBuffer sb = new StringBuffer("write_bulkdata: " + lenWritten
					+ " Bytes sent: ");
			for (int i = 0; i < lenWritten; i++) {
				sb.append("0x" + String.format("%1$02X", data[i]) + " ");
			}
			logger.info(sb.toString());
		} catch (USBException e) {
			e.printStackTrace();
		}
	}

	static void read() {
		byte[] data = new byte[dev.getMaxPacketSize()];
		int lenRead = 0;
		try {
			lenRead = dev.readBulk(IN_ENDPOINT, data, dev.getMaxPacketSize(),
					TIMEOUT, false);
			StringBuffer sb = new StringBuffer("read_bulkdata: " + lenRead
					+ " Bytes received: Data: ");
			for (int i = 0; i < lenRead; i++) {
				sb.append("0x" + String.format("%1$02X", data[i]) + " ");
			}
			logger.info(sb.toString());
		} catch (USBException e) {
			e.printStackTrace();
		}
	}
}
