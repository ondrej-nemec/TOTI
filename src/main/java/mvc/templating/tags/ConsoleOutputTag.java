package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class ConsoleOutputTag implements Tag{

	@Override
	public String getName() {
		return "console";
	}

	@Override
	public String getStartingCode(Map<String, String> params) {
		return "System.out.println(\"" + params.get("value") + "\");";
	}

	@Override
	public String getClosingCode(Map<String, String> params) {
		// TODO Auto-generated method stub
		return "";
	}

}
