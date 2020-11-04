package mvc.authentication;

import java.util.Properties;

public class Cookie {

	public static String getCookieValue(Properties header, String cookieName) {
		if (header.get("Cookie") != null) {
			String[] cookiesArray = header.get("Cookie").toString().split(";");
			for (String cookies : cookiesArray) {
				String[] cookie = cookies.split("=", 2);
				if (cookie.length == 2 && cookie[0].trim().equals(cookieName)) {
					return cookie[1].trim();
				}
			}
		}
		return null;
	}
	
}
