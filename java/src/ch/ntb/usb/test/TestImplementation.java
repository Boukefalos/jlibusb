package ch.ntb.usb.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import ch.ntb.usb.Device;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;
import ch.ntb.usb.Usb_Bus;
import ch.ntb.usb.logger.LogUtil;

public class TestImplementation {

	private static Logger logger = LogUtil.ch_ntb_usb_test;

	static Usb_Bus bus;

	static int usb_dev_handle = 0;

	static short IdVendor = 0x04B4;

	static short IdProduct = (short) 0x1004;

	static int TIMEOUT = 1000;

	static int CONFIGURATION = 1;

	static int INTERFACE = 0;

	static int ALTINTERFACE = 0;

	static int OUT_ENDPOINT = 0x02;

	static int IN_ENDPOINT = 0x86;

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
			lenWritten = dev.bulkwrite(OUT_ENDPOINT, data, length, TIMEOUT,
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
			lenRead = dev.bulkread(IN_ENDPOINT, data, dev.getMaxPacketSize(),
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
