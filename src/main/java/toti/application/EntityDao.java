package toti.application;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface EntityDao {
	
	List<Map<String, Object>> getAll(
			int pageIndex,
			int pageSize, 
			Map<String, Object> filters,
			Map<String, Object> sorting,
			Collection<Object> forOwners) throws SQLException;
	
	Map<String, Object> get(int id) throws SQLException;
	
	Map<String, Object> delete(int id) throws SQLException;
	
	void update(int id, Map<String, Object> values) throws SQLException;
	
	int insert(Map<String, Object> values) throws SQLException;
	
	int getTotalCount() throws SQLException;
	
	Map<String, Object> getHelp(Collection<Object> forOwners) throws SQLException;
	
}
