package mvc;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;

import controllers.TestController;
import logging.LoggerFactory;
import mvc.authentication.Authenticator;
import mvc.authentication.TokenType;
import mvc.registr.Registr;
import translator.DefaultTranslator;

public class BootstrapEndToEndTest {

	public static void main(String[] args) {
		try {
			Registr.addFactory(TestController.class, ()->{
				return new TestController();
			});

			Authenticator authenticator = new Authenticator(120000, TokenType.AUTHENTIZATION(), "secretSalt");		
			Router router = new Router();
			router.addUrl("/jsgrid", "/base/grid");
			router.addUrl("", "/index.html");
			
			Bootstrap b = new Bootstrap(
					80, 10, 60000, 3600000, // 1 h
					"temp", "templates/", new String[]{"controllers"}, "www",
					new ResponseHeaders(
						RandomStringUtils.randomAlphanumeric(50),
						Arrays.asList(
							"Access-Control-Allow-Origin: *"
						)
					),
					Optional.empty(),
					"utf-8",
					new DefaultTranslator(LoggerFactory.getLogger("translator"), "", "messages"),
					authenticator,
					router,
					LoggerFactory.getLogger("server"), false
			);
			b.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
