package toti.lib.database.base;

import java.time.ZoneId;
import java.util.List;
import java.util.Properties;

public class DatabaseConfig {

	public final String type;
	public final String pathOrUrlToLocation; // path is relative ..workspace/pathOrUrlToLocation
	public final String schemaName;
	public final String login;
	public final String password;
	public final int poolSize;
	
	public final List<String> pathToMigrations;
	
	private final Properties properties;
	
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
		this.properties = new Properties();
		properties.setProperty("user", login);
		properties.setProperty("password", password);
		properties.setProperty("serverTimezone", ZoneId.systemDefault().toString());
		properties.setProperty("create", "true");
		properties.setProperty("allowMultiQueries", "true");
	}
	
	public DatabaseConfig setProperty(String name, Object value) {
		properties.setProperty(name, value == null ? null : value.toString());
		return this;
	}
	
	public Properties getProperties() {
		return properties;
	}

}
