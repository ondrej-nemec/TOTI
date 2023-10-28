package toti.ui.backend.help;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import ji.database.Database;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.SelectBuilder;
import toti.ui.backend.Owner;

public interface HelpDao{
	
	static final String HELP_KEY_NAME = "help_key";
	static final String HELP_DISPLAY_VALUE_NAME = "help_display_";
	static final String HELP_DISABLED_NAME = "help_disabled_";
	static final String HELP_GROUP_NAME = "help_group_";

	SelectBuilder _getHelp(QueryBuilder builder, String select);
	
	default List<Help> getHelp(
			Database database,
			Optional<Owner> owner, 
			String key, String title, String disabled, String optGroup, String ...params) throws SQLException {
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
		for (String param : params) {
			selectQuery.append(", " + param);
		}
		if (!sorting.toString().isEmpty()) {
			sorting.append(",");
		}
		sorting.append(title);
		return getHelp(
			database, 
			builder->_getHelp(builder, selectQuery.toString()), 
			owner, sorting.toString()
		);
	}
	
	default List<Help> getHelp(
			Database database, Function<QueryBuilder, SelectBuilder> selectFactory,
			Optional<Owner> owner, 
			String sortingColumn) throws SQLException {
		return database.applyBuilder((builder)->{
			List<Help>items = new LinkedList<>();
		    SelectBuilder select = selectFactory.apply(builder);
		    if (owner.isPresent()) {
		    	Owner o = owner.get();
		        if (!o.getAllowedValues().isEmpty()) {
		             select.where(o.getColumnName() + " in (:in)").addParameter(":in", o.getAllowedValues());
		        } else {
		             select.where("1=2"); // no results
		        }
		    }
		    select.orderBy(sortingColumn);
		    
		    select.fetchAll().forEach((row)->{
		    	Help help = new Help(
					row.getValue(HELP_KEY_NAME),
					row.getValue(HELP_DISPLAY_VALUE_NAME),
					row.getString(HELP_GROUP_NAME),
					row.getValue(HELP_DISABLED_NAME) == null ? false : row.getBoolean(HELP_DISABLED_NAME)
				);
		    	row.remove(HELP_DISPLAY_VALUE_NAME);
		    	row.remove(HELP_KEY_NAME);
		    	row.remove(HELP_GROUP_NAME);
		    	row.remove(HELP_DISABLED_NAME);
		    	row.forEach((key, dv)->{
		    		help.addParameter(key, dv.getValue());
		    	});
		        items.add(help);
		    });
		    return items;
		});
	}
}