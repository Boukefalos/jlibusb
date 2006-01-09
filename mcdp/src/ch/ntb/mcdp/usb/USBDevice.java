package ch.ntb.mcdp.usb;

import ch.ntb.usb.Device;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;

public class USBDevice {

	private final static short IdVendor = (short) 0x8235;

	private final static short IdProduct = 0x0100;

	private final static int Configuration = 1;

	private final static int Interface = 0;

	private final static int Altinterface = 0;

	private static final int OUT_Endpoint_BDI = 0x02;

	private static final int IN_Endpoint_BDI = 0x86;

	private static final int OUT_Endpoint_UART = 0x04;

	private static final int IN_Endpoint_UART = 0x88;

	private static final int BDI_Timeout = 1000;

	private static final int UART_Timeout = 50;
	
	private static Device dev;

	static {
		// set data for our device
		dev = USB.getDevice(IdVendor, IdProduct);
	}

	public static void open() throws USBException {
		dev.open(Configuration, Interface, Altinterface);
	}

	public static void close() throws USBException {
		dev.close();
	}

	public static void reset() throws USBException {
		dev.reset();
	}

	public static void write_BDI(byte[] data, int length) throws USBException {
		dev.bulkwrite(OUT_Endpoint_BDI, data, length, BDI_Timeout);
	}

	public static synchronized int read_BDI(byte[] data, int size)
			throws USBException {
		return dev.bulkread(IN_Endpoint_BDI, data, size, BDI_Timeout);
	}

	public static void write_UART(byte[] data, int length) throws USBException {
		dev.bulkwrite(OUT_Endpoint_UART, data, length, UART_Timeout);
	}

	public static synchronized int read_UART(byte[] data, int size)
			throws USBException {
		return dev.bulkread(IN_Endpoint_UART, data, size, UART_Timeout);
	}
}
