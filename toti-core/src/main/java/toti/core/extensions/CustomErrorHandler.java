package toti.core.extensions;

import toti.core.answers.action.ResponseAction;
import toti.lib.tcpip.enums.StatusCode;

public interface CustomErrorHandler {
	
	ResponseAction onError(StatusCode status, Throwable t);
	
}
