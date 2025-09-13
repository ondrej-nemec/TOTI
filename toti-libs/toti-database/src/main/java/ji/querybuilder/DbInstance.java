package ji.querybuilder;

import java.util.List;

import ji.querybuilder.builder_impl.AlterTableBuilderImpl;
import ji.querybuilder.builder_impl.AlterViewBuilderImpl;
import ji.querybuilder.builder_impl.CallProcedureBuilderImpl;
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
	
}
