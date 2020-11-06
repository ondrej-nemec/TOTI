package toti.control;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import toti.templating.Template;

public class Html {

	public static Html element(String element) {
		return new Html(element);
	}
	
	private final StringBuilder html = new StringBuilder();
	
	private Html(String element) {
		if (!isTextSave(element)) {
			throw new RuntimeException(String.format("The element must be alphanumeric optionally with '-' or '_'. You give '%s'", element));
		}
		html.append(String.format("$(\"<%s>\")", element));
	}
	
	@Override
	public String toString() {
		return replaceItemValues(html.toString());
	}
	
	public Html attr(String name, Object value) {
		if (!isTextSave(name)) {
			throw new RuntimeException(String.format("The name must be alphanumeric optionally with '-' or '_'. You give '%s'", name));
		}
		html.append(String.format(".attr(\"%s\", \"%s\")", name, Template.escapeVariable(value))); // TODO object to json string
		return this;
	}
	
	public Html prop(String name, Object value) {
		if (!isTextSave(name)) {
			throw new RuntimeException(String.format("The name must be alphanumeric optionally with '-' or '_'. You give '%s'", name));
		}
		html.append(String.format(".prop(\":%s\", \"%s\")", name, Template.escapeVariable(value))); // TODO object to json string
		return this;
	}
	
	public Html text(Object value) {
		html.append(String.format(".html(\"%s\")", Template.escapeVariable(value))); // TODO object to json string
		return this;
	}
	
	public Html html(Object value) {
		html.append(String.format(".html(\"%s\")", value)); // TODO object to json string
		return this;
	}
	
	protected boolean isTextSave(String text) {
		Matcher m = Pattern.compile("^[a-zA-Z0-9-_]+$").matcher(text);
		return m.find();
	}
	
	public static String replaceItemValues(String text) {
		Matcher m = Pattern.compile("(\\$\\{[ ]*)([a-zA-Z0-9-_]+)([ ]*\\})").matcher(text);
		String result = text;
		while(m.find()) {
			result = result.replace(m.group(), "\"+(item." + m.group(2) + ")+\"");
		}
		return result;
	}
	
}
