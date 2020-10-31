package controllers;

import java.util.List;
import java.util.Map;

public interface EntityDao <E extends Entity> {

	List<Map<String, Object>> getData(int pageIndex, int pageSize, Map<String, Object> filters, Map<String, Object> sorting);
	
	List<E> getAll();
	
	E get(int id);
	
	void delete(int id);
	
	void update(int id, E entity);
	
	int insert(E entity);
	
}
