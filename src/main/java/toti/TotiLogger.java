package toti;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggerFactory;

import common.functions.InputStreamLoader;

public class TotiLogger extends Logger implements common.Logger {

	public static String CONF_FILE = "conf/log4j.properties";	
	
	private static LoggerFactory myFactory;
	
	public static TotiLogger getLogger(String name) {
		if (myFactory == null) {
			try {
				PropertyConfigurator.configure(InputStreamLoader.createInputStream(TotiLogger.class, CONF_FILE));
			} catch (IOException e) {
				e.printStackTrace();
			}
			myFactory = new LoggerFactory() {
				@Override public Logger makeNewLoggerInstance(String name) {
					return new TotiLogger(name);
				}
			};
		}
		return (TotiLogger)Logger.getLogger(name, myFactory);
	}
	
	protected TotiLogger(String name) {
		super(name);
	}

}
