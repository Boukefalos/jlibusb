package ch.ntb.mcdp.uart.blackbox;

import ch.ntb.mcdp.uart.UartDispatch;
import ch.ntb.mcdp.usb.DataPacket;
import ch.ntb.mcdp.usb.Dispatch;
import ch.ntb.mcdp.usb.USBDevice;
import ch.ntb.usb.USBException;

public class Uart0 {

	// UART 0 Subtypes
	/**
	 * Data to UART 0
	 */
	private static final byte STYPE_UART_0_IN = 0x11;

	public static final int MAX_UART_PAYLOAD = UartDispatch.MAX_UART_PAYLOAD;

	private static void write(byte packetSubType, byte[] data, int len)
			throws USBException {
		byte[] usbData = new byte[len + DataPacket.PACKET_MIN_LENGTH];
		usbData[0] = DataPacket.PACKET_HEADER;
		usbData[1] = Dispatch.MTYPE_UART;
		usbData[2] = packetSubType;
		usbData[3] = (byte) (len / 0x100);
		usbData[4] = (byte) (len & 0xFF);
		for (int i = 0; i < len; i++) {
			usbData[DataPacket.PACKET_DATA_OFFSET + i] = data[i];
		}
		usbData[DataPacket.PACKET_DATA_OFFSET + len] = DataPacket.PACKET_END;
		USBDevice.write_BDI(usbData, usbData.length);
	}

	/**
	 * Write a Uart data-packet to the target. The maximal number of bytes is
	 * specified by <code>MAX_UART_PAYLOAD</code>. If more data is sent in
	 * one packet, only <code>MAX_UART_PAYLOAD</code> bytes are forwarded to
	 * the uart.
	 * 
	 * @param data
	 *            The data to be sent.
	 * @param len
	 *            Length of the data to be sent.
	 * @return true if the data has been sent successfully
	 */
	public static boolean write(byte[] data, int len) {
		try {
			write(STYPE_UART_0_IN, data, len);
		} catch (USBException e) {
			return false;
		}
		return true;
	}

	/**
	 * Try to read uart data from the device.
	 * 
	 * @return uart data or null if no data is available or an exception occured
	 */
	public static byte[] read() {
		DataPacket packet;
		try {
			packet = Dispatch.readUART();
			return packet.data;
		} catch (Exception e) {
			return null;
		}
	}
}
