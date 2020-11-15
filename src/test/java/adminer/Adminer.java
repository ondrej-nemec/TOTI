package adminer;

import java.util.Properties;

import core.text.Text;
import toti.annotations.url.Action;
import toti.annotations.url.Controller;
import toti.annotations.url.Params;
import toti.response.Response;

@Controller("adminer")
public class Adminer {
	
	@Action("")
	public Response adminer(@Params Properties prop) throws Exception {
		StringBuilder params = new StringBuilder();
		prop.forEach((key, value)->{
			params.append(String.format(" %s=%s", key, value));
		});
		Process pr = Runtime.getRuntime().exec("php www\\adminer.php" + params.toString());
		
		StringBuilder text = new StringBuilder();
		Text.read((br)->{
			int i;
			while((i = br.read()) != -1) {
				text.append((char)i);
			}
			return null;
		}, pr.getInputStream());
		Text.read((br)->{
			int i;
			while((i = br.read()) != -1) {
				System.err.println((char)i);
			}
			return null;
		}, pr.getErrorStream());
		pr.waitFor();
		System.err.println(pr.exitValue());
		
		
		return Response.getText(text.toString());
	}
	
}
