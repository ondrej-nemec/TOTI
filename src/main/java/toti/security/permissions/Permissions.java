package toti.security.permissions;

import java.io.Serializable;

public interface Permissions extends Serializable {

	Rules getRulesForDomain(String domain);
	
}
