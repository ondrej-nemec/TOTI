package toti.logging;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ji.common.functions.InputStreamLoader;

public class TotiLogger extends Logger implements ji.common.Logger {

	public static String CONF_FILE = "conf/log4j.properties";	
	
	private static TotiLoggerFactory totiLoggerFactory;
	
	public static TotiLogger getLogger(String name) {
		if (totiLoggerFactory == null) {
			try {
				PropertyConfigurator.configure(InputStreamLoader.createInputStream(TotiLogger.class, CONF_FILE));
			} catch (IOException e) {
				e.printStackTrace();
			}
			totiLoggerFactory = new TotiLoggerFactory();
		}
		return (TotiLogger)Logger.getLogger(name, totiLoggerFactory);
	}
	
	protected TotiLogger(String name) {
		super(name);
	}

}
