package toti.ui.backend.grid;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.MapInit;
import ji.common.structures.SortedMap;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.mysql.MySqlFunctions;
import ji.querybuilder.mysql.MySqlSelectBuilder;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.ui.backend.Owner;

@RunWith(JUnitParamsRunner.class)
public class GridDaoTest {
	
	@Test
	@Parameters(method = "dataApplyFilters")
	public void testApplyFilters(
			String message,
			Optional<Owner> owner,
			SortedMap<String, Filter> filters, String expected) {
		QueryBuilder builder = mock(QueryBuilder.class);
		SelectBuilder select = new MySqlSelectBuilder(null, ""); // mock(SelectBuilder.class);
		when(builder.getSqlFunctions()).thenReturn(new MySqlFunctions());
		
		GridDao dao = new GridDao() {};
		
		dao._applyFilters(builder, select, filters, owner);
		assertEquals(message, "SELECT  " + expected, select.createSql());
	}
	
	public Object[] dataApplyFilters() {
		return new Object[] {
			// owner specified, owners is empty, no filters
			new Object[] {
					"owner specified, owners is empty, no filters",
				Optional.of(new Owner("ownerId", Arrays.asList())),
				new MapInit<String, Filter>().toSortedMap(),
				"WHERE (1=1) AND (1=2)"
			},
			// owner specified, owners with ids, no filters
			new Object[] {
					"owner specified, owners with ids, no filters",
				Optional.of(new Owner("ownerId", Arrays.asList(1, "--id--", false))),
				new MapInit<String, Filter>().toSortedMap(),
				"WHERE (1=1) AND (ownerId in (1,'--id--',false))"
			},
			// no owner, owners with ids, no filters
			/*new Object[] {
					"no owner, owners with ids, no filters",
				Optional.empty(), Arrays.asList(1, "--id--", false),
				new MapInit<String, Filter>().toSortedMap(),
				"WHERE (1=1)"
			},*/
			// no owner, no ids, like
			new Object[] {
					"no owner, no ids, like",
				Optional.empty(),
				new MapInit<String, Filter>()
					.append("likeColumn", new Filter("likeColumn", FilterMode.LIKE, "--value--", false, false))
					.toSortedMap(),
				"WHERE (1=1) AND (CONCAT('', likeColumn) LIKE '%--value--%')"
			},
			// no owner, no ids, starts
			new Object[] {
					"no owner, no ids, starts",
				Optional.empty(),
				new MapInit<String, Filter>()
					.append("startsColumn", new Filter("startsColumn", FilterMode.STARTS_WITH, "--value--", false, false))
					.toSortedMap(),
				"WHERE (1=1) AND (CONCAT('', startsColumn) LIKE '%--value--')"
			},
			// no owner, no ids, ends
			new Object[] {
					"no owner, no ids, ends",
				Optional.empty(),
				new MapInit<String, Filter>()
				.append("endsColumn", new Filter("endsColumn", FilterMode.ENDS_WITH, "--value--", false, false))
				.toSortedMap(),
				"WHERE (1=1) AND (CONCAT('', endsColumn) LIKE '--value--%')"
			},
			// no owner, no ids, equas
			new Object[] {
					"no owner, no ids, equas",
				Optional.empty(),
				new MapInit<String, Filter>()
				.append("equalsColumn", new Filter("equalsColumn", FilterMode.EQUALS, "--value--", false, false))
				.toSortedMap(),
				"WHERE (1=1) AND (equalsColumn = '--value--')"
			},
			// combined
			new Object[] {
					"combined",
				Optional.of(new Owner("ownerId", Arrays.asList(1, "--id--", false))),
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
		
		GridDao dao = new GridDao() {};
		
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
}
