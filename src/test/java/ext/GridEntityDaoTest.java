package ext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import ext.Entity;
import ext.Filter;
import ext.FilterMode;
import ext.GridEntityDao;
import ext.Sort;
import ji.common.structures.MapInit;
import ji.common.structures.SortedMap;
import ji.database.Database;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.mysql.MySqlFunctions;
import ji.querybuilder.mysql.MySqlSelectBuilder;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class GridEntityDaoTest implements Entity {
	
	@Test
	@Parameters(method = "dataApplyFilters")
	public void testApplyFilters(
			String message,
			Optional<String> owner, Collection<Object> forOwners,
			SortedMap<String, Filter> filters, String expected) {
		QueryBuilder builder = mock(QueryBuilder.class);
		SelectBuilder select = new MySqlSelectBuilder(null, ""); // mock(SelectBuilder.class);
		when(builder.getSqlFunctions()).thenReturn(new MySqlFunctions());
		
		GridEntityDao<GridEntityDaoTest> dao = new GridEntityDao<GridEntityDaoTest>() {			
			@Override public String getTableName() { return null; }
			@Override public Database getDatabase() { return null; }
			@Override public Optional<String> getOwnerColumnName() { return owner; }
			@Override public SelectBuilder _getGrid(String select, QueryBuilder builder) { return null; }
		};
		
		dao._applyFilters(builder, select, filters, forOwners, owner);
		assertEquals(message, "SELECT  " + expected, select.createSql());
	}
	
	public Object[] dataApplyFilters() {
		return new Object[] {
			// owner specified, owners is empty, no filters
			new Object[] {
					"owner specified, owners is empty, no filters",
				Optional.of("ownerId"), Arrays.asList(),
				new MapInit<String, Filter>().toSortedMap(),
				"WHERE (1=1) AND (1=2)"
			},
			// owner specified, owners with ids, no filters
			new Object[] {
					"owner specified, owners with ids, no filters",
				Optional.of("ownerId"), Arrays.asList(1, "--id--", false),
				new MapInit<String, Filter>().toSortedMap(),
				"WHERE (1=1) AND (ownerId in (1,'--id--',false))"
			},
			// no owner, owners with ids, no filters
			new Object[] {
					"no owner, owners with ids, no filters",
				Optional.empty(), Arrays.asList(1, "--id--", false),
				new MapInit<String, Filter>().toSortedMap(),
				"WHERE (1=1)"
			},
			// no owner, no ids, like
			new Object[] {
					"no owner, no ids, like",
				Optional.empty(), Arrays.asList(),
				new MapInit<String, Filter>()
					.append("likeColumn", new Filter("likeColumn", FilterMode.LIKE, "--value--", false, false))
					.toSortedMap(),
				"WHERE (1=1) AND (CONCAT('', likeColumn) LIKE '%--value--%')"
			},
			// no owner, no ids, starts
			new Object[] {
					"no owner, no ids, starts",
				Optional.empty(), Arrays.asList(),
				new MapInit<String, Filter>()
					.append("startsColumn", new Filter("startsColumn", FilterMode.STARTS_WITH, "--value--", false, false))
					.toSortedMap(),
				"WHERE (1=1) AND (CONCAT('', startsColumn) LIKE '%--value--')"
			},
			// no owner, no ids, ends
			new Object[] {
					"no owner, no ids, ends",
				Optional.empty(), Arrays.asList(),
				new MapInit<String, Filter>()
				.append("endsColumn", new Filter("endsColumn", FilterMode.ENDS_WITH, "--value--", false, false))
				.toSortedMap(),
				"WHERE (1=1) AND (CONCAT('', endsColumn) LIKE '--value--%')"
			},
			// no owner, no ids, equas
			new Object[] {
					"no owner, no ids, equas",
				Optional.empty(), Arrays.asList(),
				new MapInit<String, Filter>()
				.append("equalsColumn", new Filter("equalsColumn", FilterMode.EQUALS, "--value--", false, false))
				.toSortedMap(),
				"WHERE (1=1) AND (equalsColumn = '--value--')"
			},
			// combined
			new Object[] {
					"combined",
				Optional.of("ownerId"), Arrays.asList(1, "--id--", false),
				new MapInit<String, Filter>()
				.append("likeColumn", new Filter("likeColumn", FilterMode.LIKE, "--value--", false, false))
				.append("startsColumn", new Filter("startsColumn", FilterMode.STARTS_WITH, "--value--", false, false))
				.append("endsColumn", new Filter("endsColumn", FilterMode.ENDS_WITH, "--value--", false, false))
				.append("equalsColumn", new Filter("equalsColumn", FilterMode.EQUALS, "--value--", false, false))
				.toSortedMap(),
				"WHERE (1=1) AND (ownerId in (1,'--id--',false))"
				+ " AND (equalsColumn = '--value--')"
				+ " AND (CONCAT('', startsColumn) LIKE '%--value--')"
				+ " AND (CONCAT('', likeColumn) LIKE '%--value--%')"
				+ " AND (CONCAT('', endsColumn) LIKE '--value--%')"
			},
		};
	}
	
	@Test
	@Parameters(method = "dataApplySorting")
	public void testApplySorting(
			SortedMap<String, Sort> sorting, String expected) {
		SelectBuilder select = new MySqlSelectBuilder(null, ""); // mock(SelectBuilder.class);
		
		GridEntityDao<GridEntityDaoTest> dao = new GridEntityDao<GridEntityDaoTest>() {			
			@Override public String getTableName() { return null; }
			@Override public Database getDatabase() { return null; }
			@Override public Optional<String> getOwnerColumnName() { return null; }
			@Override public SelectBuilder _getGrid(String select, QueryBuilder builder) { return null; }
		};
		
		dao._applySorting(select, sorting);
		assertEquals("SELECT " + expected, select.createSql());
	}
	
	public Object[] dataApplySorting() {
		return new Object[] {
			// no sorting
			new Object[] {
				new MapInit<String, Sort>().toSortedMap(),
				""
			},
			// one sort asc
			new Object[] {
				new MapInit<String, Sort>()
				.append("sortAsc", new Sort("sortAsc", false))
				.toSortedMap(),
				" ORDER BY sortAsc ASC"
			},
			// one sort desc
			new Object[] {
				new MapInit<String, Sort>()
				.append("sortDesc", new Sort("sortDesc", true))
				.toSortedMap(),
				" ORDER BY sortDesc DESC"
			},
			// more sorts
			new Object[] {
				new MapInit<String, Sort>()
				.append("sortAsc", new Sort("sortAsc", false))
				.append("sortDesc", new Sort("sortDesc", true))
				.append("sortAsc2", new Sort("sortAsc2", false))
				.toSortedMap(),
				" ORDER BY sortAsc ASC, sortDesc DESC, sortAsc2 ASC"
			},
			// name substitution
			new Object[] {
				new MapInit<String, Sort>()
				.append("sortName", new Sort("realSortName", false))
				.toSortedMap(),
				" ORDER BY realSortName ASC"
			},
		};
	}
	// TODO test for help
	/*
	@Test
	@Parameters(method = "dataHelp")
	public void testHelp(String key, String value, String disabled, String group, String expected) {
		ObjectBuilder<SelectBuilder> select = new ObjectBuilder<>();
		QueryBuilder builder = new QueryBuilder(new MySqlQueryBuilder(null) {
			@Override public SelectBuilder select(String... query) {
				SelectBuilder s = super.select(query);
				select.set(s);
				return s;
			}
		});
		
		GridEntityDao<GridEntityDaoTest> dao = new GridEntityDao<GridEntityDaoTest>() {
			@Override public String getHelpKey() { return key; }
			@Override public String getHelpDisabled() { return disabled; }
			@Override public String getHelpDisplayValue() { return value; }
			@Override public String getHelpOptgroup() { return group; }
			
			@Override public String getTableName() { return "tableName"; }
			@Override public Database getDatabase() { return null; }
			@Override public Optional<String> getOwnerColumnName() { return null; }
			@Override public SelectBuilder _getGrid(String select, QueryBuilder builder) { return null; }
		};
		
		dao._getHelp(builder);
		assertEquals(expected, select.get().createSql());
	}
	
	public Object[] dataHelp() {
		return new Object[] {
			new Object[] {
				"helpKey", "helpValue", null, null,
				"SELECT helpKey AS help_key,helpValue AS help_display_value"
				+ " FROM tableName" //  ORDER BY helpValue // order move after user
			},
			new Object[] {
				"helpKey", "helpValue", "isDisabled", null,
				"SELECT helpKey AS help_key,helpValue AS help_display_value, isDisabled AS help_disabled"
				+ " FROM tableName" //  ORDER BY helpValue // order move after user
			},
			new Object[] {
				"helpKey", "helpValue", null, "optGroup",
				"SELECT helpKey AS help_key,helpValue AS help_display_value, optGroup AS help_group"
				+ " FROM tableName" // ORDER BY helpValue // order move after user
			},
			new Object[] {
				"helpKey", "helpValue", "isDisabled", "optGroup",
				"SELECT helpKey AS help_key,helpValue AS help_display_value, isDisabled AS help_disabled, optGroup AS help_group"
				+ " FROM tableName" // ORDER BY helpValue // order move after user
			}
		};
	}*/

}
