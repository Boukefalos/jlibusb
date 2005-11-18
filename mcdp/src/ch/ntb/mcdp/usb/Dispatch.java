package ch.ntb.mcdp.usb;

import java.util.LinkedList;

import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;

public class Dispatch {

	// Main Types
	/**
	 * general errors
	 */
	public static final byte MTYPE_ERROR = 0x01;

	/**
	 * BDI specific packets
	 */
	public static final byte MTYPE_BDI = 0x02;

	/**
	 * UART specific packets
	 */
	public static final byte MTYPE_UART = 0x03;

	// Sub Types
	// ERRORS
	/**
	 * Unknown MTYPE
	 */
	public static final byte STYPE_ERROR_UNKNOWN_MTYPE = 0x70;

	/**
	 * Header of packet wrong
	 */
	public static final byte STYPE_ERROR_HEADER = 0x71;

	/**
	 * Packet end wrong
	 */
	public static final byte STYPE_ERROR_PACKET_END = 0x72;

	private static byte[] usbData = new byte[USB.MAX_DATA_SIZE];

	private static LinkedList<DataPacket> bdiData, uartData;

	static {
		bdiData = new LinkedList<DataPacket>();
		uartData = new LinkedList<DataPacket>();
	}

	public static void emptyBuffers() {
		bdiData.clear();
		uartData.clear();
	}

	private static void dispatch(byte[] data, int size)
			throws DispatchException {
		int index = 0, mainType, subtype;
		byte[] packetData;
		while (index < size) {
			if (data[index++] != DataPacket.PACKET_HEADER) {
				throw new DispatchException("PACKET_HEADER wrong: "
						+ data[index - 1]);
			}
			mainType = data[index++];
			subtype = data[index++];
			int dataLen = data[index++] * 0x100 + data[index++];
			if (data[index + dataLen] != DataPacket.PACKET_END) {
				throw new DispatchException("PACKET_END or packetLen ("
						+ dataLen + " bytes) wrong");
			}

			switch (mainType) {
			case MTYPE_ERROR:
				switch (subtype) {
				case STYPE_ERROR_HEADER:
					throw new DispatchException(
							"MTYPE_ERROR: STYPE_ERROR_HEADER");
				case STYPE_ERROR_PACKET_END:
					throw new DispatchException(
							"MTYPE_ERROR: STYPE_ERROR_PACKET_END");
				case STYPE_ERROR_UNKNOWN_MTYPE:
					throw new DispatchException(
							"MTYPE_ERROR: STYPE_ERROR_UNKNOWN_MTYPE");
				default:
					throw new DispatchException("MTYPE_ERROR: Unknown S_TYPE: "
							+ subtype);
				}
			case MTYPE_BDI:
				packetData = new byte[dataLen];
				// copy data to bdiData
				for (int i = 0; i < dataLen; i++) {
					packetData[i] = data[index + i];
				}
				bdiData.add(new DataPacket(subtype, packetData));
				break;
			case MTYPE_UART:
				packetData = new byte[dataLen];
				// copy data to uartData
				for (int i = 0; i < dataLen; i++) {
					packetData[i] = data[index + i];
				}
				uartData.add(new DataPacket(subtype, packetData));
				break;
			default:
				throw new DispatchException("Unknown MTYPE: " + mainType);
			}
			index += dataLen + 1;
		}
	}

	public static DataPacket readBDI() throws USBException, DispatchException {
		if (!bdiData.isEmpty()) {
			return bdiData.poll();
		}
		int dataLength = USBDevice.read_BDI(usbData, USB.MAX_DATA_SIZE);
		dispatch(usbData, dataLength);
		return bdiData.poll();
	}

	public static DataPacket readUART() throws USBException, DispatchException {
		if (!uartData.isEmpty()) {
			return uartData.poll();
		}
		int dataLength = USBDevice.read_UART(usbData, USB.MAX_DATA_SIZE);
		dispatch(usbData, dataLength);
		return uartData.poll();
	}
}
