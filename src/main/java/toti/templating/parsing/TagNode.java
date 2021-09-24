package toti.templating.parsing;

import java.util.Map;

import common.structures.ThrowingConsumer;

public class TagNode {

	protected final StringBuilder builder;
	protected final Map<String, Object> variables;
	protected final Map<String, ThrowingConsumer<Map<String, Object>, Exception>> blocks;
	
	public TagNode( Map<String, Object> variables, Map<String, ThrowingConsumer<Map<String, Object>, Exception>> blocks) {
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
	
	public Map<String,ThrowingConsumer<Map<String, Object>, Exception>> getBlocks() {
		return blocks;
	}

	@Override
	public String toString() {
		return String.format("NODE: %s, %s, %s", builder, variables, blocks);
	}
}
