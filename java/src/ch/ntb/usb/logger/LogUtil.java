package ch.ntb.usb.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

public class LogUtil {

	public static UsbLogger ch_ntb_usb, ch_ntb_usb_test;

	static {
		// set all loglevels here
		ch_ntb_usb = getLogger("ch.ntb.usb", LogLevel.OFF);
		ch_ntb_usb_test = getLogger("ch.ntb.usb.test", LogLevel.ALL);
	}

	public static void setLevel(UsbLogger logger, Level loglevel) {
		Handler[] h = logger.getHandlers();
		for (int i = 0; i < h.length; i++) {
			h[i].setLevel(loglevel);
		}
		logger.setLevel(loglevel);
	}

	private static void initLevel(UsbLogger logger, Level loglevel) {
		Handler[] h = logger.getHandlers();
		for (int i = 0; i < h.length; i++) {
			logger.removeHandler(h[i]);
		}
		Handler console = new ConsoleHandler();
		console.setLevel(loglevel);
		logger.addHandler(console);
		logger.setLevel(loglevel);
		logger.setUseParentHandlers(false);
	}

	private static UsbLogger getLogger(String name, Level loglevel) {
		UsbLogger logger = UsbLogger.getLogger(name);
		initLevel(logger, loglevel);
		return logger;
	}
}
