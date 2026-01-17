package toti.lib.files.text;

import java.io.IOException;

import toti.lib.files.text.Text;

public class Test {

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
