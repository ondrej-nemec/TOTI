import toti.templating.Template;
import toti.templating.parsing2.TagNode;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.LinkedList;
import translator.Translator;
import toti.security.Authorizator;
import toti.templating.TemplateFactory;

public class template implements Template {
	private LinkedList<TagNode> nodes = new LinkedList<>();

	private void write(Object data) {
		nodes.getLast().getBuilder().append(data);
	}

	private void addVariable(String name, Object value) {
		nodes.getLast().getVariables().put(name, value);
	}

	private Object getVariable(String name) {
		return nodes.getLast().getVariables().get(name);
	}

	private Consumer<Map<String, Object>> getBlock(String name) {
		return nodes.getLast().getBlocks().get(name);
	}

	private void addBlock(String name, Consumer<Map<String, Object>> value) {
		nodes.getLast().getBlocks().put(name, value);
	}

	private void initNode(Map<String, Object> variables) {
		Map<String, Object> params = new HashMap<>();
		Map<String, Consumer<Map<String, Object>>> blocks = new HashMap<>();
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

	public long getLastModification() {
		return 0L;
	}

	public String create(TemplateFactory templateFactory, Map<String, Object> variables, Translator translator,
			Authorizator authorizator) throws Exception {
		return create(templateFactory, variables, translator, authorizator, new LinkedList<>()).getBuilder().toString();
	}

	public TagNode create(TemplateFactory templateFactory, Map<String, Object> variables, Translator translator,
			Authorizator authorizator, LinkedList<TagNode> nodes) throws Exception {
		Template layout = null;
		this.nodes = nodes;
		initNode(variables);
		write("<html>");
		write("\n<head>");
		write("\n</head>");
		write("\n<body>");
		write("\n	<h1>");
		Object o1_0 = getVariable("title");
		write(Template.escapeVariable(o1_0));
		write("</h1>");
		write("\n");
		write("\n	<p>");
		Object o2_0 = getVariable("title");
		Object o2_1 = o2_0.getClass().getMethod("length").invoke(o2_0);
		write(Template.escapeVariable(o2_1));
		write("</p>");
		write("\n	<p>");
		Object o3_0 = getVariable("title");
		Object o4_0 = getVariable("title");
		Object o3_1 = o3_0.getClass().getMethod("equals", Object.class).invoke(o3_0, Object.class.cast(o4_0));
		write(Template.escapeVariable(o3_1));
		write("</p>");
		write("\n	<p>");
		Object o5_0 = getVariable("age");
		write(Template.escapeVariable(o5_0));
		write("</p>");
		write("\n	<p>");
		Object o6_0 = getVariable("age");
		Object o6_1 = o6_0.getClass().getMethod("getClass").invoke(o6_0);
		write(Template.escapeVariable(o6_1));
		write("</p>");
		write("\n	<p>");
		Object o7_0 = getVariable("age");
		write(Template.escapeVariable(Integer.class.cast(o7_0)));
		write("</p>");
		write("\n");
		write("\n");
		for (int i = 0; i < 10; i++) {
			System.err.println("#" + i);
		}
		write("");
		write("\n");
		write("\n");
		write("");
		write("\n");
		write("\n</body>");
		write("\n</html>");
		if (layout != null) {
			layout.create(templateFactory, variables, translator, authorizator, this.nodes);
		}
		return flushNode();
	}
}