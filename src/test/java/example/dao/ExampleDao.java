package example.dao;

import java.util.Optional;

import ji.database.Database;
import ji.database.support.DatabaseRow;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.translator.Translator;
import toti.application.EntityDao;
import toti.application.GridEntityDao;

public class ExampleDao implements EntityDao<Example>, GridEntityDao<Example> {

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
	public String getHelpDisplayValue() {
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
	
	@Override
	public SelectBuilder _getGrid(String select, QueryBuilder builder) {
		return _getAll(select, builder);
	}

}
