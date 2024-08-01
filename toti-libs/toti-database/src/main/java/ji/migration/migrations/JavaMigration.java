package ji.migration.migrations;

import ji.migration.Migration;
import ji.querybuilder.QueryBuilder;

public class JavaMigration implements SingleMigration {
	
	private final String path; // where migrations are
	
	private final ClassLoader loader;
	
	private final boolean isRevert;
	
	//private final boolean isInclasspath;

	public JavaMigration(String path, ClassLoader loader, boolean isRevert/*, boolean isInclasspath*/) {
		this.path = path.replaceAll("\\\\", ".").replaceAll("/", ".");
		this.loader = loader;
		this.isRevert = isRevert;
		//this.isInclasspath = isInclasspath;
	}

	@Override
	public void migrate(String name, QueryBuilder builder) throws Exception {
		//String path = isInclasspath ? this.path + "." :  "";
		Migration m = getMigration(name);
		if (isRevert) {
			m.revert(builder);
		} else {
			m.migrate(builder);
		}
	}
	
	private Migration getMigration(String name) throws Exception {
		try {
			return (Migration)loader.loadClass(this.path + "." + name).getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException e1) {
			try {
				return (Migration)loader.loadClass(name).getDeclaredConstructor().newInstance();
			} catch (ClassNotFoundException e2) {
				throw new ClassNotFoundException("Migration class not found: " + e1.getMessage() + " OR " + e2.getMessage());
			}
		}
	}
	
}
