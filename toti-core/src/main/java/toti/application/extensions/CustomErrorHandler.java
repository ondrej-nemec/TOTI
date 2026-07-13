package toti.application.extensions;

import toti.application.answers.action.ResponseAction;
import toti.lib.tcpip.enums.StatusCode;

public interface CustomErrorHandler {
	
	ResponseAction onError(StatusCode status, Throwable t);
	
}
