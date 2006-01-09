package ch.ntb.usb;

import java.util.Iterator;
import java.util.LinkedList;

import ch.ntb.usb.logger.LogUtil;
import ch.ntb.usb.logger.UsbLogger;

public class USB {

	/**
	 * The maximal data size in bytes which is allowed to be transmitted at
	 * once.
	 */
	public static final int MAX_DATA_SIZE = 512;

	private static UsbLogger logger = LogUtil.ch_ntb_usb;

	private static LinkedList<Device> devices = new LinkedList<Device>();

	/**
	 * Create a new device an register it in a device queue or get an already
	 * created device.
	 * 
	 * @param idVendor
	 *            the vendor id of the USB device
	 * @param idProduct
	 *            the product id of the USB device
	 * @return a newly created device or an already registered device
	 */
	public static Device getDevice(short idVendor, short idProduct) {

		// check if this device is already registered
		Device dev = getRegisteredDevice(idVendor, idProduct);
		if (dev != null) {
			return dev;
		}
		dev = new Device(idVendor, idProduct);
		devices.add(dev);
		return dev;
	}

	/**
	 * Get an already registered device or null if the device does not exist.
	 * 
	 * @param idVendor
	 *            the vendor id of the USB device
	 * @param idProduct
	 *            the product id of the USB device
	 * @return the device or null
	 */
	private static Device getRegisteredDevice(short idVendor, short idProduct) {
		for (Iterator<Device> iter = devices.iterator(); iter.hasNext();) {
			Device dev = iter.next();
			if ((dev.getIdVendor() == idVendor)
					&& (dev.getIdProduct() == idProduct)) {
				return dev;
			}
		}
		return null;
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
	static void claim_interface(int usb_dev_handle, int configuration,
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
	static void release_interface(int dev_handle, int interface_)
			throws USBException {
		if (LibusbWin.usb_release_interface(dev_handle, interface_) < 0) {
			throw new USBException("LibusbWin.usb_release_interface: "
					+ LibusbWin.usb_strerror());
		}
		logger.info("interface released");
	}
}
