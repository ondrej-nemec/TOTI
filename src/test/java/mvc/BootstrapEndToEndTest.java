package mvc;

import java.util.Arrays;

import logging.LoggerFactory;
import translator.DefaultTranslator;

public class BootstrapEndToEndTest {

	public static void main(String[] args) {
		try {
			Bootstrap b = new Bootstrap(
					80, 10, 60000, 600000,
					"temp", "templates/", new String[]{"controllers"}, "www",
					Arrays.asList(
							"Access-Control-Allow-Origin: *"
					), "utf-8",
					new DefaultTranslator(LoggerFactory.getLogger("translator"), "", "messages"),
					LoggerFactory.getLogger("server")
			);
			b.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
