/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.inf.libusbJava;

/**
 * Represents an USB bus.<br>
 * This is the root class for the representation of the libusb USB structure.
 * Zero or more devices may be connected to an USB bus.
 * 
 */
public class Usb_Bus {

	private Usb_Bus next, prev;

	private String dirname;

	private Usb_Device devices;

	private long location;

	private Usb_Device root_dev;

	public Usb_Bus(String dirname, long location) {
		this.dirname = dirname;
		this.location = location;
	}

	/**
	 * Get the first device ojects of the devices linked list.<br>
	 * 
	 * @return the first device ojects of the devices linked list or null
	 */
	public Usb_Device getDevices() {
		return devices;
	}

	protected void setDevices(Usb_Device devices) {
		this.devices = devices;
	}

	/**
	 * Returns the systems String representation of the bus.<br>
	 * 
	 * @return the systems String representation of the bus
	 */
	public String getDirname() {
		return dirname;
	}

	/**
	 * Returns the next bus object.<br>
	 * 
	 * @return Returns the next bus object or null
	 */
	public Usb_Bus getNext() {
		return next;
	}

	protected void setNext(Usb_Bus bus) {
		next = bus;
	}

	/**
	 * Returns the previous bus object.<br>
	 * 
	 * @return Returns the previous bus object or null
	 */
	public Usb_Bus getPrev() {
		return prev;
	}

	protected void setPrev(Usb_Bus bus) {
		prev = bus;
	}

	/**
	 * Get the root device of this bus.<br>
	 * 
	 * @return the root device oject or null
	 */
	public Usb_Device getRootDev() {
		return root_dev;
	}

	/**
	 * Returns the location in the USB bus linked list.<br>
	 * 
	 * @return the location in the USB bus linked list
	 */
	public long getLocation() {
		return location;
	}

	protected void setLocation(long busnumber) {
		location = busnumber;
	}

	@Override
	public String toString() {
		return "Usb_Bus " + dirname;
	}
}