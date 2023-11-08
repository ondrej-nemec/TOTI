package toti.ui.backend.help;

import static org.junit.Assert.fail;

import org.junit.Test;

public class HelpDaoTest {

	@Test
	public void test() {
		fail("TODO");
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
