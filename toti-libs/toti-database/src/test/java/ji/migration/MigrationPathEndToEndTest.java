package ji.migration;

import java.util.Arrays;

import ji.common.Log4j2LoggerTestImpl;
import ji.database.Database;
import ji.database.DatabaseConfig;

public class MigrationPathEndToEndTest {

	public static void main(String[] args) {
		System.out.println("start");
		DatabaseConfig config = new DatabaseConfig(
				"postgresql", "//localhost", true, "paths", 
				"postgres", "1234", Arrays.asList(
					"migration/path_files_src",
					"migration/path_files_res",
					"test/path_files_dir"
				), 5
			);
		Database db = new Database(config, new Log4j2LoggerTestImpl(null));
		db.createDbAndMigrate();
		System.out.println("end");
	}
}
