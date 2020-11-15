# Generate Entity

## Required

###

File: layout.jsp

### Audit trail

```java
import java.util.Map;

public interface AuditTrail {

	void insert(String userId, Map<String, Object> inserted);
	
	void update(String userId, Map<String, Object> origin, Map<String, Object> updated);
	
	void delete(String userId, Map<String, Object> deleted);
		
}
```

### Module config

```java
import toti.registr.Registr;

public interface ModuleConfig {

	void initInstances(Registr registr);
	
	void addRoutes(Router router);
	
	String getTemplatesPath();
	
	String getControllersPath();
}
```