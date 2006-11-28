/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <libusb@drip.ch>
 *
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import ch.ntb.usb.logger.LogUtil;

/**
 * This class manages all USB devices and defines some USB specific constants.<br>
 * 
 * @author schlaepfer
 * 
 */
public class USB {

	/**
	 * The maximum packet size of a bulk transfer when operation in highspeed
	 * (480 MB/s) mode.
	 */
	public static int HIGHSPEED_MAX_BULK_PACKET_SIZE = 512;

	/**
	 * The maximum packet size of a bulk transfer when operation in fullspeed
	 * (12 MB/s) mode.
	 */
	public static int FULLSPEED_MAX_BULK_PACKET_SIZE = 64;

	private static final Logger logger = LogUtil.getLogger("ch.ntb.usb");

	private static LinkedList<Device> devices = new LinkedList<Device>();

	/**
	 * Create a new device an register it in a device queue. If the device is
	 * already registered, a reference to it will be returned.<br>
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
			logger.info("return already registered device");
			return dev;
		}
		dev = new Device(idVendor, idProduct);
		logger.info("create new device");
		devices.add(dev);
		return dev;
	}

	/**
	 * Get an already registered device or null if the device does not exist.<br>
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
}
