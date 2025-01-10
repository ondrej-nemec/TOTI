package ji.database;

import java.util.List;

public class DatabaseConfig {

	public final String type;
	
	public final String pathOrUrlToLocation; // path is relative ..workspace/pathOrUrlToLocation
	
	public final String schemaName;
	
	public final String login;

	public final String password;
	
	public final List<String> pathToMigrations;
	
	public final int poolSize;
	
	public DatabaseConfig(
			final String type,
			final String pathOrUrlToLocation,
			final String schemaName,
			final String login,
			final String password,
			final List<String> pathToMigrations,
			final int poolSize) {
		this.type = type;
		this.pathOrUrlToLocation = pathOrUrlToLocation;
		this.schemaName = schemaName;
		this.login = login;
		this.password = password;
		this.pathToMigrations = pathToMigrations;
		this.poolSize = poolSize;
	}

}
