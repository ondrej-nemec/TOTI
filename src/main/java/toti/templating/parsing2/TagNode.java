package toti.templating.parsing2;

import java.util.Map;
import java.util.function.Consumer;

public class TagNode {

	protected final StringBuilder builder;
	protected final Map<String, Object> variables;
	protected final Map<String, Consumer<Map<String, Object>>> blocks;
	
	public TagNode( Map<String, Object> variables, Map<String, Consumer<Map<String, Object>>> blocks) {
		this.builder = new StringBuilder();
		this.variables = variables;
		this.blocks = blocks;
	}
	
	public StringBuilder getBuilder() {
		return builder;
	}
	
	public Map<String, Object> getVariables() {
		return variables;
	}
	
	public Map<String, Consumer<Map<String, Object>>> getBlocks() {
		return blocks;
	}

	@Override
	public String toString() {
		return String.format("NODE: %s, %s, %s", builder, variables, blocks);
	}
}
