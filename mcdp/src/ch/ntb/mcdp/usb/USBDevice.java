package ch.ntb.mcdp.usb;

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

	private static final int Timeout = 2000;

	static {
		// set data for our device
		USB.setIdVendor(IdVendor);
		USB.setIdProduct(IdProduct);
		USB.setConfiguration(Configuration);
		USB.setInterface(Interface);
		USB.setAltinterface(Altinterface);
		USB.setOUT_Endpoint_1(OUT_Endpoint_BDI);
		USB.setIN_Endpoint_1(IN_Endpoint_BDI);
		USB.setOUT_Endpoint_2(OUT_Endpoint_UART);
		USB.setIN_Endpoint_2(IN_Endpoint_UART);
		USB.setTimeout(Timeout);
		// reset USB
		USB.reset();
	}

	public static void open() throws USBException {
		USB.openUsbDevice();
	}

	public static void close() throws USBException {
		USB.closeUsbDevice();
	}

	public static void reset() throws USBException {
		USB.resetUsbDevice();
	}

	public static void write_BDI(byte[] data, int length) throws USBException {
		USB.write_EP1(data, length);
	}

	public static synchronized int read_BDI(byte[] data, int size)
			throws USBException {
		return USB.read_EP1(data, size);
	}

	public static void write_UART(byte[] data, int length) throws USBException {
		USB.write_EP2(data, length);
	}

	public static synchronized int read_UART(byte[] data, int size)
			throws USBException {
		return USB.read_EP2(data, size);
	}
}
