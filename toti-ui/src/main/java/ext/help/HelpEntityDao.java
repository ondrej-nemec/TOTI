package ext.help;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import ext.Entity;
import ji.database.Database;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.SelectBuilder;

public interface HelpEntityDao<T extends Entity>{
	
	Database getDatabase();
	
	default String getHelpKey() {
		return null;
	}
	
	default String getHelpDisplayValue() {
		return null;
	}
	
	default String getHelpDisabled() {
		return null;
	}
	
	default String getHelpOptgroup() {
		return null;
	}
	
	String getHelpTable();
	
	default Optional<String> getOwnerColumnName() {
		return Optional.empty();
	}

	default List<Help> getHelp(Collection<Object> forOwners) throws SQLException {
		return getHelp(
			getDatabase(), getHelpTable(), forOwners, getOwnerColumnName(),
			getHelpKey(), getHelpDisplayValue(), getHelpDisabled(), getHelpOptgroup()
		);
	}
	
	static final String HELP_KEY_NAME = "help_key";
	static final String HELP_DISPLAY_VALUE_NAME = "help_display_";
	static final String HELP_DISABLED_NAME = "help_disabled_";
	static final String HELP_GROUP_NAME = "help_group_";
	
	default List<Help> getHelp(
			Database database, String table,
			Collection<Object> forOwners, Optional<String> ownerColumnName, 
			String key, String title, String disabled, String optGroup) throws SQLException {
		StringBuilder selectQuery = new StringBuilder();
		selectQuery.append(key);
		selectQuery.append(" AS ");
		selectQuery.append(HELP_KEY_NAME);
		selectQuery.append(",");             

		selectQuery.append(title);
		selectQuery.append(" AS ");
		selectQuery.append(HELP_DISPLAY_VALUE_NAME);
		if (disabled != null) {
		    selectQuery.append(", ");
		    selectQuery.append(disabled);
		    selectQuery.append(" AS ");
		    selectQuery.append(HELP_DISABLED_NAME);
		}
		StringBuilder sorting = new StringBuilder();
		if (optGroup != null) {
			sorting.append(optGroup);
		    selectQuery.append(", ");
		    selectQuery.append(optGroup);
		    selectQuery.append(" AS ");
		    selectQuery.append(HELP_GROUP_NAME);
		}
		if (!sorting.toString().isEmpty()) {
			sorting.append(",");
		}
		sorting.append(title);
		return getHelp(
			database, 
			builder->builder.select(selectQuery.toString()).from(table), 
			forOwners, ownerColumnName, sorting.toString()
		);
	}
	
	default List<Help> getHelp(
			Database database, Function<QueryBuilder, SelectBuilder> selectFactory,
			Collection<Object> forOwners, Optional<String> ownerColumnName, 
			String sortingColumn) throws SQLException {
		return database.applyBuilder((builder)->{
			List<Help>items = new LinkedList<>();
		    SelectBuilder select = selectFactory.apply(builder);
		    if (ownerColumnName.isPresent() && forOwners != null) {
		        if (!forOwners.isEmpty()) {
		             select.where(ownerColumnName.get() + " in (:in)").addParameter(":in", forOwners);
		        } else {
		             select.where("1=2"); // no results
		        }
		    }
		    select.orderBy(sortingColumn);
		    
		    select.fetchAll().forEach((row)->{
		        items.add(
		             new Help(
		                  row.getValue(HELP_KEY_NAME),
		                  row.getValue(HELP_DISPLAY_VALUE_NAME),
		                  row.getString(HELP_GROUP_NAME),
		                  row.getValue(HELP_DISABLED_NAME) == null ? false : row.getBoolean(HELP_DISABLED_NAME)
		             )
		        );
		    });
		    return items;
		});
	}
}