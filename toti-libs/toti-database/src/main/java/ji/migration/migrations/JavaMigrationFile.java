package ji.migration.migrations;

import java.sql.SQLException;
import java.util.Objects;

import ji.migration.Migration;
import ji.querybuilder.QueryBuilder;
import toti.common.structures.ThrowingBiFunction;

public class JavaMigrationFile implements MigrationFile {
	
	private final String path;
	private final String name;
	private final ClassLoader loader;
	
	public JavaMigrationFile(String path, String name, ClassLoader loader) {
		this.path = path;
		this.loader = loader;
		this.name = name;
	}

	@Override
	public void migrate(QueryBuilder builder, boolean isRevert) throws Exception {
		migrate(builder, isRevert, getMigration((string, loader)->{
			return loader.loadClass(string).getDeclaredConstructor().newInstance();
		}));
	}

	protected void migrate(QueryBuilder builder, boolean isRevert, Migration m) throws SQLException {
		if (isRevert) {
			m.revert(builder);
		} else {
			m.migrate(builder);
		}
	}
	
	protected Migration getMigration(ThrowingBiFunction<String, ClassLoader, Object, Exception> createInstance) throws Exception {
		try {
			return (Migration)createInstance.apply(this.path + "." + this.name, loader);
		} catch (ClassNotFoundException e1) {
			// if file is in dir not in classpath
			try {
				return (Migration)createInstance.apply(name, loader);
			} catch (ClassNotFoundException e2) {
				throw new ClassNotFoundException("Migration class not found: " + e1.getMessage() + " OR " + e2.getMessage());
			}
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(loader, name, path);
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
		JavaMigrationFile other = (JavaMigrationFile) obj;
		return Objects.equals(loader, other.loader) && Objects.equals(name, other.name)
			&& Objects.equals(path, other.path);
	}

	@Override
	public String toString() {
		return "JavaMigrationFile [path=" + path + ", name=" + name + ", loader=" + loader + "]";
	}

}
