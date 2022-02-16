package toti.application;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.MapInit;
import ji.database.Database;
import ji.database.support.DatabaseRow;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.mysql.MySqlQueryBuilder;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class EntityDaoTest implements Entity {
	/*
	@Test
	public void testGetAllWithSubstitutionWorks() {
		QueryBuilder builder = new QueryBuilder(new MySqlQueryBuilder(null));
		EntityDao<EntityDaoTest> dao = getEntityDao(Optional.empty());
		assertEquals(
			"SELECT"
			+ " table1.firstDisplay AS firstSubst, t.firstColumn,"
			+ " t.secondColumn, table2.secondDisplay AS secondSubst, t.thirdColumn"
			+ " FROM tableName t"
			+ " JOIN table1 ON t.firstSubst = table1.firstId"
			+ " JOIN table2 ON t.secondSubst = table2.secondId",
			dao._getAll(builder, dao.getSubstitution()).getSql()
		);
	}
	
	@Test
	@Parameters(method="dataApplyFiltersWorks")
	public void testApplyFiltersWorks(String sql, Collection<Object> ids, Optional<String> owner) {
		QueryBuilder builder = new QueryBuilder(new MySqlQueryBuilder(null));
		SelectBuilder select = builder.select("");
		EntityDao<EntityDaoTest> dao = getEntityDao(owner);
		dao._applyFilters(
			builder, 
			select,
			new MapInit<String, Object>()
				.append("firstDisplay", "v1")
				.append("firstColumn", "v2")
				.append("thirdColumn", "v3")
				.toMap(),
			ids
		);
		assertEquals(
			"SELECT  " + sql,
			select.getSql()
		);
	}
	
	public Object[] dataApplyFiltersWorks() {
		return new Object[] {
			new Object[] {
				"WHERE (1=1)"
				+ " AND (CAST(firstColumn AS CHAR(20)) like :firstColumnLikeValue OR firstColumn = :firstColumnValue)"
				+ " AND (CAST(thirdColumn AS CHAR(20)) like :thirdColumnLikeValue OR thirdColumn = :thirdColumnValue)"
				+ " AND (CAST(firstDisplay AS CHAR(20)) like :firstDisplayLikeValue OR firstDisplay = :firstDisplayValue)",
				Arrays.asList(),
				Optional.empty()
			},
			new Object[] {
				"WHERE (1=1)"
				+ " AND (1=2)"
				+ " AND (CAST(firstColumn AS CHAR(20)) like :firstColumnLikeValue OR firstColumn = :firstColumnValue)"
				+ " AND (CAST(thirdColumn AS CHAR(20)) like :thirdColumnLikeValue OR thirdColumn = :thirdColumnValue)"
				+ " AND (CAST(firstDisplay AS CHAR(20)) like :firstDisplayLikeValue OR firstDisplay = :firstDisplayValue)",
				Arrays.asList(),
				Optional.of("owner")
			},
			// invalid state
		//	new Object[] {
		//		"WHERE (1=1)"
		//		+ " AND (CAST(firstColumn AS CHAR(20)) like :firstColumnLikeValue OR firstColumn = :firstColumnValue)"
		//		+ " AND (CAST(thirdColumn AS CHAR(20)) like :thirdColumnLikeValue OR thirdColumn = :thirdColumnValue)"
		//		+ " AND (CAST(firstDisplay AS CHAR(20)) like :firstDisplayLikeValue OR firstDisplay = :firstDisplayValue)",
		//		null,
		//		Optional.of("owner")
		//	},
			new Object[] {
				"WHERE (1=1)"
				+ " AND (owner in (:in))"
				+ " AND (CAST(firstColumn AS CHAR(20)) like :firstColumnLikeValue OR firstColumn = :firstColumnValue)"
				+ " AND (CAST(thirdColumn AS CHAR(20)) like :thirdColumnLikeValue OR thirdColumn = :thirdColumnValue)"
				+ " AND (CAST(firstDisplay AS CHAR(20)) like :firstDisplayLikeValue OR firstDisplay = :firstDisplayValue)",
				Arrays.asList(1,2,3),
				Optional.of("owner")
			}
		};
	}
	
	@Test
	public void testApplySortingWorks() {
		EntityDao<EntityDaoTest> dao = getEntityDao(Optional.empty());
		assertEquals(
			"table1.firstDisplay DESC, t.firstColumn ASC, t.thirdColumn ASC", 
			dao._applySorting(
				new MapInit<String, Object>()
				.append("firstSubst", "DESC")
				.append("firstColumn", "ASC")
				.append("thirdColumn", "ASC")
				.toMap(),
				dao.getSubstitution()
			)
		);
	}
	*/
/*
	protected EntityDao<EntityDaoTest> getEntityDao(Optional<String> owner) {
		return new EntityDao<EntityDaoTest>() {
			@Override public String getTableName() { return "tableName"; }
			@Override public Database getDatabase() { return null; }
			@Override public EntityDaoTest createEntity(DatabaseRow row) {
				return new EntityDaoTest();
			}
			@Override
			public Optional<String> getOwnerColumnName() {
				return owner;
			}
			@Override
			public Map<String, GridSubstitution> getSubstitution() {
				Map<String, GridSubstitution> map = new HashMap<>();
				map.put("firstSubst", new GridSubstitution("table1", "firstId", "firstDisplay"));
				map.put("secondSubst", new GridSubstitution("table2", "secondId", "secondDisplay"));
				return map;
			}
		};
	}
*/
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("firstColumn", "--");
		map.put("secondColumn", "--");
		map.put("thirdColumn", "--");
		map.put("firstSubst", "--");
		map.put("secondSubst", "--");
		return map;
	}

}
