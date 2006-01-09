package ch.ntb.usb;

import java.util.logging.Level;

import ch.ntb.usb.logger.LogUtil;
import ch.ntb.usb.logger.UsbLogger;

public class Device {

	private static UsbLogger logger = LogUtil.ch_ntb_usb;

	private static final int TIMEOUT_ERROR_CODE = -116;

	public int MAX_DATA_SIZE = USB.MAX_DATA_SIZE;

	private int idVendor, idProduct, configuration, interface_, altinterface;

	private int usb_dev_handle;

	Device(short idVendor, short idProduct) {
		this.idVendor = idVendor;
		this.idProduct = idProduct;
	}

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
		USB.claim_interface(usb_dev_handle, configuration, interface_,
				altinterface);
	}

	public void close() throws USBException {
		if (usb_dev_handle <= 0) {
			throw new USBException("invalid device handle");
		}
		USB.release_interface(usb_dev_handle, interface_);
		if (LibusbWin.usb_close(usb_dev_handle) < 0) {
			usb_dev_handle = 0;
			throw new USBException("LibusbWin.usb_close: "
					+ LibusbWin.usb_strerror());
		}
		usb_dev_handle = 0;
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

	public int bulkwrite(int out_ep_address, byte[] data, int length, int timeout)
			throws USBException {
		if (usb_dev_handle <= 0) {
			throw new USBException("invalid device handle");
		}
		if (data == null) {
			throw new USBException("data must not be null");
		}
		if (length <= 0) {
			throw new USBException("size must be > 0");
		}
		int lenWritten = LibusbWin.usb_bulk_write(usb_dev_handle, out_ep_address,
				data, length, timeout);
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
		int lenRead = LibusbWin.usb_bulk_read(usb_dev_handle, in_ep_address, data,
				size, timeout);
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

	public int getIdProduct() {
		return idProduct;
	}

	public int getIdVendor() {
		return idVendor;
	}

	public int getAltinterface() {
		return altinterface;
	}

	public int getConfiguration() {
		return configuration;
	}

	public int getInterface() {
		return interface_;
	}

	public int getUsb_dev_handle() {
		return usb_dev_handle;
	}

}
