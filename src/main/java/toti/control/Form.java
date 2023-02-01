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

	private String placeholderUrl = null;
	private String placeholderMethod = "get";
	
	private final boolean editable;
	private String onBindFailure;
	private String onPlaceholderLoadFailure;
	private String beforeBind;
	private String afterBind;
	private String beforePlaceholderLoad;
	private String afterPlaceholderLoad;
	private String beforeRender;
	private String afterRender;
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
	
	public Form setAfterRender(String afterRender) {
		this.afterRender = afterRender;
		return this;
	}
	
	public Form setBeforeBind(String beforeBind) {
		this.beforeBind = beforeBind;
		return this;
	}
	
	public Form setBeforeRender(String beforeRender) {
		this.beforeRender = beforeRender;
		return this;
	}

	public Form setAfterSubmit(String afterSubmit) {
		this.afterSubmit = afterSubmit;
		return this;
	}
	
	public Form setBeforeSubmit(String beforeSubmit) {
		this.beforeSubmit = beforeSubmit;
		return this;
	}
	
	public Form setPlaceholderUrl(String placeholderUrl) {
		this.placeholderUrl = placeholderUrl;
		return this;
	}
	
	public Form setPlaceholderMethod(String placeholderMethod) {
		this.placeholderMethod = placeholderMethod;
		return this;
	}
	
	public Form setOnPlaceholderLoadFailure(String onPlaceholderLoadFailure) {
		this.onPlaceholderLoadFailure = onPlaceholderLoadFailure;
		return this;
	}
	
	public Form setAfterPlaceholderLoad(String afterPlaceholderLoad) {
		this.afterPlaceholderLoad = afterPlaceholderLoad;
		return this;
	}
	
	public Form setBeforePlaceholderLoad(String beforePlaceholderLoad) {
		this.beforePlaceholderLoad = beforePlaceholderLoad;
		return this;
	}
	
	@Override
	public Map<String, Object> toJson() {
		Map<String, Object> json = new HashMap<>();
		json.put("action", formAction);
		json.put("method", formMethod);
		json.put("fields", fields);
		json.put("editable", editable);
		if (afterRender != null) {
			json.put("afterRender", afterRender);
		}
		if (beforeRender != null) {
			json.put("beforeRender", beforeRender);
		}
		if (beforeSubmit != null) {
			json.put("beforeSubmit", beforeSubmit);
		}
		if (afterSubmit != null) {
			json.put("afterSubmit", afterSubmit);
		}
		if (bindUrl != null) {
			Map<String, Object> bind = new HashMap<>();
			json.put("bind", bind);
			bind.put("url", bindUrl);
			bind.put("method", bindMethod);
			bind.put("onFailure", onBindFailure);
			if (beforeBind != null) {
				bind.put("before", beforeBind);
			}
			if (afterBind != null) {
				bind.put("after", afterBind);
			}
		}
		if (placeholderUrl != null) {
			Map<String, Object> placeholder = new HashMap<>();
			json.put("placeholder", placeholder);
			placeholder.put("url", placeholderUrl);
			placeholder.put("method", placeholderMethod);
			placeholder.put("onFailure", onPlaceholderLoadFailure);
			if (beforePlaceholderLoad != null) {
				placeholder.put("before", beforePlaceholderLoad);
			}
			if (afterPlaceholderLoad != null) {
				placeholder.put("after", afterPlaceholderLoad);
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
