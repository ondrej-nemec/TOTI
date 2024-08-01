package ji.querybuilder.structures;

import ji.querybuilder.enums.Join;

public class Joining {
	
	private final SubSelect builder;
	private final String alias;
	private final Join join;
	private final String on;
	
	public Joining(SubSelect builder, String alias, Join join, String on) {
		this.builder = builder;
		this.alias = alias;
		this.join = join;
		this.on = on;
	}

	public SubSelect getBuilder() {
		return builder;
	}

	public String getAlias() {
		return alias;
	}

	public Join getJoin() {
		return join;
	}

	public String getOn() {
		return on;
	}

}
