package mvc;

import java.util.Arrays;

import logging.LoggerFactory;

public class BootstrapEndToEndTest {

	public static void main(String[] args) {
		try {
			Bootstrap b = new Bootstrap(
					80, 10, 60000, 600000,
					"temp", "jsp", "www",
					Arrays.asList(
							"Access-Control-Allow-Origin: *"
					), "utf-8",
					LoggerFactory.getLogger("server")
			);
			b.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
