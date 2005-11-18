package ch.ntb.usb;

import ch.ntb.usb.LibusbWin;
import ch.ntb.usb.Usb_Bus;
import ch.ntb.usb.Usb_Device;
import ch.ntb.usb.Usb_Device_Descriptor;

public class USB {

	public static final int MAX_DATA_SIZE = 512;

	private static final int TIMEOUT_ERROR_CODE = -116;

	private static final boolean DEBUG_ON = true;

	private static Usb_Bus bus;

	private static int usb_dev_handle = 0;

	private static short IdVendor = -1;

	private static short IdProduct = -1;

	private static int Timeout = 3000;

	private static int Configuration = -1;

	private static int Interface = -1;

	private static int Altinterface = -1;

	private static int OUT_Endpoint_1 = -1;

	private static int IN_Endpoint_1 = -1;

	private static int OUT_Endpoint_2 = -1;

	private static int IN_Endpoint_2 = -1;

	public static void reset() {
		bus = null;
		usb_dev_handle = 0;

		if (DEBUG_ON) {
			System.out.println("reset done");
		}
	}

	public static void openUsbDevice() throws USBException {

		int res;

		// open bus
		if (bus == null) {
			LibusbWin.usb_init();
			LibusbWin.usb_find_busses();
			LibusbWin.usb_find_devices();

			bus = LibusbWin.usb_get_busses();
			if (bus == null) {
				throw new USBException("LibusbWin.usb_get_busses(): "
						+ LibusbWin.usb_strerror());
			}
		}
		// search for device
		if (usb_dev_handle <= 0) {

			while (bus != null) {
				Usb_Device dev = bus.devices;
				while (dev != null) {
					// Usb_Device_Descriptor
					Usb_Device_Descriptor defDesc = dev.descriptor;
					if ((defDesc.idVendor == IdVendor)
							&& (defDesc.idProduct == IdProduct)) {
						if (DEBUG_ON) {
							System.out.println("Open device: " + dev.filename);
						}
						res = LibusbWin.usb_open(dev);
						if (res <= 0) {
							throw new USBException("LibusbWin.usb_open: "
									+ LibusbWin.usb_strerror());
						} else {
							usb_dev_handle = res;
						}
					}
					dev = dev.next;
				}
				bus = bus.next;
			}
			if (usb_dev_handle <= 0) {
				throw new USBException("UsbDevice with idVendor 0x"
						+ Integer.toHexString(IdVendor & 0xFFFF)
						+ " and idProduct 0x"
						+ Integer.toHexString(IdProduct & 0xFFFF)
						+ " not found");
			}
		}
		claim_interface(usb_dev_handle, Configuration, Interface, Altinterface);

		if (DEBUG_ON) {
			System.out.println("device opened, interface claimed");
		}
	}

	public static void closeUsbDevice() throws USBException {
		release_interface(usb_dev_handle, Interface);
		if (LibusbWin.usb_close(usb_dev_handle) < 0) {
			throw new USBException("LibusbWin.usb_close: "
					+ LibusbWin.usb_strerror());
		}

		if (DEBUG_ON) {
			System.out.println("device closed");
		}
	}

	public static void resetUsbDevice() throws USBException {
		if (LibusbWin.usb_reset(usb_dev_handle) < 0) {
			throw new USBException("LibusbWin.usb_reset: "
					+ LibusbWin.usb_strerror());
		}
		usb_dev_handle = 0;

		if (DEBUG_ON) {
			System.out.println("resetUsbDevie done");
		}
	}

	public static void write_EP1(byte[] data, int length) throws USBException {
		if (data == null) {
			throw new USBException("data must not be null");
		}
		if (length <= 0) {
			throw new USBException("size must be > 0");
		}
		int lenWritten = LibusbWin.usb_bulk_write(usb_dev_handle,
				OUT_Endpoint_1, data, length, Timeout);
		if (lenWritten < 0) {
			if (lenWritten == TIMEOUT_ERROR_CODE) {
				throw new USBTimeoutException("LibusbWin.usb_bulk_write EP1: "
						+ LibusbWin.usb_strerror());
			}
			throw new USBException("LibusbWin.usb_bulk_write EP1: "
					+ LibusbWin.usb_strerror());
		}

		if (DEBUG_ON) {
			System.out.print("write_bulkdata EP1: " + lenWritten
					+ " Bytes sent: ");
			for (int i = 0; i < lenWritten; i++) {
				System.out.print("0x" + String.format("%1$02X", data[i]) + " ");
			}
			System.out.println();
		}
	}

	public static int read_EP1(byte[] data, int size) throws USBException {
		if (data == null) {
			throw new USBException("data must not be null");
		}
		if (size <= 0) {
			throw new USBException("size must be > 0");
		}
		int lenRead = LibusbWin.usb_bulk_read(usb_dev_handle, IN_Endpoint_1,
				data, size, Timeout);
		if (lenRead < 0) {
			if (lenRead == TIMEOUT_ERROR_CODE) {
				throw new USBTimeoutException("LibusbWin.usb_bulk_read EP1: "
						+ LibusbWin.usb_strerror());
			}
			throw new USBException("LibusbWin.usb_bulk_read EP1: "
					+ LibusbWin.usb_strerror());
		}

		if (DEBUG_ON) {
			System.out.print("read_bulkdata EP1: " + lenRead
					+ " Bytes received: ");
			System.out.print("Data: ");
			for (int i = 0; i < lenRead; i++) {
				System.out.print("0x" + String.format("%1$02X", data[i]) + " ");
			}
			System.out.println();
		}
		return lenRead;
	}

	public static void write_EP2(byte[] data, int length) throws USBException {
		if (data == null) {
			throw new USBException("data must not be null");
		}
		if (length <= 0) {
			throw new USBException("size must be > 0");
		}
		int lenWritten = LibusbWin.usb_bulk_write(usb_dev_handle,
				OUT_Endpoint_2, data, length, Timeout);
		if (lenWritten < 0) {
			if (lenWritten == TIMEOUT_ERROR_CODE) {
				throw new USBTimeoutException("LibusbWin.usb_bulk_write EP2: "
						+ LibusbWin.usb_strerror());
			}
			throw new USBException("LibusbWin.usb_bulk_write EP2: "
					+ LibusbWin.usb_strerror());
		}

		if (DEBUG_ON) {
			System.out.print("write_bulkdata EP2: " + lenWritten
					+ " Bytes sent: ");
			for (int i = 0; i < lenWritten; i++) {
				System.out.print("0x" + String.format("%1$02X", data[i]) + " ");
			}
			System.out.println();
		}
	}

	public static int read_EP2(byte[] data, int size) throws USBException {
		if (data == null) {
			throw new USBException("data must not be null");
		}
		if (size <= 0) {
			throw new USBException("size must be > 0");
		}
		int lenRead = LibusbWin.usb_bulk_read(usb_dev_handle, IN_Endpoint_2,
				data, size, Timeout);
		if (lenRead < 0) {
			if (lenRead == TIMEOUT_ERROR_CODE) {
				throw new USBTimeoutException("LibusbWin.usb_bulk_read EP2: "
						+ LibusbWin.usb_strerror());
			}
			throw new USBException("LibusbWin.usb_bulk_read EP2: "
					+ LibusbWin.usb_strerror());
		}

		if (DEBUG_ON) {
			System.out.print("read_bulkdata EP2: " + lenRead
					+ " Bytes received: ");
			System.out.print("Data: ");
			for (int i = 0; i < lenRead; i++) {
				System.out.print("0x" + String.format("%1$02X", data[i]) + " ");
			}
			System.out.println();
		}
		return lenRead;
	}

	private static void claim_interface(int dev_handle, int configuration,
			int interface_, int altinterface) throws USBException {
		if (LibusbWin.usb_set_configuration(usb_dev_handle, configuration) < 0) {
			throw new USBException("LibusbWin.usb_set_configuration: "
					+ LibusbWin.usb_strerror());
		}
		if (LibusbWin.usb_claim_interface(dev_handle, interface_) < 0) {
			throw new USBException("LibusbWin.usb_claim_interface: "
					+ LibusbWin.usb_strerror());
		}
		if (LibusbWin.usb_set_altinterface(dev_handle, altinterface) < 0) {
			throw new USBException("LibusbWin.usb_set_altinterface: "
					+ LibusbWin.usb_strerror());
		}
	}

	private static void release_interface(int dev_handle, int interface_)
			throws USBException {
		if (LibusbWin.usb_release_interface(dev_handle, interface_) < 0) {
			throw new USBException("LibusbWin.usb_release_interface: "
					+ LibusbWin.usb_strerror());
		}
	}

	public static short getIdProduct() {
		return IdProduct;
	}

	public static void setIdProduct(short idProduct) {
		IdProduct = idProduct;
	}

	public static short getIdVendor() {
		return IdVendor;
	}

	public static void setIdVendor(short idVendor) {
		IdVendor = idVendor;
	}

	public static int getConfiguration() {
		return Configuration;
	}

	public static void setConfiguration(int configuration) {
		Configuration = configuration;
	}

	public static int getInterface() {
		return Interface;
	}

	public static void setInterface(int interface_) {
		Interface = interface_;
	}

	public static int getIN_Endpoint_1() {
		return IN_Endpoint_1;
	}

	public static void setIN_Endpoint_1(int in_endpoint_1) {
		IN_Endpoint_1 = in_endpoint_1;
	}

	public static int getOUT_Endpoint_1() {
		return OUT_Endpoint_1;
	}

	public static void setOUT_Endpoint_1(int out_endpoint_1) {
		OUT_Endpoint_1 = out_endpoint_1;
	}

	public static int getIN_Endpoint_2() {
		return IN_Endpoint_2;
	}

	public static void setIN_Endpoint_2(int in_endpoint_2) {
		IN_Endpoint_2 = in_endpoint_2;
	}

	public static int getOUT_Endpoint_2() {
		return OUT_Endpoint_2;
	}

	public static void setOUT_Endpoint_2(int out_endpoint_2) {
		OUT_Endpoint_2 = out_endpoint_2;
	}

	public static int getTimeout() {
		return Timeout;
	}

	public static void setTimeout(int timeout) {
		Timeout = timeout;
	}

	public static int getAltinterface() {
		return Altinterface;
	}

	public static void setAltinterface(int altinterface) {
		Altinterface = altinterface;
	}
}
