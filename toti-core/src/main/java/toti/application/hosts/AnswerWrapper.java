package toti.application.hosts;

import toti.application.answers.Answer;

public class AnswerWrapper {

	private final Answer answer;
	private final boolean usePath;
	
	public AnswerWrapper(Answer answer, boolean usePath) {
		this.answer = answer;
		this.usePath = usePath;
	}

	public Answer getAnswer() {
		return answer;
	}

	public boolean usePath() {
		return usePath;
	}
	
	public boolean isUsed() {
		return answer != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answer == null) ? 0 : answer.hashCode());
		result = prime * result + (usePath ? 1231 : 1237);
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
		AnswerWrapper other = (AnswerWrapper) obj;
		if (answer == null) {
			if (other.answer != null) {
				return false;
			}
		} else if (!answer.equals(other.answer)) {
			return false;
		}
		if (usePath != other.usePath) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AnswerWrapper [answer=" + (isUsed() ? answer.hashCode() : "NOT_USED") + ", usePath=" + usePath + "]";
	}

}
