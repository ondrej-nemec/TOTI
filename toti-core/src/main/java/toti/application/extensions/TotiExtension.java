package toti.application.extensions;

import java.util.List;

import toti.application.answers.action.ResponseAction;

public interface TotiExtension extends Extension {
	
	List<String> getListeningUri();
	
	ResponseAction get(String uri, boolean isDevelop);

}
