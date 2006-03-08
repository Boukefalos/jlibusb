package ch.ntb.usb;

import java.util.logging.Level;

import ch.ntb.usb.logger.LogUtil;
import ch.ntb.usb.logger.UsbLogger;

/**
 * This class represents an USB device.<br>
 * To get an instance of an USB device use <code>USB.getDevice(...)</code>.
 * 
 * @author schlaepfer
 * 
 */
public class Device {

	private static UsbLogger logger = LogUtil.ch_ntb_usb;

	private static final int TIMEOUT_ERROR_CODE = -116;

	private int maxPacketSize;

	private int idVendor, idProduct, configuration, interface_, altinterface;

	private int usb_dev_handle;

	protected Device(short idVendor, short idProduct) {
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
	 *            the desired configuration
	 * @param interface_
	 *            the desired interface
	 * @param altinterface
	 *            the desired alternative interface
	 * @throws USBException
	 */
	public void open(int configuration, int interface_, int altinterface)
			throws USBException {
		this.configuration = configuration;
		this.interface_ = interface_;
		this.altinterface = altinterface;

		Usb_Bus bus;

		if (usb_dev_handle > 0) {
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
				// Usb_Device_Descriptor
				Usb_Device_Descriptor defDesc = dev.descriptor;
				if ((defDesc.idVendor == idVendor)
						&& (defDesc.idProduct == idProduct)) {
					logger.info("Open device: " + dev.filename);
					int res = LibusbWin.usb_open(dev);
					if (res <= 0) {
						throw new USBException("LibusbWin.usb_open: "
								+ LibusbWin.usb_strerror());
					} else {
						usb_dev_handle = res;
						// get endpoint wMaxPacketSize
						Usb_Config_Descriptor[] confDesc = dev.config;
						for (int i = 0; i < confDesc.length; i++) {
							Usb_Interface[] int_ = confDesc[i].interface_;
							for (int j = 0; j < int_.length; j++) {
								Usb_Interface_Descriptor[] intDesc = int_[j].altsetting;
								for (int k = 0; k < intDesc.length; k++) {
									Usb_Endpoint_Descriptor[] epDesc = intDesc[k].endpoint;
									for (int l = 0; l < epDesc.length; l++) {
										maxPacketSize = Math.max(epDesc[l].wMaxPacketSize,
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
				}
				dev = dev.next;
			}
			bus = bus.next;
		}
		if (usb_dev_handle <= 0) {
			throw new USBException("USB device with idVendor 0x"
					+ Integer.toHexString(idVendor & 0xFFFF)
					+ " and idProduct 0x"
					+ Integer.toHexString(idProduct & 0xFFFF) + " not found");
		}
		claim_interface(usb_dev_handle, configuration, interface_, altinterface);
	}

	public void close() throws USBException {
		if (usb_dev_handle <= 0) {
			throw new USBException("invalid device handle");
		}
		release_interface(usb_dev_handle, interface_);
		if (LibusbWin.usb_close(usb_dev_handle) < 0) {
			usb_dev_handle = 0;
			throw new USBException("LibusbWin.usb_close: "
					+ LibusbWin.usb_strerror());
		}
		usb_dev_handle = 0;
		maxPacketSize = -1;
		logger.info("device closed");
	}

	/**
	 * Sends an USB reset to the device. The device handle will no longer be
	 * valid. To use the device again, <code>open</code> must be called.
	 * 
	 * @throws USBException
	 */
	public void reset() throws USBException {
		if (usb_dev_handle <= 0) {
			throw new USBException("invalid device handle");
		}
		if (LibusbWin.usb_reset(usb_dev_handle) < 0) {
			throw new USBException("LibusbWin.usb_reset: "
					+ LibusbWin.usb_strerror());
		}
		usb_dev_handle = 0;
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
	 * @return the actual number of bytes written
	 * @throws USBException
	 */
	public int bulkwrite(int out_ep_address, byte[] data, int length,
			int timeout) throws USBException {
		if (usb_dev_handle <= 0) {
			throw new USBException("invalid device handle");
		}
		if (data == null) {
			throw new USBException("data must not be null");
		}
		if (length <= 0) {
			throw new USBException("size must be > 0");
		}
		int lenWritten = LibusbWin.usb_bulk_write(usb_dev_handle,
				out_ep_address, data, length, timeout);
		if (lenWritten < 0) {
			if (lenWritten == TIMEOUT_ERROR_CODE) {
				throw new USBTimeoutException("LibusbWin.usb_bulk_write: "
						+ LibusbWin.usb_strerror());
			}
			throw new USBException("LibusbWin.usb_bulk_write: "
					+ LibusbWin.usb_strerror());
		}

		if (logger.getLevel().intValue() <= Level.INFO.intValue()) {
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
	 * @param in_ep_address
	 *            endpoint address to read from
	 * @param data
	 *            data buffer for the data to be read
	 * @param size
	 *            the maximum requested data size
	 * @param timeout
	 *            amount of time in ms the device will try to receive data until
	 *            a timeout exception is thrown
	 * @return the actual number of bytes read
	 * @throws USBException
	 */
	public int bulkread(int in_ep_address, byte[] data, int size, int timeout)
			throws USBException {
		if (usb_dev_handle <= 0) {
			throw new USBException("invalid device handle");
		}
		if (data == null) {
			throw new USBException("data must not be null");
		}
		if (size <= 0) {
			throw new USBException("size must be > 0");
		}
		int lenRead = LibusbWin.usb_bulk_read(usb_dev_handle, in_ep_address,
				data, size, timeout);
		if (lenRead < 0) {
			if (lenRead == TIMEOUT_ERROR_CODE) {
				throw new USBTimeoutException("LibusbWin.usb_bulk_read: "
						+ LibusbWin.usb_strerror());
			}
			throw new USBException("LibusbWin.usb_bulk_read: "
					+ LibusbWin.usb_strerror());
		}

		if (logger.getLevel().intValue() <= Level.INFO.intValue()) {
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
	 * Claim an interface to send and receive USB data.
	 * 
	 * @param usb_dev_handle
	 *            the handle of the device <b>(MUST BE VALID)</b>
	 * @param configuration
	 *            the configuration to use
	 * @param interface_
	 *            the interface to claim
	 * @param altinterface
	 *            the alternative interface to use
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
		if (LibusbWin.usb_set_altinterface(usb_dev_handle, altinterface) < 0) {
			throw new USBException("LibusbWin.usb_set_altinterface: "
					+ LibusbWin.usb_strerror());
		}
		logger.info("interface claimed");
	}

	/**
	 * Release a previously claimed interface.
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
			throw new USBException("LibusbWin.usb_release_interface: "
					+ LibusbWin.usb_strerror());
		}
		logger.info("interface released");
	}

	/**
	 * @return the product ID of the device.
	 */
	public int getIdProduct() {
		return idProduct;
	}

	/**
	 * @return the vendor ID of the device.
	 */
	public int getIdVendor() {
		return idVendor;
	}

	/**
	 * @return the alternative interface. This value is only valid after opening
	 *         the device.
	 */
	public int getAltinterface() {
		return altinterface;
	}

	/**
	 * @return the current configuration used. This value is only valid after
	 *         opening the device.
	 */
	public int getConfiguration() {
		return configuration;
	}

	/**
	 * @return the current interface. This value is only valid after opening the
	 *         device.
	 */
	public int getInterface() {
		return interface_;
	}

	/**
	 * @return the current device handle. This value is only valid after opening
	 *         the device.
	 */
	public int getUsb_dev_handle() {
		return usb_dev_handle;
	}

	/**
	 * Returns the maximum packet size in bytes which is allowed to be
	 * transmitted at once.<br>
	 * The value is determined by reading the endpoint descriptor(s) when
	 * opening the device. It is invalid before the device is opened!
	 * 
	 * @return the maximum packet size
	 */
	public int getMaxPacketSize() {
		return maxPacketSize;
	}

}
