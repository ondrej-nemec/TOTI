package toti.control;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import toti.control.inputs.Hidden;
import toti.control.inputs.Input;
import toti.security.Authenticator;
import toti.security.Identity;

public class Form implements Control {

	private String bindUrl = null;
	private String bindMethod = "get";
	private final boolean editable;
	private String onBindFailure;
	private String beforeBind;
	private String afterBind;
	private String beforePrint;
	private String afterPrint;
	private String beforeSubmit;
	private String afterSubmit;
	
	private final String formAction;
	private String formMethod = "get";

	private final List<Map<String, Object>> fields;
	
	public Form(String action, boolean editable) {
		this.formAction = action;
		this.fields = new LinkedList<>();
		this.editable = editable;
	}
	
	public void setCsrfSecured(Identity identity) {
		fields.add(
			Hidden.input(Authenticator.CSRF_TOKEN_PARAMETER)
			.setDefaultValue(identity.getCsrfToken())
			.getInputSettings()
		);
	}
	
	public Form addInput(Input input) {
		fields.add(input.getInputSettings());
		return this;
	}

	public Form setBindUrl(String bindUrl) {
		this.bindUrl = bindUrl;
		return this;
	}

	public Form setBindMethod(String bindMethod) {
		this.bindMethod = bindMethod;
		return this;
	}
	
	public Form setOnBindFailure(String onBindFailure) {
		this.onBindFailure = onBindFailure;
		return this;
	}
	
	public Form setFormMethod(String formMethod) {
		this.formMethod = formMethod;
		return this;
	}
	
	public Form setAfterBind(String afterBind) {
		this.afterBind = afterBind;
		return this;
	}
	
	public Form setAfterPrint(String afterPrint) {
		this.afterPrint = afterPrint;
		return this;
	}
	
	public Form setBeforeBind(String beforeBind) {
		this.beforeBind = beforeBind;
		return this;
	}

	@Override
	public Map<String, Object> toJson() {
		Map<String, Object> json = new HashMap<>();
		json.put("action", formAction);
		json.put("method", formMethod);
		json.put("fields", fields);
		json.put("editable", editable);
		if (afterPrint != null) {
			json.put("afterPrint", afterPrint);
		}
		if (bindUrl != null) {
			Map<String, Object> bind = new HashMap<>();
			json.put("bind", bind);
			bind.put("url", bindUrl);
			bind.put("method", bindMethod);
			bind.put("onFailure", onBindFailure);
			if (beforeBind != null) {
				bind.put("beforeBind", beforeBind);
			}
			if (afterBind != null) {
				bind.put("afterBind", afterBind);
			}
		}
		return json;
	}
	
	@Override
	public String toString() {
		return toJs();
	}

	@Override
	public String getType() {
		return "Form";
	}
	
}
