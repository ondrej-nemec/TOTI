package example.dao;

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
	public Database getDatabase() {
		return database;
	}

	@Override
	public String getTableName() {
		return table;
	}

	@Override
	public Optional<String> getOwnerColumnName() {
		return Optional.empty();
	}

	@Override
	public String getHelpKey() {
		return "id";
	}

	@Override
	public String getHelpValue() {
		return "name";
	}

}
