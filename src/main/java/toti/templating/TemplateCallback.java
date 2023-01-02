package toti.templating;

import ji.translator.Translator;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.url.MappedUrl;

public interface TemplateCallback {

	void call(
		TemplateParameters parameters, 
		Identity identty, Translator translator,
		Authorizator authorizator, MappedUrl current
	) throws Exception;
	
}
