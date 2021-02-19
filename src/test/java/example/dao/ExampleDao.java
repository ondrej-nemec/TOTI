package example.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
		return getAll(database, table, Optional.empty(), pageIndex, pageSize, filters, sorting, forOwners);
	}

	@Override
	public Map<String, Object> get(int id) throws SQLException {
		return get(database, table, id);
	}

	@Override
	public Map<String, Object> delete(int id) throws SQLException {
		return delete(database, table, id);
	}

	@Override
	public void update(int id, Map<String, Object> values) throws SQLException {
		update(database, table, id, values);
	}

	@Override
	public int insert(Map<String, Object> values) throws SQLException {
		return insert(database, table, values);
	}

	@Override
	public int getTotalCount() throws SQLException {
		return getTotalCount(database, table);
	}

	@Override
	public Map<String, Object> getHelp(Collection<Object> forOwners) throws SQLException {
		return getHelp(database, table, Optional.empty(), forOwners, "id", "name");
	}

}
