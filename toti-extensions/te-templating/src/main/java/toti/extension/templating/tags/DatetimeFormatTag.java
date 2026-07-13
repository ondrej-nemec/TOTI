package toti.extension.templating.tags;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import toti.core.answers.request.Identity;
import toti.core.extensions.Translator;
import toti.lib.templating.Tag;
import toti.lib.templating.TagVariableMode;
import toti.lib.templating.TemplateException;

public class DatetimeFormatTag implements Tag {

	@Override
	public TagVariableMode getMode(String name) {
		if ("type".equalsIgnoreCase(name)) {
			return TagVariableMode.STRING;
		}
		if ("format".equalsIgnoreCase(name)) {
			return TagVariableMode.STRING;
		}
		return TagVariableMode.CODE;
	}

	@Override
	public String getName() {
		return "format";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return getNotPairCode(params);
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		checkParameter(params, "value");
		checkParameter(params, "format");
		
		return String.format(
			"write(escapeHtml(new DictionaryValue(%s)"
			+ ".getValue(%s.class).format(%s.ofPattern("
				+ Identity.class.getCanonicalName()
				+ ".class.cast(getVariable(\"totiIdentity\"))"
				+ ".getScope("
				+ Translator.class.getCanonicalName()
				+ ")"
				+ ".translate(\"%s\")"
			+ "))));",
			params.get("value"),
			ZonedDateTime.class.getName(), // getClassName(params.get("type")),
			DateTimeFormatter.class.getName(),
			params.get("format")
		);
	}

	private void checkParameter(Map<String, String> params, String param) {
		if (!params.containsKey(param)) {
			throw new TemplateException(String.format("Tag 'format': missing parameter '%s'", param));
		}
	}
/*
	private String getClassName(String type) {
		switch (type) {
			case "datetime": return LocalDateTime.class.getName();
			case "date": return LocalDate.class.getName();
			case "time": return LocalTime.class.getName();
		}
		throw new TemplateException("Tag 'format': unsupported type '" + type + "'");
	}
*/
}
