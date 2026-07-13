package test;

import toti.application.annotations.Action;
import toti.application.answers.action.ResponseAction;
import toti.lib.tcpip.enums.HttpMethod;

public class Test {
	
	/*public static void main(String[] args) {
		Register r = null;
		r.addFactory(String.class, ()->{
			return "";
		});
	}*/

	@Action(path = "index", methods = HttpMethod.GET)
	public ResponseAction example(Integer id) {
		// TODO
		/* 
			nastavit typ prijimaneho body
				- strukturovana
					- json, xml => parsovat
					- urlencoded/formdata - asi jako jedno
				- plain
		*/
		return (request, iden)->{
			return null; // response
		}
		;
	}
	
}
