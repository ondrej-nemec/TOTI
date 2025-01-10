package ji.database;

import java.sql.SQLException;

import ji.querybuilder.DbInstance;

public interface DatabaseInstance {

	void createDb() throws SQLException;
	
	DbInstance getBuilderInstance();

}
