package toti.core.hosts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.mock;

import toti.core.answers.Answer;

public class HostsTest {

	@ParameterizedTest
	@MethodSource("dataOneAnswer")
	public void testOneAnswer(String hostName, String path) {
		Answer answer = mock(Answer.class);
		
		Hosts hosts = new Hosts();
		hosts.add(answer, null, null);
		
		AnswerWrapper expected = new AnswerWrapper(answer, false);
		
		assertEquals(expected, hosts.get(hostName, path));
	}
	
	public static Object[] dataOneAnswer() {
		return new Object[] {
			new Object[] { null, null },
			new Object[] { "", "" },
			new Object[] { null, "some-path-1" },
			new Object[] { "some-hostname-1", null },
			new Object[] { "some-hostname-1", "some-path-1" }
		};
	}

	@ParameterizedTest
	@MethodSource
	public void testOnlyHostnames(String hostName, String path, Integer result) {
		Answer answer1 = mock(Answer.class);
		Answer answer2 = mock(Answer.class);
		
		Hosts hosts = new Hosts();
		hosts.add(answer1, "h1", null);
		hosts.add(answer2, "h2", null);
		
		Answer answer = null;
		if (result != null) {
			switch (result) {
				case 1 -> answer = answer1;
				case 2 -> answer = answer2;
			}
		}
		
		AnswerWrapper expected = new AnswerWrapper(answer, false);
		assertEquals(expected, hosts.get(hostName, path));
	}
	
	public static Object[] testOnlyHostnames() {
		return new Object[] {
			new Object[] { null, null, null },
			new Object[] { "", "", null },
			new Object[] { null, "some-path-1", null },
			new Object[] { "not-existing", "", null },
			new Object[] { "h1", null, 1 },
			new Object[] { "h2", null, 2 },
			new Object[] { "h1", "something", 1 },
			new Object[] { "h1", "else", 1 }
		};
	}

	@ParameterizedTest
	@MethodSource("dataOnlyPaths")
	public void testOnlyPaths(String hostName, String path, Integer result, boolean usePath) {
		Answer answer1 = mock(Answer.class);
		Answer answer2 = mock(Answer.class);
		
		Hosts hosts = new Hosts();
		hosts.add(answer1, null, "p1");
		hosts.add(answer2, null, "p2");
		
		Answer answer = null;
		if (result != null) {
			switch (result) {
				case 1 -> answer = answer1;
				case 2 -> answer = answer2;
			}
		}
		
		AnswerWrapper expected = new AnswerWrapper(answer, usePath);
		assertEquals(expected, hosts.get(hostName, path));
	}
	
	public static Object[] dataOnlyPaths() {
		return new Object[] {
			new Object[] { null, null, null, false },
			new Object[] { "", "", null, false },
			new Object[] { null, "not-existing", null, false },
			new Object[] { null, "p1", 1, true },
			new Object[] { null, "p2", 2, true },
			new Object[] { "something", "p1", 1, true },
			new Object[] { "else", "p1", 1, true }
		};
	}

	@ParameterizedTest
	@MethodSource("dataCombined")
	public void testCombined(String hostName, String path, Integer result, boolean usePath) {
		Answer answer1 = mock(Answer.class);
		Answer answer2 = mock(Answer.class);
		Answer answer3 = mock(Answer.class);
		
		Hosts hosts = new Hosts();
		hosts.add(answer1, "h1", "p1");
		hosts.add(answer2, null, "p2");
		hosts.add(answer3, "h2", null);
		
		Answer answer = null;
		if (result != null) {
			switch (result) {
				case 1 -> answer = answer1;
				case 2 -> answer = answer2;
				case 3 -> answer = answer3;
			}
		}

		AnswerWrapper expected = new AnswerWrapper(answer, usePath);
		assertEquals(expected, hosts.get(hostName, path));
	}
	
	public static Object[] dataCombined() {
		return new Object[] {
			new Object[] { null, null, null, false },
			new Object[] { "not-existing", null, null, false },
			new Object[] { null, "not-existing", null, false },
			
			new Object[] { "h1", null, null, false },
			new Object[] { "h1", "p1", 1, true },
			new Object[] { "h1", "not-existing", null, false },
			
			new Object[] { null, "p2", 2, true },
			new Object[] { "something", "p2", 2, true },
			
			new Object[] { "h2", null, 3, false },
			new Object[] { "h2", "something", 3, false },
			new Object[] { "h2", "p1", 3, false }
		};
	}
}
