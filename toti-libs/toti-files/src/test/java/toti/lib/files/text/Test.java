package toti.lib.files.text;

import java.io.IOException;

public class Test {

	@SuppressWarnings("CallToPrintStackTrace")
	public static void main(String[] args) {
		
		try {
			Text.get().read((br)->{
				return "";
			}, "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
