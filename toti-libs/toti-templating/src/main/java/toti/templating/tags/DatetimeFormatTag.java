package toti.templating.tags;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;
import toti.templating.TemplateException;

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
		// {value=getVariable(()->{Object o4_0=getVariable("detail");Object o4_1=null;try{o4_1=o4_0.getClass().getMethod("get",java.lang.String.class).invoke(o4_0,"time");}catch(NoSuchMethodException e){o4_1=o4_0.getClass().getMethod("get",Object.class).invoke(o4_0,"time");}return o4_1;})}
		/*
		write(Template.escapeHtml(new DictionaryValue(getVariable(() -> {
			Object o4_0 = getVariable("detail");
			Object o4_1 = null;
			try {
				o4_1 = o4_0.getClass().getMethod("get", java.lang.String.class).invoke(o4_0, "time");
			} catch (NoSuchMethodException e) {
				o4_1 = o4_0.getClass().getMethod("get", Object.class).invoke(o4_0, "time");
			}
			return o4_1;
		})).getValue(LocalDateTime.class).format(DateTimeFormatter.ofPattern(
			// "yyyy-MM-dd HH:mm"
			"dd.MM. yyyy HH:mm"
		))));
		*/
		checkParameter(params, "value");
	//	checkParameter(params, "type");
		checkParameter(params, "format");
		
		return String.format(
			"write(Template.escapeHtml(new DictionaryValue(%s)"
			+ ".getValue(%s.class).format(%s.ofPattern("
				+ "container.translate(\"%s\")"
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
