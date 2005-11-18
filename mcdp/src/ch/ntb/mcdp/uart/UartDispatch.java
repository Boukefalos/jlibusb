package ch.ntb.mcdp.uart;

import java.util.LinkedList;

import ch.ntb.mcdp.usb.DataPacket;
import ch.ntb.mcdp.usb.Dispatch;
import ch.ntb.mcdp.usb.DispatchException;
import ch.ntb.mcdp.usb.USBDevice;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;
import ch.ntb.usb.USBTimeoutException;

public class UartDispatch {

	public static final int MAX_UART_PAYLOAD = USB.MAX_DATA_SIZE
			- DataPacket.PACKET_MIN_LENGTH;

	private static boolean running = false;

	static Thread dispatchThread;

	static LinkedList<Uart> uarts = new LinkedList<Uart>();

	public static void start() {
		if (dispatchThread == null) {
			dispatchThread = new Thread() {
				@Override
				public void run() {
					while (running) {
						DataPacket data;
						try {
							data = Dispatch.readUART();
							if (data != null) {
								while (uarts.iterator().hasNext()) {
									Uart obj = uarts.iterator().next();
									if (obj.getSTYPE_OUT() == data.subtype) {
										obj.in.bufferList.add(data);
									}
								}
							}
						} catch (USBTimeoutException e) {
							// ignore TimeoutExceptions
						} catch (USBException e) {
							// TODO: Exceptionhandling
							e.printStackTrace();
						} catch (DispatchException e) {
							// TODO: Exceptionhandling
							e.printStackTrace();
						}
					}
				}
			};
		} else {
			running = true;
			dispatchThread.start();
		}
	}

	public static void stop() {
		running = false;
	}

	public LinkedList<Uart> getUartList() {
		return uarts;
	}

	public static void write(byte packetSubType, byte[] data, int len)
			throws USBException {
		byte[] usbData = new byte[len + DataPacket.PACKET_MIN_LENGTH];
		usbData[0] = DataPacket.PACKET_HEADER;
		usbData[1] = Dispatch.MTYPE_UART;
		usbData[2] = packetSubType;
		for (int i = 0; i < len; i++) {
			usbData[DataPacket.PACKET_DATA_OFFSET + i] = data[i];
		}
		usbData[DataPacket.PACKET_DATA_OFFSET + len] = DataPacket.PACKET_END;
		USBDevice.write_UART(data, len);
	}
}
