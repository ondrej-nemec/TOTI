package toti.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import ji.common.Log4j2LoggerTestImpl;

public class TotiLoggerFactory implements Function<String, Logger> {
	
	private final static TotiLoggerFactory instance = new TotiLoggerFactory();
	
	public static TotiLoggerFactory get() {
		return instance;
	}

	private final Map<String, Logger> loggers = new HashMap<>();

	@Override
	public Logger apply(String name) {
		if (!loggers.containsKey(name)) {
			loggers.put(name, new Log4j2LoggerTestImpl(name));
		}
		return loggers.get(name);
	}
	
}
