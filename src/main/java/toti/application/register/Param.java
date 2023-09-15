package toti.application.register;

import java.util.HashMap;
import java.util.Map;

import ji.socketCommunication.http.HttpMethod;
import toti.url.MappedAction;

public class Param {

	private String text;
	
	private Map<HttpMethod, MappedAction> actions = new HashMap<>();
	
	private Map<Param, Param> childs = new HashMap<>();
	
	public Param(String text) {
		this.text = text;
	}
	
	public Param addChild(String uri) {
		Param param = new Param(uri);
		Param child = childs.get(param);
		if (child == null) {
			child = new Param(uri);
			childs.put(param, child);
		}
		return child;
	}
	
	public void addAction(HttpMethod method, MappedAction action) {
		this.actions.put(method, action);
	}
	
	public Param getChild(String uri) {
		return childs.get(new Param(uri));
	}
	
	public MappedAction getAction(HttpMethod method) {
		return actions.get(method);
	}
	
	public boolean isAction() {
		return actions.size() == 0;
	}
	
	public boolean isParam() {
		return text == null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Param other = (Param) obj;
		if (text == null) {
			return true;
		}
		return text.equals(other.text);
	}
	
	
	
}
