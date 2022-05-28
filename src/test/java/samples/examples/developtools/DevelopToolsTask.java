package samples.examples.developtools;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ji.common.structures.MapInit;
import ji.database.Database;
import ji.querybuilder.enums.ColumnType;
import ji.querybuilder.enums.Join;
import ji.translator.Translator;
import toti.application.Task;

public class DevelopToolsTask implements Task {
	
	private final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
	private Future<?> future;

	private final Translator trans;
	private final Database database;
	
	public DevelopToolsTask(Translator trans, Database database) {
		this.trans = trans;
		this.database = database;
	}

	@Override
	public void start() throws Exception {
		try {
			// try create and fill tables OneTable and JoinTable if not exist
			database.applyBuilder((b)->{
				b.createTable("OneTable")
				.addColumn("id", ColumnType.integer())
				.addColumn("Name", ColumnType.string(20))
				.execute();
				b.createTable("JoinTable")
				.addColumn("FK_id", ColumnType.integer())
				.addColumn("Col", ColumnType.string(20))
				.execute();
				
				for (int i = 1; i < 15; i++) {
					b.insert("OneTable")
					.addValue("id", i)
					.addValue("Name", "Name #" + i)
					.execute();
					b.insert("JoinTable")
					.addValue("FK_id", i)
					.addValue("Col", "Col #" + i)
					.execute();
				}
				return null;
			});
		} catch (SQLException e) {
			// if fail - table exists (or connection fail)
		}
		
		
		future = pool.scheduleWithFixedDelay(()->{
			trans.translate("some.key.to.test");
			trans.translate(
				"some.another.key", 
				new MapInit<String, Object>().append("param1", "value1").append("param2", "value2").toMap()
			);
			try {
				// not executed, in profiler
				database.applyQuery((con)->{
					try (PreparedStatement stmt = con.prepareStatement("select * from OneTable where id = ?")) {
						stmt.setInt(1, 5);
					}
					return null;
				});
				// executed, in profiler
				database.applyQuery((con)->{
					try (PreparedStatement stmt = con.prepareStatement("select * from OneTable where Name = ?")) {
						stmt.setString(1, "Name #3");
						stmt.executeQuery();
					}
					return null;
				});
				// not executed, not in profiler
				database.applyBuilder((builder)->{
					builder.select("'Task - not executed', *, :const")
					.from("OneTable")
					.join("JoinTable", Join.INNER_JOIN, "id = FK_id")
					.where("Col = :value")
					.addParameter(":const", 123)
					.addParameter(":value", "Col #6");
					return null;
				});
				// executed, in profiler
				database.applyBuilder((builder)->{
					builder.select("'Task - executed', *, :const")
					.from("OneTable")
					.join("JoinTable", Join.INNER_JOIN, "OneTable.id = JoinTable.FK_id")
					.where("Name like :value")
					.addParameter(":const", 123)
					.addParameter(":value", "%#1%")
					.fetchAll();
					return null;
				});
			} catch (SQLException e) {
				e.printStackTrace();
				// query fail - no db connection
			}
			trans.translate("not.existing.file", new MapInit<String, Object>().append("b", "BB").toMap());
		}, 0, 1, TimeUnit.MINUTES);
	}

	@Override
	public void stop() throws Exception {
		if (future != null) {
			future.cancel(true);
		}
		pool.shutdownNow();
	}

}
