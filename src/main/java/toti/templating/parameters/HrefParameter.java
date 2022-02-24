package toti.templating.parameters;

import ji.common.exceptions.LogicException;
import toti.templating.Parameter;
import toti.url.Link;

public class HrefParameter implements Parameter {

	@Override
	public String getName() {
		return "href";
	}

	@Override
	public String getCode(String value) {
        String[] values = value.split(":");
        Link link = Link.get();
        if (values.length == 1) {
             throw new LogicException("HREF parameter required format: '<[controller]>:[method]<:[parameter]>{n}'");
             //code.append(String.format(".setMethod(\"%s\")", values[0]));
        }
        if (!values[0].isEmpty()) {
             link.setController(values[0]);
        }
        link.setMethod(values[1]);
        for (int i = 2; i < values.length; i++) {
             link.addUrlParam(values[i]);
        }
        return "\" + " + link.create() + " + \"";
    }

}
