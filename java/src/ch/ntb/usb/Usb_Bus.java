/* 
 * Java LibUsb-Win32 wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <libusb@drip.ch>
 *
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

/**
 * Represents an USB bus.<br>
 * This is the root class for the representation of the libusb USB structure.
 * Zero or more devices may be connected to an USB bus.
 * 
 * @author schlaepfer
 * 
 */
public class Usb_Bus {

	/**
	 * The next and previous bus object
	 */
	public Usb_Bus next, prev;

	/**
	 * Systems String representation of the bus
	 */
	public String dirname;

	/**
	 * Device objects attached to this bus
	 */
	public Usb_Device devices;

	/**
	 * Location in the USB bus linked list
	 */
	public long location;

	public Usb_Device root_dev;

	@Override
	public String toString() {
		return "Usb_Bus " + dirname;
	}
}