package ch.ntb.usb.logger;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class UsbLogger extends Logger {

	protected UsbLogger(String name, String resourceBundleName) {
		super(name, resourceBundleName);
	}

	public void debug(String msg) {
		log(LogLevel.DEBUG, msg);
	}

	public static synchronized UsbLogger getLogger(String name) {
		LogManager manager = LogManager.getLogManager();
		UsbLogger result = (UsbLogger) manager.getLogger(name);
		if (result == null) {
			result = new UsbLogger(name, null);
			manager.addLogger(result);
			result = (UsbLogger) manager.getLogger(name);
		}
		result.setLevel(null);
		return result;
	}
}
