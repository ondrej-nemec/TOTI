package toti.application.hosts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toti.application.answers.Answer;

public class Host {
	
	private final Map<String, Answer> answers;
	private Answer defAnswer;
	
	public Host() {
		this.answers = new HashMap<>();
	}
	
	public void add(List<String> paths, Answer answer) {
		if (paths == null) {
			defAnswer = answer;
			return;
		}
		paths.forEach(p->answers.put(p, answer));
	}

	public void remove(List<String> paths) {
		if (paths == null) {
			defAnswer = null;
			return;
		}
		paths.forEach(p->answers.remove(p));
	}
	
	public AnswerWrapper get(String path) {
		if (path != null && path.length() > 0 && answers.containsKey(path)) {
			return new AnswerWrapper(answers.get(path), true);
		}
		return new AnswerWrapper(defAnswer, false);
	}
	
}
