/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <libusb@drip.ch>
 *
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

/**
 * Represents an USB device.<br>
 * An USB device has one device descriptor and it may have multiple
 * configuration descriptors.
 * 
 * @author schlaepfer
 * 
 */
public class Usb_Device {

	/**
	 * Pointers to the next and previous device
	 */
	public Usb_Device next, prev;

	/**
	 * Systems String representation
	 */
	public String filename;

	/**
	 * Reference to the bus to which this device is connected
	 */
	public Usb_Bus bus;

	/**
	 * USB device descriptor
	 */
	public Usb_Device_Descriptor descriptor;

	/**
	 * USB config descriptors
	 */
	public Usb_Config_Descriptor[] config;

	/**
	 * Number assigned to this device
	 */
	public byte devnum;

	/**
	 * Number of children of this device
	 */
	public byte num_children;

	/**
	 * Reference to the first child
	 */
	public Usb_Device children;

	@Override
	public String toString() {
		return "Usb_Device " + filename;
	}

}