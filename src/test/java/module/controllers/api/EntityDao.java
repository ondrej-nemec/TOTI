package module.controllers.api;

import java.util.List;
import java.util.Map;

public interface EntityDao {

	List<Map<String, Object>> getAll(
			int pageIndex,
			int pageSize, 
			Map<String, Object> filters,
			Map<String, Object> sorting) throws Exception;
	
	Map<String, Object> get(int id) throws Exception;
	
	Map<String, Object> delete(int id) throws Exception;
	
	void update(int id, Map<String, Object> values) throws Exception;
	
	int insert(Map<String, Object> values) throws Exception;
	
	int getTotalCount() throws Exception;
	
}
