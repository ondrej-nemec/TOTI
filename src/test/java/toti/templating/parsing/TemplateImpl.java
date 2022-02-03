package toti.templating.parsing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import ji.common.structures.MapInit;
import ji.common.structures.ThrowingConsumer;
import toti.security.Authorizator;
import toti.templating.Template;
import toti.templating.TemplateFactory;
import toti.templating.parsing.structures.TagNode;
import toti.url.MappedUrl;
import ji.translator.Translator;

public class TemplateImpl implements Template {
	
	private LinkedList<TagNode> nodes = new LinkedList<>();
	
	/***/
	
	private void write(Object data) { nodes.getLast().getBuilder().append(data); }
	
	private void addVariable(String name, Object value) { nodes.getLast().getVariables().put(name, value); }
	
	private Object getVariable(String name) { return nodes.getLast().getVariables().get(name); }
	
	private ThrowingConsumer<Map<String, Object>, Exception> getBlock(String name) {return nodes.getLast().getBlocks().get(name);}
	
	private void addBlock(String name, ThrowingConsumer<Map<String, Object>, Exception> value) {nodes.getLast().getBlocks().put(name, value);}
	
	/***/
	
	private void initNode(Map<String, Object> variables) {
		Map<String, Object> params = new HashMap<>();
		Map<String, ThrowingConsumer<Map<String, Object>, Exception>> blocks = new HashMap<>();
		if (nodes.size() > 0) {
			params.putAll(nodes.getLast().getVariables());
			blocks.putAll(nodes.getLast().getBlocks());
		}
		if (variables != null) {
			params.putAll(variables);
		}
		nodes.add(new TagNode(params, blocks));
	}

	private TagNode flushNode() {
		TagNode node = nodes.removeLast();
		if (nodes.size() > 0) {
			write(node.getBuilder().toString());
		}
		return node;
	}

	/********/
	
	private boolean is;
	
	public TemplateImpl(boolean is) {
		this.is = is;
	}
	
	public TemplateImpl() {
		is = false;
	}
	
	/********/
	
	@Override
	public long getLastModification() {
		return 0;
	}
	
	public String _create(TemplateFactory templateFactory, Map<String, Object> variables, Translator translator,
			Authorizator authorizator, LinkedList<TagNode> nodes, MappedUrl current) throws Exception {
		TemplateImpl layout = null; // TODO template
		this.nodes = nodes;
		initNode(variables);
		
		if (!is) {
			layout = new TemplateImpl(true);
			addBlock("title", (v)->{
				initNode(v);
				write("---- TITLE ---");
				flushNode();
			});
			addBlock("content", (v)->{
				initNode(v);
				write("html another html ..........");
				
				int i = 0;
				addVariable("i", 0);
				
				addBlock("blockName", (blockNameParams)->{
					initNode(blockNameParams);
					write("block something -> " + getVariable("aa") + " " + i);
					addVariable("inBlock", true);
					write("\nIn Block var " + getVariable("inBlock"));
					flushNode();
				});
				write("\nFOR");
				Object o1 = getVariable("limit");
				for (int a = 0; a < Integer.class.cast(o1); a++) {
					initNode(null);
					addVariable("a", a);
					write("\n");
					write("-----\n");
					getBlock("blockName").accept(new MapInit<String, Object>().append("aa", "aa" + a).toMap());
					write("\nOut Block var " + getVariable("inBlock"));
					flushNode();
				}
				write("\nEND\n");
			//	System.err.println("**-- " + this.nodes);
				
				flushNode();
			});
		} else {
			write("MAIN\n");
		//	System.err.println(this.nodes);
		//	System.err.println(nodes);
			
			Object o = getVariable("length");
			write(Integer.class.cast(o) > 12 ? "red" : "blue");
			// {{ ${leng} > 12 ? "red" : "blue" }}
			getBlock("title").accept(new HashMap<>());
			write("\n");
			getBlock("content").accept(new HashMap<>());
			write("\n-->END");
			System.err.println(getBlock("blockName"));
		}
		
		if (layout != null) {
			return layout._create(templateFactory, variables, translator, authorizator, this.nodes, current);
		}
		return flushNode().getBuilder().toString();
	}

	public static void main(String[] args) {
		TemplateImpl template = new TemplateImpl();
		try {
			System.out.println(template.create(null, null, null, null, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
