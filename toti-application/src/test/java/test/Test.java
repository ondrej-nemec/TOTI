package test;

import ji.socketCommunication.http.HttpMethod;
import toti.annotations.Action;
import toti.annotations.Secured;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.application.register.Register;

public class Test {
	
	public static void main(String[] args) {
		Register r = null;
		r.addFactory(String.class, ()->{
			return "";
		});
	}

	@Action(path = "index", methods = HttpMethod.GET)
	@Secured
	public ResponseAction example(Integer id) {
		// TODO
		/* 
			nastavit typ prijimaneho body
				- strukturovana
					- json, xml => parsovat
					- urlencoded/formdata - asi jako jedno
				- plain
		*/
		return ResponseBuilder.get()
		// auther is automatic - secured header?
		.prevalidate((request, trans, iden)->{
			// neco hazet? vracet?
			// napr: true, false, specialni pripady exception?
		})
		// .authorize("some-domain", toti.security.Action.CREATE) // zustane zachovano pro jednodussi pouziti
		.authorize((request, trans, iden)->{
			// neco hazet? vracet?
			// napr: true, false, specialni pripady exception?
		})
		.validate((request, trans, iden)->{
			// validace path
			// validace query
			// validace body (vcetne struktury: xml, json, form-data, urleconded, plaintext)
		})
		.createRequest((request, trans, iden)->{
			return null; // response
		})
		;
	}
	
}
