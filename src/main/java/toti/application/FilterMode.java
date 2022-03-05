package toti.application;

public enum FilterMode {

	LIKE("%%%s%%"), 
	EQUALS("%s"), 
	STARTS_WITH("%%%s"), 
	ENDS_WITH("%s%%");
	
	private String format;
	
	private FilterMode(String format) {
		this.format = format;
	}
	
	public String getFormat() {
		return format;
	}
	
}
