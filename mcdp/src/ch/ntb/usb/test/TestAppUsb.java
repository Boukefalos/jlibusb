package ch.ntb.usb.test;

import ch.ntb.usb.LibusbWin;
import ch.ntb.usb.Usb_Bus;
import ch.ntb.usb.Usb_Device;
import ch.ntb.usb.Usb_Device_Descriptor;

public class TestAppUsb {

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

	static void reset() {
		bus = null;
		usb_dev_handle = 0;
		dev_opened = false;
		System.out.println("reset done");
	}

	static void openUsbDevice() {

		int res;

		// open bus
		if (bus == null) {
			LibusbWin.usb_init();
			LibusbWin.usb_find_busses();
			LibusbWin.usb_find_devices();

			bus = LibusbWin.usb_get_busses();
			if (bus == null) {
				System.err.println("Error on LibusbWin.usb_get_busses(): "
						+ LibusbWin.usb_strerror());
				return;
			}
		}
		// search for device
		dev_opened = false;
		if (usb_dev_handle <= 0) {

			while (bus != null) {
				Usb_Device dev = bus.devices;
				while (dev != null) {
					// Usb_Device_Descriptor
					Usb_Device_Descriptor defDesc = dev.descriptor;
					if ((defDesc.idVendor == IdVendor)
							&& (defDesc.idProduct == IdProduct)) {
						System.out.println("Open device: " + dev.filename);
						res = LibusbWin.usb_open(dev);
						if (res <= 0) {
							System.err.println("Error on LibusbWin.usb_open: "
									+ LibusbWin.usb_strerror());
							return;
						} else {
							usb_dev_handle = res;
						}
					}
					dev = dev.next;
				}
				bus = bus.next;
			}
			if (usb_dev_handle <= 0) {
				System.out.println("UsbDevice with idVendor 0x"
						+ Integer.toHexString(IdVendor) + " and idProduct 0x"
						+ Integer.toHexString(IdProduct) + " not found");
				return;
			}
		}
		if (!claim_interface(usb_dev_handle, CONFIGURATION, INTERFACE,
				ALTINTERFACE)) {
			System.err.println("Error on claim_interface");
			return;
		}
		dev_opened = true;
		System.out.println("device opened, interface claimed");
	}

	static void closeUsbDevice() {
		if (!release_interface(usb_dev_handle, INTERFACE)) {
			System.err.println("Error on release_interface");
		}
		int res = LibusbWin.usb_close(usb_dev_handle);
		if (res < 0) {
			System.err.println("Error on LibusbWin.usb_close: "
					+ LibusbWin.usb_strerror());
		}
		dev_opened = false;
		bus = null;
		usb_dev_handle = -1;
		System.out.println("device closed");
	}

	static void write(byte[] data, int length) {
		if (!dev_opened) {
			System.out.println("Open Device first");
			return;
		}
		int res = LibusbWin.usb_bulk_write(usb_dev_handle, OUT_ENDPOINT, data,
				length, TIMEOUT);
		if (res < 0) {
			System.err.println("Error on LibusbWin.usb_bulk_write: "
					+ LibusbWin.usb_strerror());
		}
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
