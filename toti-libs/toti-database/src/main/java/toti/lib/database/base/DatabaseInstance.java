package toti.lib.database.base;

import java.sql.SQLException;

import toti.lib.database.querybuilder.DbInstance;

public interface DatabaseInstance {
	
	void createDb() throws SQLException;
	
	DbInstance getBuilderInstance();
	
	String getConnectionString();

}
