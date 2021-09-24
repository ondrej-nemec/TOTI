package toti.templating.parsing;

public class ParsingNode {
	
	private StringBuilder builder = new StringBuilder();
	
	private ParsingNode parent;
	
	public ParsingNode() {
		this.parent = null;
	}
	
	public ParsingNode(ParsingNode parent) {
		this.parent = parent;
	}
	
	public void add(String text) {
		builder.append(text);
	}
	
	public void add(char text) {
		builder.append(text);
	}
	
	public ParsingNode createChild() {
		return new ParsingNode(this);
	}
	
	public ParsingNode backToParent() {
		parent.add(flush());
		return parent;
	}
	
	public String flush() {
		builder.append("\");");
		return builder.toString();
	}
}
