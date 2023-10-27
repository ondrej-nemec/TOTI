package ext.grid;

public enum FilterMode {

	LIKE("%%%s%%", null), 
	EQUALS("%s", null), 
	STARTS_WITH("%s%%", null), 
	ENDS_WITH("%%%s", null),
    MORE_OR_EQUALS("%s", ">="),
    MORE("%s", ">"),
    LESS_OR_EQUALS("%s", "<="),
    LESS("%s", "<")
    ;
	
	private String format;
    private String operand;
	
	private FilterMode(String format, String operand) {
		this.format = format;
        this.operand = operand;
	}
	
	public String getOperand() {
		return operand;
	}
	
	public String getFormat() {
		return format;
	}
	
}
