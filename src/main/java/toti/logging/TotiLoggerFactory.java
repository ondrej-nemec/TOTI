package toti.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

public class TotiLoggerFactory implements LoggerFactory {

	@Override
	public Logger makeNewLoggerInstance(String name) {
		return new TotiLogger(name);
	}

}
