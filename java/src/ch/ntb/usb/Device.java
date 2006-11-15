package ch.ntb.usb;

import java.util.logging.Level;
import java.util.logging.Logger;

import ch.ntb.usb.logger.LogUtil;

/**
 * This class represents an USB device.<br>
 * To get an instance of an USB device use <code>USB.getDevice(...)</code>.
 * 
 * @author schlaepfer
 */
public class Device {

	private static final Logger logger = LogUtil.getLogger("ch.ntb.usb");

	private static final int TIMEOUT_ERROR_CODE = -116;

	private int maxPacketSize;

	private int idVendor, idProduct, dev_configuration, dev_interface,
			dev_altinterface;

	private int usbDevHandle;

	private boolean resetOnFirstOpen, resetDone;

	private int resetTimeout = 1000;

	protected Device(short idVendor, short idProduct) {
		resetOnFirstOpen = false;
		resetDone = false;
		maxPacketSize = -1;
		this.idVendor = idVendor;
		this.idProduct = idProduct;
	}

	/**
	 * Opens the device and claims the specified configuration, interface and
	 * altinterface.<br>
	 * First the bus is enumerated. If the device is found its descriptors are
	 * read and the <code>maxPacketSize</code> value is updated. If no
	 * endpoints are found in the descriptors an exception is thrown.
	 * 
	 * @param configuration
	 *            the configuration
	 * @param interface_
	 *            the interface
	 * @param altinterface
	 *            the alternate interface. If no alternate interface must be set
	 *            <i>-1</i> can be used.
	 * @throws USBException
	 */
	public void open(int configuration, int interface_, int altinterface)
			throws USBException {
		this.dev_configuration = configuration;
		this.dev_interface = interface_;
		this.dev_altinterface = altinterface;

		Usb_Bus bus;

		if (usbDevHandle > 0) {
			throw new USBException("device opened, close or reset first");
		}

		// open bus
		LibusbWin.usb_init();
		LibusbWin.usb_find_busses();
		LibusbWin.usb_find_devices();

		bus = LibusbWin.usb_get_busses();
		if (bus == null) {
			throw new USBException("LibusbWin.usb_get_busses(): "
					+ LibusbWin.usb_strerror());
		}

		maxPacketSize = -1;

		// search for device
		while (bus != null) {
			Usb_Device dev = bus.devices;
			while (dev != null) {
				Usb_Device_Descriptor devDesc = dev.descriptor;
				if ((devDesc.idVendor == idVendor)
						&& (devDesc.idProduct == idProduct)) {
					logger.info("Open device: " + dev.filename);
					int res = LibusbWin.usb_open(dev);
					if (res <= 0) {
						throw new USBException("LibusbWin.usb_open: "
								+ LibusbWin.usb_strerror());
					}
					usbDevHandle = res;
					// get endpoint wMaxPacketSize
					Usb_Config_Descriptor[] confDesc = dev.config;
					for (int i = 0; i < confDesc.length; i++) {
						Usb_Interface[] int_ = confDesc[i].interface_;
						for (int j = 0; j < int_.length; j++) {
							Usb_Interface_Descriptor[] intDesc = int_[j].altsetting;
							for (int k = 0; k < intDesc.length; k++) {
								Usb_Endpoint_Descriptor[] epDesc = intDesc[k].endpoint;
								for (int l = 0; l < epDesc.length; l++) {
									maxPacketSize = Math.max(
											epDesc[l].wMaxPacketSize,
											maxPacketSize);
								}
							}
						}
					}
					if (maxPacketSize <= 0) {
						throw new USBException(
								"No USB endpoints found. Check the device configuration");
					}
				}
				dev = dev.next;
			}
			bus = bus.next;
		}
		if (usbDevHandle <= 0) {
			throw new USBException("USB device with idVendor 0x"
					+ Integer.toHexString(idVendor & 0xFFFF)
					+ " and idProduct 0x"
					+ Integer.toHexString(idProduct & 0xFFFF) + " not found");
		}
		claim_interface(usbDevHandle, configuration, interface_, altinterface);
		if (resetOnFirstOpen & !resetDone) {
			logger.info("reset on first open");
			resetDone = true;
			reset();
			try {
				Thread.sleep(resetTimeout);
			} catch (InterruptedException e) {
				//
			}
			open(configuration, interface_, altinterface);
		}
	}

	/**
	 * Release the claimed interface and close the opened device.<br>
	 * 
	 * @throws USBException
	 */
	public void close() throws USBException {
		if (usbDevHandle <= 0) {
			throw new USBException("invalid device handle");
		}
		release_interface(usbDevHandle, dev_interface);
		if (LibusbWin.usb_close(usbDevHandle) < 0) {
			usbDevHandle = 0;
			throw new USBException("LibusbWin.usb_close: "
					+ LibusbWin.usb_strerror());
		}
		usbDevHandle = 0;
		maxPacketSize = -1;
		logger.info("device closed");
	}

	/**
	 * Sends an USB reset to the device. The device handle will no longer be
	 * valid. To use the device again, {@link #open(int, int, int)} must be
	 * called.
	 * 
	 * @throws USBException
	 */
	public void reset() throws USBException {
		if (usbDevHandle <= 0) {
			throw new USBException("invalid device handle");
		}
		if (LibusbWin.usb_reset(usbDevHandle) < 0) {
			usbDevHandle = 0;
			throw new USBException("LibusbWin.usb_reset: "
					+ LibusbWin.usb_strerror());
		}
		usbDevHandle = 0;
		logger.info("device reset");
	}

	/**
	 * Write data to the device using a bulk transfer.<br>
	 * 
	 * @param out_ep_address
	 *            endpoint address to write to
	 * @param data
	 *            data to write to this endpoint
	 * @param length
	 *            length of the data
	 * @param timeout
	 *            amount of time in ms the device will try to send the data
	 *            until a timeout exception is thrown
	 * @param reopenOnTimeout
	 *            if set to true, the device will try to open the connection and
	 *            send the data again before a timeout exception is thrown
	 * @return the actual number of bytes written
	 * @throws USBException
	 */
	public int writeBulk(int out_ep_address, byte[] data, int length,
			int timeout, boolean reopenOnTimeout) throws USBException {
		if (usbDevHandle <= 0) {
			throw new USBException("invalid device handle");
		}
		if (data == null) {
			throw new USBException("data must not be null");
		}
		if (length <= 0) {
			throw new USBException("size must be > 0");
		}
		int lenWritten = LibusbWin.usb_bulk_write(usbDevHandle, out_ep_address,
				data, length, timeout);
		if (lenWritten < 0) {
			if (lenWritten == TIMEOUT_ERROR_CODE) {
				// try to reopen the device and send the data again
				if (reopenOnTimeout) {
					logger.info("try to reopen");
					reset();
					open(dev_configuration, dev_interface, dev_altinterface);
					return writeBulk(out_ep_address, data, length, timeout,
							false);
				}
				throw new USBTimeoutException("LibusbWin.usb_bulk_write: "
						+ LibusbWin.usb_strerror());
			}
			throw new USBException("LibusbWin.usb_bulk_write: "
					+ LibusbWin.usb_strerror());
		}

		logger.info("length written: " + lenWritten);
		if (logger.isLoggable(Level.FINEST)) {
			StringBuffer sb = new StringBuffer("bulkwrite, ep 0x"
					+ Integer.toHexString(out_ep_address) + ": " + lenWritten
					+ " Bytes sent: ");
			for (int i = 0; i < lenWritten; i++) {
				sb.append("0x" + String.format("%1$02X", data[i]) + " ");
			}
			logger.info(sb.toString());
		}
		return lenWritten;
	}

	/**
	 * Read data from the device using a bulk transfer.<br>
	 * 
	 * @param in_ep_address
	 *            endpoint address to read from
	 * @param data
	 *            data buffer for the data to be read
	 * @param size
	 *            the maximum requested data size
	 * @param timeout
	 *            amount of time in ms the device will try to receive data until
	 *            a timeout exception is thrown
	 * @param reopenOnTimeout
	 *            if set to true, the device will try to open the connection and
	 *            receive the data again before a timeout exception is thrown
	 * @return the actual number of bytes read
	 * @throws USBException
	 */
	public int readBulk(int in_ep_address, byte[] data, int size, int timeout,
			boolean reopenOnTimeout) throws USBException {
		if (usbDevHandle <= 0) {
			throw new USBException("invalid device handle");
		}
		if (data == null) {
			throw new USBException("data must not be null");
		}
		if (size <= 0) {
			throw new USBException("size must be > 0");
		}
		int lenRead = LibusbWin.usb_bulk_read(usbDevHandle, in_ep_address,
				data, size, timeout);
		if (lenRead < 0) {
			if (lenRead == TIMEOUT_ERROR_CODE) {
				// try to reopen the device and send the data again
				if (reopenOnTimeout) {
					logger.info("try to reopen");
					reset();
					open(dev_configuration, dev_interface, dev_altinterface);
					return readBulk(in_ep_address, data, size, timeout, false);
				}
				throw new USBTimeoutException("LibusbWin.usb_bulk_read: "
						+ LibusbWin.usb_strerror());
			}
			throw new USBException("LibusbWin.usb_bulk_read: "
					+ LibusbWin.usb_strerror());
		}

		logger.info("length read: " + lenRead);
		if (logger.isLoggable(Level.FINEST)) {
			StringBuffer sb = new StringBuffer("bulkread, ep 0x"
					+ Integer.toHexString(in_ep_address) + ": " + lenRead
					+ " Bytes received: ");
			for (int i = 0; i < lenRead; i++) {
				sb.append("0x" + String.format("%1$02X", data[i]) + " ");
			}
			logger.info(sb.toString());
		}
		return lenRead;
	}

	/**
	 * Write data to the device using a interrupt transfer.<br>
	 * 
	 * @param out_ep_address
	 *            endpoint address to write to
	 * @param data
	 *            data to write to this endpoint
	 * @param length
	 *            length of the data
	 * @param timeout
	 *            amount of time in ms the device will try to send the data
	 *            until a timeout exception is thrown
	 * @param reopenOnTimeout
	 *            if set to true, the device will try to open the connection and
	 *            send the data again before a timeout exception is thrown
	 * @return the actual number of bytes written
	 * @throws USBException
	 */
	public int writeInterrupt(int out_ep_address, byte[] data, int length,
			int timeout, boolean reopenOnTimeout) throws USBException {
		if (usbDevHandle <= 0) {
			throw new USBException("invalid device handle");
		}
		if (data == null) {
			throw new USBException("data must not be null");
		}
		if (length <= 0) {
			throw new USBException("size must be > 0");
		}
		int lenWritten = LibusbWin.usb_interrupt_write(usbDevHandle,
				out_ep_address, data, length, timeout);
		if (lenWritten < 0) {
			if (lenWritten == TIMEOUT_ERROR_CODE) {
				// try to reopen the device and send the data again
				if (reopenOnTimeout) {
					logger.info("try to reopen");
					reset();
					open(dev_configuration, dev_interface, dev_altinterface);
					return writeInterrupt(out_ep_address, data, length,
							timeout, false);
				}
				throw new USBTimeoutException("LibusbWin.usb_bulk_write: "
						+ LibusbWin.usb_strerror());
			}
			throw new USBException("LibusbWin.usb_bulk_write: "
					+ LibusbWin.usb_strerror());
		}

		logger.info("length written: " + lenWritten);
		if (logger.isLoggable(Level.FINEST)) {
			StringBuffer sb = new StringBuffer("bulkwrite, ep 0x"
					+ Integer.toHexString(out_ep_address) + ": " + lenWritten
					+ " Bytes sent: ");
			for (int i = 0; i < lenWritten; i++) {
				sb.append("0x" + String.format("%1$02X", data[i]) + " ");
			}
			logger.info(sb.toString());
		}
		return lenWritten;
	}

	/**
	 * Read data from the device using a interrupt transfer.<br>
	 * 
	 * @param in_ep_address
	 *            endpoint address to read from
	 * @param data
	 *            data buffer for the data to be read
	 * @param size
	 *            the maximum requested data size
	 * @param timeout
	 *            amount of time in ms the device will try to receive data until
	 *            a timeout exception is thrown
	 * @param reopenOnTimeout
	 *            if set to true, the device will try to open the connection and
	 *            receive the data again before a timeout exception is thrown
	 * @return the actual number of bytes read
	 * @throws USBException
	 */
	public int readInterrupt(int in_ep_address, byte[] data, int size,
			int timeout, boolean reopenOnTimeout) throws USBException {
		if (usbDevHandle <= 0) {
			throw new USBException("invalid device handle");
		}
		if (data == null) {
			throw new USBException("data must not be null");
		}
		if (size <= 0) {
			throw new USBException("size must be > 0");
		}
		int lenRead = LibusbWin.usb_interrupt_read(usbDevHandle, in_ep_address,
				data, size, timeout);
		if (lenRead < 0) {
			if (lenRead == TIMEOUT_ERROR_CODE) {
				// try to reopen the device and send the data again
				if (reopenOnTimeout) {
					logger.info("try to reopen");
					reset();
					open(dev_configuration, dev_interface, dev_altinterface);
					return readInterrupt(in_ep_address, data, size, timeout,
							false);
				}
				throw new USBTimeoutException("LibusbWin.usb_bulk_read: "
						+ LibusbWin.usb_strerror());
			}
			throw new USBException("LibusbWin.usb_bulk_read: "
					+ LibusbWin.usb_strerror());
		}

		logger.info("length read: " + lenRead);
		if (logger.isLoggable(Level.FINEST)) {
			StringBuffer sb = new StringBuffer("bulkread, ep 0x"
					+ Integer.toHexString(in_ep_address) + ": " + lenRead
					+ " Bytes received: ");
			for (int i = 0; i < lenRead; i++) {
				sb.append("0x" + String.format("%1$02X", data[i]) + " ");
			}
			logger.info(sb.toString());
		}
		return lenRead;
	}

	/**
	 * Claim an interface to send and receive USB data.<br>
	 * 
	 * @param usb_dev_handle
	 *            the handle of the device <b>(MUST BE VALID)</b>
	 * @param configuration
	 *            the configuration to use
	 * @param interface_
	 *            the interface to claim
	 * @param altinterface
	 *            the alternate interface to use. If no alternate interface must
	 *            be set <i>-1</i> can be used.
	 * @throws USBException
	 *             throws an USBException if the action fails
	 */
	private void claim_interface(int usb_dev_handle, int configuration,
			int interface_, int altinterface) throws USBException {
		if (LibusbWin.usb_set_configuration(usb_dev_handle, configuration) < 0) {
			throw new USBException("LibusbWin.usb_set_configuration: "
					+ LibusbWin.usb_strerror());
		}
		if (LibusbWin.usb_claim_interface(usb_dev_handle, interface_) < 0) {
			throw new USBException("LibusbWin.usb_claim_interface: "
					+ LibusbWin.usb_strerror());
		}
		if (altinterface >= 0) {
			if (LibusbWin.usb_set_altinterface(usb_dev_handle, altinterface) < 0) {
				throw new USBException("LibusbWin.usb_set_altinterface: "
						+ LibusbWin.usb_strerror());
			}
		}
		logger.info("interface claimed");
	}

	/**
	 * Release a previously claimed interface.<br>
	 * 
	 * @param dev_handle
	 *            the handle of the device <b>(MUST BE VALID)</b>
	 * @param interface_
	 *            the interface to claim
	 * @throws USBException
	 *             throws an USBException if the action fails
	 */
	private void release_interface(int dev_handle, int interface_)
			throws USBException {
		if (LibusbWin.usb_release_interface(dev_handle, interface_) < 0) {
			usbDevHandle = 0;
			throw new USBException("LibusbWin.usb_release_interface: "
					+ LibusbWin.usb_strerror());
		}
		logger.info("interface released");
	}

	/**
	 * Returns the product ID of the device.<br>
	 * 
	 * @return the product ID of the device.
	 */
	public int getIdProduct() {
		return idProduct;
	}

	/**
	 * Returns the vendor ID of the device.<br>
	 * 
	 * @return the vendor ID of the device.
	 */
	public int getIdVendor() {
		return idVendor;
	}

	/**
	 * Returns the alternative interface. This value is only valid after opening
	 * the device.<br>
	 * 
	 * @return the alternative interface. This value is only valid after opening
	 *         the device.
	 */
	public int getAltinterface() {
		return dev_altinterface;
	}

	/**
	 * Returns the current configuration used. This value is only valid after
	 * opening the device.<br>
	 * 
	 * @return the current configuration used. This value is only valid after
	 *         opening the device.
	 */
	public int getConfiguration() {
		return dev_configuration;
	}

	/**
	 * Returns the current interface. This value is only valid after opening the
	 * device.<br>
	 * 
	 * @return the current interface. This value is only valid after opening the
	 *         device.
	 */
	public int getInterface() {
		return dev_interface;
	}

	/**
	 * Returns the current device handle. This value is only valid after opening
	 * the device.<br>
	 * 
	 * @return the current device handle. This value is only valid after opening
	 *         the device.
	 */
	public int getUsbDevHandle() {
		return usbDevHandle;
	}

	/**
	 * Returns the maximum packet size in bytes which is allowed to be
	 * transmitted at once.<br>
	 * The value is determined by reading the endpoint descriptor(s) when
	 * opening the device. It is invalid before the device is opened! Note that
	 * if some endpoints use different packet sizes the maximum packet size is
	 * return. This value may be used to determine if a device is opened in
	 * fullspeed or highspeed mode.
	 * 
	 * @return the maximum packet size
	 */
	public int getMaxPacketSize() {
		return maxPacketSize;
	}

	/**
	 * Check if the device is open.<br>
	 * This checks only for a valid device handle. It doesn't check if the
	 * device is still attached or working.
	 * 
	 * @return true if the device is open
	 */
	public boolean isOpen() {
		return usbDevHandle > 0;
	}

	/**
	 * If enabled, the device is reset when first opened. <br>
	 * This will only happen once. When the application is started, the device
	 * state is unknown. If the device is not reset, read or write may result in
	 * a {@link USBTimeoutException}.<br>
	 * <br>
	 * This feature is disabled by default.
	 * 
	 * @param enable
	 *            true if the device should be reset when first opened
	 * @param timeout
	 *            the timeout between the reset and the reopening
	 */
	public void setResetOnFirstOpen(boolean enable, int timeout) {
		resetOnFirstOpen = enable;
		resetTimeout = timeout;
	}
}
