package toti.lib.database.querybuilder.builders.parents;

public interface Parametrized<P> {

	P addParameter(String name, Object value);
	
}
