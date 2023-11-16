package toti.ui.backend.help;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import ji.database.Database;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.SelectBuilder;
import toti.ui.backend.Owner;

public interface HelpDao{
	
	static final String _HELP_KEY_NAME = "help_key";
	static final String _HELP_DISPLAY_VALUE_NAME = "help_display_";
	static final String _HELP_DISABLED_NAME = "help_disabled_";
	static final String _HELP_GROUP_NAME = "help_group_";
	
	default List<Help> getHelp(
			Database database,
			Optional<Owner> owner,
			BiFunction<String, QueryBuilder, SelectBuilder> getSelect,
			String key, String title, String disabled, String optGroup, String ...params) throws SQLException {
		StringBuilder selectQuery = new StringBuilder();
		selectQuery.append(key);
		selectQuery.append(" AS ");
		selectQuery.append(_HELP_KEY_NAME);
		selectQuery.append(",");             

		selectQuery.append(title);
		selectQuery.append(" AS ");
		selectQuery.append(_HELP_DISPLAY_VALUE_NAME);
		if (disabled != null) {
		    selectQuery.append(", ");
		    selectQuery.append(disabled);
		    selectQuery.append(" AS ");
		    selectQuery.append(_HELP_DISABLED_NAME);
		}
		StringBuilder sorting = new StringBuilder();
		if (optGroup != null) {
			sorting.append(optGroup);
		    selectQuery.append(", ");
		    selectQuery.append(optGroup);
		    selectQuery.append(" AS ");
		    selectQuery.append(_HELP_GROUP_NAME);
		}
		for (String param : params) {
			selectQuery.append(", " + param);
		}
		if (!sorting.toString().isEmpty()) {
			sorting.append(",");
		}
		sorting.append(title);
		return _getHelp(
			database, 
			builder->getSelect.apply(selectQuery.toString(), builder), 
			owner, sorting.toString()
		);
	}
	
	default List<Help> _getHelp(
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
					row.getValue(_HELP_KEY_NAME),
					row.getValue(_HELP_DISPLAY_VALUE_NAME),
					row.getString(_HELP_GROUP_NAME),
					row.getValue(_HELP_DISABLED_NAME) == null ? false : row.getBoolean(_HELP_DISABLED_NAME)
				);
		    	row.remove(_HELP_DISPLAY_VALUE_NAME);
		    	row.remove(_HELP_KEY_NAME);
		    	row.remove(_HELP_GROUP_NAME);
		    	row.remove(_HELP_DISABLED_NAME);
		    	row.forEach((key, dv)->{
		    		help.addParameter(key, dv.getValue());
		    	});
		        items.add(help);
		    });
		    return items;
		});
	}
}