package toti.templating.parameters;

import toti.templating.Parameter;

public class FormActionParameter implements Parameter {

	@Override
	public String getName() {
		return "action";
	}

	@Override
	public String getCode(String value) {
		return new HrefParameter().getCode(value);
	}

}
