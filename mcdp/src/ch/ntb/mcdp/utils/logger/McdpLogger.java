package ch.ntb.mcdp.utils.logger;

import java.util.logging.Logger;
import java.util.logging.LogManager;

public class McdpLogger extends Logger {

	protected McdpLogger(String name, String resourceBundleName) {
		super(name, resourceBundleName);
	}

	public void debug(String msg) {
		log(LogLevel.DEBUG, msg);
	}

	public static synchronized McdpLogger getLogger(String name) {
		LogManager manager = LogManager.getLogManager();
		McdpLogger result = (McdpLogger) manager.getLogger(name);
		if (result == null) {
			result = new McdpLogger(name, null);
			manager.addLogger(result);
			result = (McdpLogger) manager.getLogger(name);
		}
		return result;
	}
}
