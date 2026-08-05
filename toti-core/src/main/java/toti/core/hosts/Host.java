package toti.core.hosts;

import java.util.HashMap;
import java.util.Map;

import toti.core.answers.Answer;

public class Host {
	
	private final Map<String, Answer> answers;
	private Answer defAnswer;
	
	public Host() {
		this.answers = new HashMap<>();
	}
	
	public void add(String path, Answer answer) {
		if (path == null) {
			defAnswer = answer;
			return;
		}
		answers.put(path, answer);
	}

	public void remove(String path) {
		if (path == null) {
			defAnswer = null;
			return;
		}
		answers.remove(path);
	}
	
	public AnswerWrapper get(String path) {
		if (path != null && path.length() > 0 && answers.containsKey(path)) {
			return new AnswerWrapper(answers.get(path), true);
		}
		return new AnswerWrapper(defAnswer, false);
	}
	
}
