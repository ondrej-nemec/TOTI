package module;

import java.util.Map;

import ji.common.Logger;
import toti.logging.TotiLogger;

public class AuditTrail {

	private final Logger logger = TotiLogger.getLogger("audit-trail");
	
	public void insert(Object userId, Map<String, Object> inserted) {
		logger.info(String.format("User #%s insert. Values: %s", userId, inserted));
	}
	
	public void update(Object userId, Map<String, Object> origin, Map<String, Object> updated) {
		logger.info(String.format("User #%s update. Origin: %s, new: %s", userId, origin, updated));
	}
	
	public void delete(Object userId, Map<String, Object> deleted) {
		logger.info(String.format("User #%s delete. Values: %s", userId, deleted));
	}
		
}
