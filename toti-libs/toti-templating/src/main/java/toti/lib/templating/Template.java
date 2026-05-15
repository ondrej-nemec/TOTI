package toti.lib.templating;

import java.util.LinkedList;
import java.util.Map;

import toti.lib.templating.parsing.structures.TagNode;

public interface Template {

	long getLastModification();

	default String create(Map<String, Object>variables) throws Exception {
		return _create(variables, new LinkedList<>(), 0);
	}

	String _create(Map<String, Object>variables, LinkedList<TagNode> nodes, int parent) throws Exception;

}
