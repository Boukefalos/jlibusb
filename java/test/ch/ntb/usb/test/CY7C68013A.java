/* 
 * Java LibUsb-Win32 wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <libusb@drip.ch>
 *
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.test;

import ch.ntb.usb.USB;

public class CY7C68013A extends AbstractDeviceInfo {

	@Override
	public void initValues() {
		setIdVendor((short) 0x8235);
		setIdProduct((short) 0x0222);
		setTimeout(2000);
		setConfiguration(1);
		setInterface(0);
		setAltinterface(-1);
		setOUT_EP_INT(0x02);
		setIN_EP_INT(0x86);
		setOUT_EP_BULK(0x04);
		setIN_EP_BULK(0x88);
		setSleepTimeout(2000);
		setMaxDataSize(USB.HIGHSPEED_MAX_BULK_PACKET_SIZE);
		setMode(WriteMode.Bulk);
	}
}
