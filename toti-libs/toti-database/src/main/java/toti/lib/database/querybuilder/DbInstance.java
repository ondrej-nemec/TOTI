package toti.lib.database.querybuilder;

import java.util.List;

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

public interface DbInstance extends Functions {

	String createSql(DeleteIndexBuilderImpl deleteIndex);

	String createSql(CreateIndexBuilderImpl createIndex);

	List<String> createSql(InsertBuilderImpl insert, boolean create);

	String createSql(UpdateBuilderImpl updateBuilder, boolean create);

	String createSql(DeleteBuilderImpl delete, boolean create);

	String createSql(SelectBuilderImpl select, boolean create);

	String createSql(MultipleSelectBuilderImpl multipleSelect, boolean create);

	String createSql(CreateViewBuilderImpl createView, boolean create);

	String createSql(AlterViewBuilderImpl alterView, boolean create);

	String createSql(DeleteViewBuilderImpl deleteView);

	String createSql(CreateTableBuilderImpl createTable);

	List<String> createSql(AlterTableBuilderImpl alterTable);

	String createSql(DeleteTableBuilderImpl deleteTable);

	String createSql(CallProcedureBuilderImpl callProcedure, boolean create);
	
	Escape getEscape();

}
