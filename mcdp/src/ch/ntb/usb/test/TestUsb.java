package ch.ntb.usb.test;

import java.io.IOException;

import ch.ntb.usb.*;

public class TestUsb {

	static Usb_Bus bus;

	static final short EZ_USB_DevKit_idVendor = 0x04B4;

	static final short EZ_USB_DevKit_idProduct = (short) 0x1004; // 0x8613;

	static final int TIMEOUT = 3000;

	static final int CONFIGURATION = 1;

	static final int INTERFACE = 0;

	static final int ALTINTERFACE = 0; // 1;

	static final int OUT_ENDPOINT = 0x02; // 0x04;

	static final int IN_ENDPOINT = 0x86; // 0x88;

	static final int MAX_BYTEARRAY_SIZE = 512;

	static final int PACKET_HEADER_1 = 0x33;	// first byte of header
	static final int PACKET_HEADER_2 = 0x5B;	// second byte of header
	static final int PACKET_END = 0x1F;			// last byte of packet
	static final int PACKET_DATA_OFFSET = 6;	// offset to the first byte of data
	static final int PACKET_MIN_LENGTH = 7;		// minimal Length of a packet (no payload)

	
//	 Main Types
	static final int MTYPE_ERROR = 0x01;			// Errors before dispatching data
	static final int MTYPE_BDI = 0x02;
	static final int MTYPE_UART_1 = 0x03;

//	 Sub Types
//	 ERRORS
	static final int STYPE_ERROR_HEADER = 0x01;	// Header of packet wrong
	static final int STYPE_ERROR_PACKET_END = 0x02;	// Packet end wrong

//	 BDI
	static final int STYPE_BDI_35IN = 0x01;		// 35 Bit Packet to BDI
	static final int STYPE_BDI_35OUT = 0x02;	// 35 Bit Packet from BDI
	static final int STYPE_BDI_10IN = 0x03;		// 10 Bit Packet to BDI
	static final int STYPE_BDI_10OUT = 0x04;	// 10 Bit Packet from BDI
	static final int STYPE_BDI_FD_DATA = 0x05;	// Fast Download Data
	static final int STYPE_BDI_ERROR_FD_LENGTH = 0x06;	// Error if length in FD packet too small

//	 UART 1
	static final int STYPE_UART_1_IN = 0x11;	// Data to UART 1
	static final int STYPE_UART_1_OUT = 0x22;	// Data from UART 1

	private static void do_read(Usb_Bus bus, int dev_handle) {
		byte[] data = new byte[MAX_BYTEARRAY_SIZE];
		int res = read_bulkdata(dev_handle, CONFIGURATION, INTERFACE, ALTINTERFACE,
				IN_ENDPOINT, data, MAX_BYTEARRAY_SIZE, TIMEOUT);
		if (res <= 0) {
			System.err.println("Error on read_bulkdata "
					+ LibusbWin.usb_strerror());
			return;
		}
		System.out.println("read_bulkdata length: " + res);
		System.out.print("Data: ");
		for (int i = 0; i < res; i++) {
			System.out.print("0x" + String.format("%1$02X", data[i]) + " ");
		}
		System.out.println();
		// System.out.println("Data: " + logBytesAsChars(data));
	}

	private static String logBytesAsChars(byte[] data) {
		StringBuffer sb = new StringBuffer(MAX_BYTEARRAY_SIZE);
		int i = 0;
		while ((data[i] != 0) && (i < MAX_BYTEARRAY_SIZE)) {
			sb.append((char) data[i]);
			i++;
		}
		return sb.toString();
	}

	private static void findFirstDevice() {
		// find a valid device
		Usb_Device dev;
		while (bus != null) {
			dev = bus.devices;
			while (dev != null) {
				System.out.println("dev.devnum " + dev.devnum);
				int dev_handle = LibusbWin.usb_open(dev);
				System.out.println("dev_handle " + dev_handle);
				if (dev_handle > 0) {
					if (LibusbWin.usb_close(dev_handle) < 0) {
						System.err
								.println("error on usb.usb_close(dev_handle)");
					}
					return;
				}
				dev = dev.next;
			}
			bus = bus.next;
		}
	}

	private static void openDevice(Usb_Bus bus) {
		int handle = Utils.openUsb_Device(bus, EZ_USB_DevKit_idVendor,
				EZ_USB_DevKit_idProduct);
		if (handle > 0) {
			System.out.println("Usb_device_handle: " + handle);
			System.out.println("closed: " + LibusbWin.usb_close(handle));
		}
	}

	private static int write_bulkdata(int dev_handle, int configuration,
			int interface_, int altinterface, int endpoint, byte[] data,
			int length, int timeout) {
		int res = LibusbWin.usb_set_configuration(dev_handle, configuration);
		if (res < 0) {
			System.err.println("Error on usb_set_configuration: "
					+ LibusbWin.usb_strerror());
			return res;
		}
		res = LibusbWin.usb_claim_interface(dev_handle, interface_);
		if (res < 0) {
			System.err.println("Error on usb_claim_interface: "
					+ LibusbWin.usb_strerror());
			return res;
		}
		LibusbWin.usb_set_altinterface(dev_handle, altinterface);
		res = LibusbWin.usb_bulk_write(dev_handle, endpoint, data, length,
				timeout);
		LibusbWin.usb_release_interface(dev_handle, interface_);
		if (res <= 0) {
			System.err.println("Error on usb_bulk_write: "
					+ LibusbWin.usb_strerror());
		}
		return res;
	}

	private static int read_bulkdata(int dev_handle, int configuration,
			int interface_, int altinterface, int endpoint, byte[] data,
			int size, int timeout) {
		int res = LibusbWin.usb_set_configuration(dev_handle, configuration);
		if (res < 0) {
			System.err.println("Error on usb_set_configuration: "
					+ LibusbWin.usb_strerror());
			return res;
		}
		res = LibusbWin.usb_claim_interface(dev_handle, interface_);
		if (res < 0) {
			System.err.println("Error on read_bulkdata: "
					+ LibusbWin.usb_strerror());
			return res;
		}
		LibusbWin.usb_set_altinterface(dev_handle, altinterface);
		res = LibusbWin
				.usb_bulk_read(dev_handle, endpoint, data, size, timeout);
		LibusbWin.usb_release_interface(dev_handle, interface_);
		if (res <= 0) {
			System.err.println("Error on usb_bulk_read: "
					+ LibusbWin.usb_strerror());
			return res;
		}
		return res;
	}

	private static void do_write(Usb_Bus bus, int dev_handle) {
		// byte[] data = new String("Data to send...").getBytes();
		byte[] data = new byte[512];
		data[0] = PACKET_HEADER_1;	// header
		data[1] = (byte) PACKET_HEADER_2;	// header
		data[2] = MTYPE_BDI;	// header
		data[3] = STYPE_BDI_35IN;	// header
		data[4] = 0x00;				// length of payload
		data[5] = 0x05;
		data[6] = 0x01; 			// payload
		data[7] = 0x03;
		data[8] = 0x07;
		data[9] = 0x0F;
		data[10] = 0x7F;
		data[11] = (byte) PACKET_END;		// packet end
		int length = 12;

		int res = write_bulkdata(dev_handle, CONFIGURATION, INTERFACE, ALTINTERFACE,
				OUT_ENDPOINT, data, length, TIMEOUT);
		if ( res <= 0) {
			System.err.println("Error on write_bulkdata");
			return;
		}
		System.out.print(res + " Bytes sent: ");
		for (int i = 0; i < res; i++) {
			System.out.print("0x" + String.format("%1$02X", data[i]) + " ");
		}
		System.out.println();

		System.out.println("write_bulkdata done");
	}

	private static String logBytes(byte[] data) {
		StringBuffer sb = new StringBuffer(MAX_BYTEARRAY_SIZE);
		for (int i = 0; i < data.length; i++) {
			sb.append(data[i]);
			sb.append(" ");
		}
		return sb.toString();
	}

	private static void do_write_read(Usb_Bus bus) {
		int dev_handle = Utils.openUsb_Device(bus, EZ_USB_DevKit_idVendor,
				EZ_USB_DevKit_idProduct);
		if (dev_handle <= 0) {
			System.err.println("Error on openUsb_Device: " + dev_handle);
			return;
		}
		boolean run = true;
		char c = 'a';
		while(run){
			try {
				c = (char) System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
				run = false;
			}
			switch (c) {
			case 'w':
				do_write(bus, dev_handle);
				break;
			case 'r':
				do_read(bus, dev_handle);
				break;
			case 'x':
				run = false;
				break;
			default:
				break;
			}
		}
		LibusbWin.usb_close(dev_handle);
	}

	public static void main(String[] args) {
		LibusbWin.usb_init();
		LibusbWin.usb_find_busses();
		LibusbWin.usb_find_devices();

		bus = LibusbWin.usb_get_busses();
		if (bus == null) {
			System.err.println("Error on usb.usb_get_busses(): "
					+ LibusbWin.usb_strerror());
			return;
		}
		// Utils.logUsb(bus);
		// openDevice(bus);

		do_write_read(bus);

		System.out.println("LibusbWin done");
	}
}
