/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.inf.libusbJava.test.devices;

import ch.ntb.inf.libusbJava.USB;
import ch.ntb.inf.libusbJava.testApp.AbstractDeviceInfo;

public class AT90USB1287 extends AbstractDeviceInfo {

	@Override
	public void initValues() {
		setIdVendor((short) 0x8235);
		setIdProduct((short) 0x0200);
		setTimeout(2000);
		setConfiguration(1);
		setInterface(0);
		setAltinterface(-1);
		setOutEPBulk(0x01);
		setInEPBulk(0x82);
		setOutEPInt(0x01);
		setInEPInt(0x82);
		setSleepTimeout(2000);
		setMaxDataSize(USB.FULLSPEED_MAX_BULK_PACKET_SIZE);
		setMode(TransferMode.Bulk);
		setManufacturer("inf.ntb.ch");
		setProduct("JUnit Test Board");
		setSerialVersion("00.10.00");
	}
}
