package example.dao;

import java.util.Optional;

import database.Database;
import database.support.DatabaseRow;
import toti.application.EntityDao;

public class ExampleDao implements EntityDao<Example> {

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

	@Override
	public Example createEntity(DatabaseRow row) {
		try {
			return row.parse(Example.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
