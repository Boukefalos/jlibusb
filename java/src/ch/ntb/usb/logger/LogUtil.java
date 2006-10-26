package ch.ntb.usb.logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogUtil {

	private static final String PLUGIN_ID = "ch.ntb.usb";
	private static final String PROPERTIES_FILE = ".configure";

	private static boolean debugEnabled;

	static {
		createLoggersFromProperties();
	}

	public static void setLevel(Logger logger, Level loglevel) {
		Handler[] h = logger.getHandlers();
		for (int i = 0; i < h.length; i++) {
			h[i].setLevel(loglevel);
		}
		logger.setLevel(loglevel);
	}

	public static Logger getLogger(String name) {
		LogManager manager = LogManager.getLogManager();
		// check if logger is already registered
		Logger logger = manager.getLogger(name);
		if (logger == null) {
			logger = Logger.getLogger(name);
			initLevel(logger, Level.OFF);
			manager.addLogger(logger);
		}
		logger.setLevel(null);
		return logger;
	}

	private static void initLevel(Logger logger, Level loglevel) {
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

	private static void createLoggersFromProperties() {
		try {
			URL url = ClassLoader.getSystemResource(PROPERTIES_FILE);
			if (url == null)
				throw new FileNotFoundException(PROPERTIES_FILE
						+ " not found -> file must be on classpath");
			String filename = URLDecoder.decode(url.getPath(), "UTF-8");
			FileInputStream fis = new FileInputStream(filename);
			Properties prop = new Properties();
			prop.load(fis);
			// get global debug enable flag
			debugEnabled = Boolean.parseBoolean(prop.getProperty(PLUGIN_ID
					+ "/debug"));
			// get and configure loggers
			boolean moreLoggers = true;
			int loggerCount = 0;
			while (moreLoggers) {
				String loggerProp = prop.getProperty(PLUGIN_ID
						+ "/debug/logger" + loggerCount);
				loggerCount++;
				if (loggerProp != null) {
					// parse string and get logger name and log level
					int slashIndex = loggerProp.indexOf('/');
					String loggerName = loggerProp.substring(0, slashIndex)
							.trim();
					String logLevel = loggerProp.substring(slashIndex + 1,
							loggerProp.length());
					// register logger
					Level level;
					if (debugEnabled) {
						level = Level.parse(logLevel);
					} else {
						level = Level.OFF;
					}
					Logger logger = getLogger(loggerName);
					initLevel(logger, level);
				} else {
					moreLoggers = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
