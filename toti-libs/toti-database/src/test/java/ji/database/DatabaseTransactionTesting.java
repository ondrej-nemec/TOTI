package ji.database;

import java.sql.SQLException;
import java.util.Arrays;

import ji.common.Log4j2LoggerTestImpl;

public class DatabaseTransactionTesting {

	public static void main(String[] args) throws SQLException {
		DatabaseConfig config = new DatabaseConfig(
				"mysql",
				"//localhost",
				"toti", 
				"root", 
				"",
				Arrays.asList(),
				5
		);
		Database db = new Database(config, new Log4j2LoggerTestImpl(null));
		System.out.println("start");
		db.applyQuery((con)->{
			con.setAutoCommit(false);
			try {
				con.setAutoCommit(false);
				con.prepareStatement("create table a(id int)").execute();
				con.commit();
				con.setAutoCommit(false);
				con.prepareStatement("insert into a(id) values(1)").execute();
				con.commit();
				con.setAutoCommit(false);
				con.prepareStatement("insert into a(id) values(2)").execute();
				con.rollback();
			} catch (Exception e) {
				con.rollback();
				throw e;
			}
			con.commit();
			return null;
		});
		System.out.println("end");
	}

}
