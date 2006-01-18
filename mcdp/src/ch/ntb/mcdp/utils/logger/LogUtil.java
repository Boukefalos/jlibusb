package ch.ntb.mcdp.utils.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

public class LogUtil {

	public static McdpLogger ch_ntb_mcdp_bdi, ch_ntb_mcdp_bdi_test,
			ch_ntb_mcdp_mc68332, ch_ntb_mcdp_usb;

	static {
		// set all loglevels here
		ch_ntb_mcdp_usb = getLogger("ch.ntb.mcdp.usb", LogLevel.OFF);

		ch_ntb_mcdp_bdi = getLogger("ch.ntb.mcdp.bdi", LogLevel.OFF);

		ch_ntb_mcdp_bdi_test = getLogger("ch.ntb.mcdp.bdi.test", LogLevel.ALL);

		ch_ntb_mcdp_mc68332 = getLogger("ch.ntb.mcdp.mc68332", LogLevel.OFF);
	}

	public static void setLevel(McdpLogger logger, Level loglevel) {
		Handler[] h = logger.getHandlers();
		for (int i = 0; i < h.length; i++) {
			h[i].setLevel(loglevel);
		}
		logger.setLevel(loglevel);
	}

	private static void initLevel(McdpLogger logger, Level loglevel) {
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

	private static McdpLogger getLogger(String name, Level loglevel) {
		McdpLogger logger = McdpLogger.getLogger(name);
		initLevel(logger, loglevel);
		return logger;
	}
}
