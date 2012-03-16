/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.inf.libusbJava.demo;

import ch.ntb.inf.libusbJava.LibusbJava;
import ch.ntb.inf.libusbJava.Usb_Bus;
import ch.ntb.inf.libusbJava.Utils;

/**
 * Initalises Libusb and prints the bus(ses) with attached devices to the
 * standard out.<br>
 * 
 * 
 */
public class LogBus {

	private static void logBus() {
		// if you don't use the ch.ntb.inf.libusbJava.Device class you must initialise
		// Libusb before use
		LibusbJava.usb_init();
		LibusbJava.usb_find_busses();
		LibusbJava.usb_find_devices();

		// retrieve a object tree representing the bus with its devices and
		// descriptors
		Usb_Bus bus = LibusbJava.usb_get_busses();

		// log the bus structure to standard out
		Utils.logBus(bus);
	}

	public static void main(String[] args) {
		logBus();
	}
}
