package ji.common;

import toti.common.tests.Log4j2LoggerTestImpl;

public class LoggerImplE2E {

	public static void main(String[] args) {
		Log4j2LoggerTestImpl log = new Log4j2LoggerTestImpl("impl");
		log.info("Info message");
		log.error("Error message", new Exception("second wrapper", new Exception("first wrapper", new Exception("root cause"))));
	}
	
}
