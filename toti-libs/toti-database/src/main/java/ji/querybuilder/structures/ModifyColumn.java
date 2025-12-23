package ji.querybuilder.structures;

import java.util.Optional;

import ji.querybuilder.enums.ColumnType;

public class ModifyColumn {

	private final String name;

	// null - ignore
	private ColumnType columnType;
	private Boolean isUnique;
	private Boolean isNullable;
	// null - ignore, empty - remove
	private Optional<DefaultValue> defValue;

    public ModifyColumn(String name) {
        this.name = name;
    }

	public String getName() {
		return name;
	}

	public void setColumnType(ColumnType columnType) {
		this.columnType = columnType;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}

	public void setIsNullable(Boolean isNullable) {
		this.isNullable = isNullable;
	}

	public void removeDefault() {
		this.defValue = Optional.empty();
	}

	public void addDefault(Object value) {
		this.defValue = Optional.of(new DefaultValue(value, false));
	}

	public void modifyDefault(Object value) {
		this.defValue = Optional.of(new DefaultValue(value, true));
	}

	public ColumnType getColumnType() {
		return columnType;
	}

	public Optional<DefaultValue> getDefValue() {
		return defValue;
	}

	public Boolean getIsNullable() {
		return isNullable;
	}

	public Boolean getIsUnique() {
		return isUnique;
	}

}
