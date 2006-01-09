package ch.ntb.usb.test;

import ch.ntb.usb.LibusbWin;
import ch.ntb.usb.USBException;
import ch.ntb.usb.USB_old;
import ch.ntb.usb.Usb_Bus;

public class TestAppUsb_old {

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

	static final int MAX_BYTEARRAY_SIZE = 512;

	static boolean dev_opened = false;

	static void openUsbDevice() {
		try {
			USB_old.openUsbDevice();
		} catch (USBException e) {
			e.printStackTrace();
		}
		System.out.println("device opened, interface claimed");
	}

	static void closeUsbDevice() {
		try {
			USB_old.closeUsbDevice();
		} catch (USBException e) {
			e.printStackTrace();
		}
		System.out.println("device closed");
	}

	static void write(byte[] data, int length) {
		try {
			USB_old.write_EP1(data, length, TIMEOUT);
		} catch (USBException e) {
			e.printStackTrace();
		}
		int res = 0;
		System.out.print("write_bulkdata: " + res + " Bytes sent: ");
		for (int i = 0; i < res; i++) {
			System.out.print("0x" + String.format("%1$02X", data[i]) + " ");
		}
		System.out.println();
	}

	static void read() {
		if (!dev_opened) {
			System.out.println("Open Device first");
			return;
		}
		byte[] data = new byte[MAX_BYTEARRAY_SIZE];
		int res = LibusbWin.usb_bulk_read(usb_dev_handle, IN_ENDPOINT, data,
				MAX_BYTEARRAY_SIZE, TIMEOUT);
		if (res < 0) {
			System.err.println("Error on LibusbWin.usb_bulk_read: "
					+ LibusbWin.usb_strerror());
		}
		System.out.print("read_bulkdata: " + res + " Bytes received: ");
		System.out.print("Data: ");
		for (int i = 0; i < res; i++) {
			System.out.print("0x" + String.format("%1$02X", data[i]) + " ");
		}
		System.out.println();

	}

	private static boolean claim_interface(int dev_handle, int configuration,
			int interface_, int altinterface) {
		int res = LibusbWin
				.usb_set_configuration(usb_dev_handle, configuration);
		if (res < 0) {
			System.err.println("Error on LibusbWin.usb_set_configuration: "
					+ LibusbWin.usb_strerror());
			return res >= 0;
		}
		res = LibusbWin.usb_claim_interface(dev_handle, interface_);
		if (res < 0) {
			System.err.println("Error on LibusbWin.usb_claim_interface: "
					+ LibusbWin.usb_strerror());
			return res >= 0;
		}
		res = LibusbWin.usb_set_altinterface(dev_handle, altinterface);
		if (res < 0) {
			System.err.println("Error on LibusbWin.usb_set_altinterface: "
					+ LibusbWin.usb_strerror());
		}
		return res >= 0;
	}

	private static boolean release_interface(int dev_handle, int interface_) {
		int res = LibusbWin.usb_release_interface(dev_handle, interface_);
		if (res < 0) {
			System.err.println("Error on LibusbWin.usb_release_interface: "
					+ LibusbWin.usb_strerror());
		}
		return res >= 0;
	}

	// private static int write_bulkdata(int dev_handle, int endpoint,
	// byte[] data, int length, int timeout) {
	// int res = LibusbWin.usb_bulk_write(dev_handle, endpoint, data, length,
	// timeout);
	// if (res < 0) {
	// System.err.println("Error on LibusbWin.usb_bulk_write: "
	// + LibusbWin.usb_strerror());
	// }
	// return res;
	// }
	//
	// private static int read_bulkdata(int dev_handle, int interface_,
	// int altinterface, int endpoint, byte[] data, int size, int timeout) {
	// int res = LibusbWin.usb_bulk_read(dev_handle, endpoint, data, size,
	// timeout);
	// if (res < 0) {
	// System.err.println("Error on LibusbWin.usb_bulk_read: "
	// + LibusbWin.usb_strerror());
	// }
	// return res;
	// }
}
