package ji.querybuilder.instances;

import ji.querybuilder.DbInstance;
import ji.querybuilder.builder_impl.AlterTableBuilderImpl;
import ji.querybuilder.builder_impl.AlterViewBuilderImpl;
import ji.querybuilder.builder_impl.CreateIndexBuilderImpl;
import ji.querybuilder.builder_impl.CreateTableBuilderImpl;
import ji.querybuilder.builder_impl.CreateViewBuilderImpl;
import ji.querybuilder.builder_impl.DeleteBuilderImpl;
import ji.querybuilder.builder_impl.DeleteIndexBuilderImpl;
import ji.querybuilder.builder_impl.DeleteTableBuilderImpl;
import ji.querybuilder.builder_impl.DeleteViewBuilderImpl;
import ji.querybuilder.builder_impl.InsertBuilderImpl;
import ji.querybuilder.builder_impl.MultipleSelectBuilderImpl;
import ji.querybuilder.builder_impl.SelectBuilderImpl;
import ji.querybuilder.builder_impl.UpdateBuilderImpl;
import ji.querybuilder.enums.ColumnType;

public class DerbyQueryBuilder implements DbInstance {

	@Override
	public String concat(String param, String... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String cast(String param, ColumnType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String groupConcat(String param, String delimeter) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String max(String param) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String min(String param) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String avg(String param) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String sum(String param) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String count(String param) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String lower(String param) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String upper(String param) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*************/

	@Override
	public String createSql(DeleteIndexBuilderImpl deleteIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSql(CreateIndexBuilderImpl createIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSql(InsertBuilderImpl insert, boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSql(UpdateBuilderImpl updateBuilder, boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSql(DeleteBuilderImpl delete, boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSql(SelectBuilderImpl select, boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSql(MultipleSelectBuilderImpl multipleSelect, boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSql(CreateViewBuilderImpl createView, boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSql(AlterViewBuilderImpl alterView, boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSql(DeleteViewBuilderImpl deleteView) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSql(CreateTableBuilderImpl createTable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSql(AlterTableBuilderImpl alterTable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSql(DeleteTableBuilderImpl deleteTable) {
		// TODO Auto-generated method stub
		return null;
	}

}
