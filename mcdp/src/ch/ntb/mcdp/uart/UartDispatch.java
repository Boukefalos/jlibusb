package ch.ntb.mcdp.uart;

import java.util.Iterator;
import java.util.LinkedList;

import ch.ntb.mcdp.usb.DataPacket;
import ch.ntb.mcdp.usb.Dispatch;
import ch.ntb.mcdp.usb.DispatchException;
import ch.ntb.mcdp.usb.USBDevice;
import ch.ntb.usb.USBException;
import ch.ntb.usb.USBTimeoutException;

public class UartDispatch {

	/**
	 * Maximal number of bytes allowed as UART payload. This value is specified
	 * in <code>UART_INBUF_LEN</code> in the file <code>uart.h</code>.
	 */
	public static final int MAX_UART_PAYLOAD = 128;

	private static boolean running = false;

	static Thread dispatchThread;

	static LinkedList<Uart> uarts = new LinkedList<Uart>();

	/**
	 * Starts the read thread for all Uarts. If the thread is already running,
	 * no action is taken.
	 */
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
								Iterator iterator = uarts.iterator();
								while (iterator.hasNext()) {
									Uart uartObj = (Uart) iterator.next();
									if (uartObj.getSTYPE_OUT() == data.subtype) {
										uartObj.in.bufferList.add(data);
									}
								}
							}
							// TODO: remove
							sleep(200);
						} catch (USBTimeoutException e) {
							// ignore TimeoutExceptions
						} catch (USBException e) {
							// TODO: Exceptionhandling
							e.printStackTrace();
							try {
								sleep(2000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						} catch (DispatchException e) {
							// TODO: Exceptionhandling
							e.printStackTrace();
							try {
								sleep(2000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						} catch (InterruptedException e) {
							// TODO Exceptionhandling
							e.printStackTrace();
						}
					}
				}
			};
		}
		if (!running) {
			running = true;
			dispatchThread.start();
		}
	}

	/**
	 * Stops the Uart read-Thread.
	 */
	public static void stop() {
		running = false;
	}

	/**
	 * Check whether the read-Thread is still running.
	 * 
	 * @return state of the read-Thread
	 */
	public static boolean isRunning() {
		return running;
	}

	/**
	 * Get the LinkedList which contains all Uart-objects.
	 * 
	 * @return The LinkedList containing all Uart-objects.
	 */
	public static LinkedList<Uart> getUartList() {
		return uarts;
	}

	/**
	 * Write a Uart data-packet to the target. The maximal number of bytes is
	 * specified by <code>MAX_UART_PAYLOAD</code>. If more data is sent in
	 * one packet, only <code>MAX_UART_PAYLOAD</code> bytes are forwarded to
	 * the uart.
	 * 
	 * @param packetSubType
	 *            The Subtype specified in <code>Dispatch.h</code>.
	 * @param data
	 *            The data to be sent.
	 * @param len
	 *            Length of the data to be sent.
	 * @throws USBException
	 */
	protected static void write(byte packetSubType, byte[] data, int len)
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
}
