package example;

import ji.common.Logger;
import toti.logging.TotiLogger;

public class AuditTrail {

	private final Logger logger = TotiLogger.getLogger("audit-trail");
	
	public void insert(Object userId, Object inserted) {
		logger.info(String.format("User #%s insert. Values: %s", userId, inserted));
	}
	
	public void update(Object userId, Object origin, Object updated) {
		logger.info(String.format("User #%s update. Origin: %s, new: %s", userId, origin, updated));
	}
	
	public void delete(Object userId, Object deleted) {
		logger.info(String.format("User #%s delete. Values: %s", userId, deleted));
	}
		
}
