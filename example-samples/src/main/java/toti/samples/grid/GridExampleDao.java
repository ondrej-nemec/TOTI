package toti.samples.grid;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import ji.database.Database;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.BatchBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.enums.ColumnType;
import toti.ui.backend.EntityDao;
import toti.ui.backend.grid.GridDao;
import toti.ui.backend.grid.GridOptions;

public class GridExampleDao implements EntityDao, GridDao {

	private final Database database;
	
	private final String table = "grid_example";
	
	public GridExampleDao(Database database) {
		this.database = database;
	}

	@Override
	public SelectBuilder _getGrid(String select, QueryBuilder builder) {
		return builder._getAll(table, select);
	}
	
	public List<GridExampleEntity> getAll() throws SQLException {
		return getAll(database, table, row->new GridExampleEntity(
			row.getInteger("id"), 
			row.getString("text"),
			row.getInteger("number"),
			row.getInteger("range"), 
			row.getBoolean("select_col"),
			row.getDateTime("datetime_col"), 
			row.getDate("date_col"), 
			row.getTime("time_col"), 
			row.getString("month"),
			row.getString("week")
		));
	}
	
	public Object getAll(GridOptions options) throws SQLException {
		return getAll(database, table, options);
	}

	public void initDb() throws SQLException {
		try {
			get(database, table, "id", 0, row->row);
			return;
		} catch (SQLException e) {
			// connection error OR table not exists -> continue
		}
		// TODO try table exists
		database.applyBuilder((builder)->{
			builder.createTable(table)
			.addColumn("id", ColumnType.integer())
			.addColumn("text", ColumnType.string(10))
			.addColumn("number", ColumnType.integer())
			.addColumn("range", ColumnType.integer())
			.addColumn("select_col", ColumnType.bool())
			.addColumn("datetime_col", ColumnType.string(19))
			.addColumn("date_col", ColumnType.string(10))
			.addColumn("time_col", ColumnType.string(9))
			.addColumn("month", ColumnType.string(7))
			.addColumn("week", ColumnType.string(8))
			.addColumn("parent", ColumnType.integer())
			.execute();
			
			BatchBuilder batch = builder.batch();
			for (int i = 0; i < 100; i++) {
				batch.addBatch(
					builder.insert(table)
					.addValue("id", i)
					.addValue("text", "Item #" + i)
					.addValue("number", getNextInt(100, -100) / 10.0)
					.addValue("range", getNextInt(20, 0)*5)
					.addValue("select_col", i%3 == 2 ? null : (i%3 == 1))
					.addValue("datetime_col", getRandomLocal())
					.addValue("date_col", getRandomLocal().toLocalDate())
					.addValue("time_col", getRandomLocal().toLocalTime())
					.addValue("month", getRandomLocal().toLocalDate().toString().substring(0, 7))
					.addValue("week", "2022-W" + String.format("%02d", getRandomLocal().getDayOfMonth()%7))
					.addValue("parent", getParent(i))
				);
			}
			batch.execute();
			return null;
		});
	}
	
	private Integer getParent(int i) {
		Random random = new Random();
		int parent = random.nextInt(100);
		if (parent == i || parent%3==0) {
			return null;
		}
		/*for (int bound : new int[] {2, 3, 15}) {
			if (i%bound == 0 && i > bound && i < bound*bound*bound+1) {
				return i/bound;
			}
		}*/
		return parent;
	}

	private LocalDateTime getRandomLocal() {
		return LocalDateTime.of(
			2022, getNextInt(12, 1), getNextInt(28, 1), 
			getNextInt(24, 0), getNextInt(60, 0), getNextInt(60, 0)
		);
	}
	
	private int getNextInt(int max, int min) {
		return new Random().nextInt(max - min) + min;
	}
	
}
