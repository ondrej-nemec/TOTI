package ji.querybuilder.instances;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class SqlServerInstanceTest extends AbstractInstanceTest {

	public SqlServerInstanceTest() {
		super(new SqlServerQueryBuilder());
	}

	@Override
	protected String getFunctions_concat() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getFunctions_groupConcat() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getFunctions_cast() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getFunctions_max() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getFunctions_min() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getFunctions_avg() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getFunctions_sum() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getFunctions_count() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getFunctions_lower() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getFunctions_upper() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getCreateTable() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getCreateTableWithPrimary() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getAlterTable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getAlterTableRename() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getDeleteTable() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getCreateView_fromString(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getCreateView_fromSelect(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getCreateView_fromMultiSelect(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getCreateView(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getAlterView_fromString(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getAlterView_fromStringAlias(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getAlterView_fromSelect(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getAlterView_fromMultiSelect(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getAlterView(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getDeleteView() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getCreateIndex() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getDeleteIndex() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQueryInsert() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQueryInsertFromSelect(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQueryUpdateBasic(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQueryUpdateJoins(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQueryUpdateWith(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQueryDeleteBasic(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQueryDeleteJoins(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQueryDeleteWith(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQuerySelect_fromString(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQuerySelect_fromStringAlias(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQuerySelect_fromSelect(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQuerySelect_with(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQuerySelect_withRecursive(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQuerySelect_fromMultiSelect(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQuerySelect(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getQueryMultipleSelect(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getBatch(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		Properties props = new Properties();
		props.setProperty("user", "root");
		props.setProperty("password", "");
		props.setProperty("serverTimezone", "Europe/Prague");
		props.setProperty("create", "true");
		props.setProperty("allowMultiQueries", "true");
		// return DriverManager.getConnection("jdbc:sqlserver://localhost;Database=" + DB_NAME + ";integratedSecurity=true", props);
		return null;
	}
	
}
