package toti.core.extensions;

import java.util.List;

import toti.core.answers.action.ResponseAction;

public interface TotiExtension extends Extension {
	
	List<String> getListeningUri();
	
	ResponseAction get(String uri, boolean isDevelop);

}
