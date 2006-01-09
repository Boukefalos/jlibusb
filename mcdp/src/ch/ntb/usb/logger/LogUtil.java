package ch.ntb.usb.logger;

public class LogUtil {
	
	public static UsbLogger ch_ntb_usb;

	static {
		// set all loglevels here
		ch_ntb_usb = UsbLogger.getLogger("ch.ntb.usb");
		ch_ntb_usb.setLevel(LogLevel.OFF);
	}
}
