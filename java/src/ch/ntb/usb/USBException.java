/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <libusb@drip.ch>
 *
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

import java.io.IOException;

public class USBException extends IOException {

	public USBException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1690857437804284710L;

}
