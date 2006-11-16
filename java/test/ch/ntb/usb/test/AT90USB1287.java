package ch.ntb.usb.test;

import ch.ntb.usb.USB;

public class AT90USB1287 extends AbstractDeviceInfo {

	@Override
	public void initValues() {
		setIdVendor((short) 0x8235);
		setIdProduct((short) 0x0222);
		setTimeout(2000);
		setConfiguration(1);
		setInterface(0);
		setAltinterface(-1);
		setOUT_EP_BULK(0x01);
		setIN_EP_BULK(0x82);
		setOUT_EP_INT(0x03);
		setIN_EP_INT(0x84);
		setSleepTimeout(2000);
		setMaxDataSize(USB.FULLSPEED_MAX_BULK_PACKET_SIZE);
		setMode(WriteMode.Bulk);
	}
}
