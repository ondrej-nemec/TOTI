package ji.querybuilder.structures;

import ji.querybuilder.enums.OnAction;

public class ForeignKey {

	private final String column;
	private final String referedTable;
	private final String referedColumn;
	private final OnAction onDelete;
	private final OnAction onUpdate;
	
	public ForeignKey(String column, String referedTable, String referedColumn, OnAction onDelete, OnAction onUpdate) {
		this.column = column;
		this.referedTable = referedTable;
		this.referedColumn = referedColumn;
		this.onDelete = onDelete;
		this.onUpdate = onUpdate;
	}

	public String getColumn() {
		return column;
	}

	public String getReferedTable() {
		return referedTable;
	}

	public String getReferedColumn() {
		return referedColumn;
	}

	public OnAction getOnDelete() {
		return onDelete;
	}

	public OnAction getOnUpdate() {
		return onUpdate;
	}

}
