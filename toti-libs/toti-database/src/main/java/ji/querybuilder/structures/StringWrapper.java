package ji.querybuilder.structures;

public class StringWrapper implements SubSelect {

	private final String string;
	
	public StringWrapper(String string) {
		this.string = string;
	}

	@Override
	public String getSql() {
		return string;
	}

	@Override
	public String createSql() {
		return string;
	}
	
	@Override
	public boolean wrap() {
		return false;
	}
	
}
