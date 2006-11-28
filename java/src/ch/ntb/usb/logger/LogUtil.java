/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <inf.ntb.ch>
 *
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.logger;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogUtil {

	private static final String PLUGIN_ID = "ch.ntb.usb";
	private static final String PROPERTIES_FILE = ".configure";
	private static final String LOGGER_WARNING = "Warning in class "
			+ LogUtil.class.getName()
			+ ": could not load the logger properties file " + PROPERTIES_FILE;

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
			InputStream is = LogUtil.class.getClassLoader()
					.getResourceAsStream(PROPERTIES_FILE);
			if (is == null) {
				System.err.println(LOGGER_WARNING);
			} else {
				Properties prop = new Properties();
				prop.load(is);
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
