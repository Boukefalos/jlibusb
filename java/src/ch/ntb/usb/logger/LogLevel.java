package ch.ntb.usb.logger;

import java.util.logging.Level;

public class LogLevel extends Level {

	private static final long serialVersionUID = -8918592094095458645L;

	public static final Level DEBUG = new LogLevel("DEBUG", 750);

	protected LogLevel(String name, int value) {
		super(name, value);
	}

}
