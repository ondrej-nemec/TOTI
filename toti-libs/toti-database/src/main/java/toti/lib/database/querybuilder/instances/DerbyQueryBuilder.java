package toti.lib.database.querybuilder.instances;

import java.util.List;

import toti.lib.database.querybuilder.DbInstance;
import toti.lib.database.querybuilder.builder_impl.AlterTableBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.AlterViewBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.CallProcedureBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.CreateIndexBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.CreateTableBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.CreateViewBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.DeleteBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.DeleteIndexBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.DeleteTableBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.DeleteViewBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.InsertBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.MultipleSelectBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.SelectBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.UpdateBuilderImpl;
import toti.lib.database.querybuilder.enums.ColumnType;

@Deprecated
public class DerbyQueryBuilder implements DbInstance {

	@Override
	public String concat(String param1, String param2, String... params) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String trim(String param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String cast(String param, ColumnType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String groupConcat(String param, String delimeter, String orderBy) {
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
	public List<String> createSql(InsertBuilderImpl insert, boolean create) {
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
	public List<String> createSql(AlterTableBuilderImpl alterTable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSql(DeleteTableBuilderImpl deleteTable) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String createSql(CallProcedureBuilderImpl callProcedure, boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

}
