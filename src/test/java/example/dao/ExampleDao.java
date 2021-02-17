package example.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import database.Database;
import toti.application.EntityDao;

public class ExampleDao implements EntityDao {

	private final Database database;
	private final String table = "Example";
	
	public ExampleDao(Database database) {
		this.database = database;
	}
	@Override
	public List<Map<String, Object>> getAll(int pageIndex, int pageSize, Map<String, Object> filters,
			Map<String, Object> sorting, Collection<Object> forOwners) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> get(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> delete(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(int id, Map<String, Object> values) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int insert(Map<String, Object> values) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalCount() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, Object> getHelp(Collection<Object> forOwners) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
