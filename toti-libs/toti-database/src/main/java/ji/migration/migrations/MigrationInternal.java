package ji.migration.migrations;

import java.util.Objects;

import ji.querybuilder.QueryBuilder;

public class MigrationInternal {

	private final static String ALWAYS_ID = "ALWAYS";
	
	private final String id;
	private final String desc;
	private final String module;
	private MigrationFile file;

	public MigrationInternal(String id, String desc, String module) {
		this.id = id;
		this.desc = desc;
		this.module = module;
	}

	public String getId() {
		return id;
	}

	public String getDesc() {
		return desc;
	}
	
	public String getModule() {
		return module;
	}
	
	public MigrationFile getFile() {
		return file;
	}

	public MigrationInternal setFile(MigrationFile file) {
		this.file = file;
		return this;
	}
	
	public boolean isAlways() {
		return id.contains(ALWAYS_ID);
	}

	public void migrate(QueryBuilder builder, boolean isRevert) throws Exception {
		file.migrate(builder, isRevert);
	}

	@Override
	public int hashCode() {
		return Objects.hash(desc, file, id, module);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MigrationInternal other = (MigrationInternal) obj;
		return Objects.equals(desc, other.desc) && Objects.equals(file, other.file) && Objects.equals(id, other.id)
			&& Objects.equals(module, other.module);
	}

	@Override
	public String toString() {
		return "MigrationInternal [id=" + id + ", desc=" + desc + ", module=" + module + ", file=" + file + "]";
	}

}
