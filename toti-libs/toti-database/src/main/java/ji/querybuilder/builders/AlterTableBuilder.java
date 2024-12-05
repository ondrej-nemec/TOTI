package ji.querybuilder.builders;

import ji.querybuilder.builders.alterTable.AlterTableBuilderBase;
import ji.querybuilder.builders.alterTable.AlterTableBuilderRenameColumn;
import ji.querybuilder.builders.alterTable.AlterTableBuilderRenameTable;

public interface AlterTableBuilder extends AlterTableBuilderBase, AlterTableBuilderRenameColumn, AlterTableBuilderRenameTable {

}
