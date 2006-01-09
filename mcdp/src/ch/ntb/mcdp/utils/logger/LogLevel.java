package ch.ntb.mcdp.utils.logger;

import java.util.logging.Level;

public class LogLevel extends Level {

	public static final Level DEBUG = new LogLevel("DEBUG", 750);

	protected LogLevel(String name, int value) {
		super(name, value);
	}

}
