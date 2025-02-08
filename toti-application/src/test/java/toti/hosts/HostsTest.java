package toti.hosts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.answers.Answer;

@RunWith(JUnitParamsRunner.class)
public class HostsTest {

	@Test
	@Parameters(method="dataOneAnswer")
	public void testOneAnswer(String hostName, String path) {
		Answer answer = mock(Answer.class);
		
		Hosts hosts = new Hosts();
		hosts.add(answer, null, null);
		
		AnswerWrapper expected = new AnswerWrapper(answer, false);
		
		assertEquals(expected, hosts.get(hostName, path));
	}
	
	public Object[] dataOneAnswer() {
		return new Object[] {
			new Object[] { null, null },
			new Object[] { new LinkedList<>(), new LinkedList<>() },
			new Object[] { "", "" },
			new Object[] { null, "some-path-1" },
			new Object[] { "some-hostname-1", null },
			new Object[] { "some-hostname-1", "some-path-1" }
		};
	}

	@Test
	@Parameters(method="dataOnlyHostnames")
	public void testOnlyHostnames(String hostName, String path, Integer result) {
		Answer answer1 = mock(Answer.class);
		Answer answer2 = mock(Answer.class);
		
		Hosts hosts = new Hosts();
		hosts.add(answer1, Arrays.asList("h11", "h12"), null);
		hosts.add(answer2, Arrays.asList("h21", "h22"), null);
		
		Answer answer = null;
		if (result != null) {
			switch (result) {
				case 1: answer = answer1; break;
				case 2: answer = answer2; break;
			}
		}
		
		AnswerWrapper expected = new AnswerWrapper(answer, false);
		assertEquals(expected, hosts.get(hostName, path));
	}
	
	public Object[] dataOnlyHostnames() {
		return new Object[] {
			new Object[] { null, null, null },
			new Object[] { "", "", null },
			new Object[] { null, "some-path-1", null },
			new Object[] { "not-existing", "", null },
			new Object[] { "h11", null, 1 },
			new Object[] { "h12", null, 1 },
			new Object[] { "h22", null, 2 },
			new Object[] { "h22", null, 2 },
			new Object[] { "h12", "something", 1 },
			new Object[] { "h12", "else", 1 }
		};
	}

	@Test
	@Parameters(method="dataOnlyPaths")
	public void testOnlyPaths(String hostName, String path, Integer result, boolean usePath) {
		Answer answer1 = mock(Answer.class);
		Answer answer2 = mock(Answer.class);
		
		Hosts hosts = new Hosts();
		hosts.add(answer1, null, Arrays.asList("p11", "p12"));
		hosts.add(answer2, null, Arrays.asList("p21", "p22"));
		
		Answer answer = null;
		if (result != null) {
			switch (result) {
				case 1: answer = answer1; break;
				case 2: answer = answer2; break;
			}
		}
		
		AnswerWrapper expected = new AnswerWrapper(answer, usePath);
		assertEquals(expected, hosts.get(hostName, path));
	}
	
	public Object[] dataOnlyPaths() {
		return new Object[] {
			new Object[] { null, null, null, false },
			new Object[] { "", "", null, false },
			new Object[] { null, "not-existing", null, false },
			new Object[] { null, "p11", 1, true },
			new Object[] { null, "p12", 1, true },
			new Object[] { null, "p22", 2, true },
			new Object[] { null, "p22", 2, true },
			new Object[] { "something", "p12", 1, true },
			new Object[] { "else", "p12", 1, true }
		};
	}

	@Test
	@Parameters(method="dataCombined")
	public void testCombined(String hostName, String path, Integer result, boolean usePath) {
		Answer answer1 = mock(Answer.class);
		Answer answer2 = mock(Answer.class);
		Answer answer3 = mock(Answer.class);
		
		Hosts hosts = new Hosts();
		hosts.add(answer1, Arrays.asList("h11", "h12"), Arrays.asList("p11", "p12"));
		hosts.add(answer2, null, Arrays.asList("p21", "p22"));
		hosts.add(answer3, Arrays.asList("h21", "h22"), null);
		
		Answer answer = null;
		if (result != null) {
			switch (result) {
				case 1: answer = answer1; break;
				case 2: answer = answer2; break;
				case 3: answer = answer3; break;
			}
		}

		AnswerWrapper expected = new AnswerWrapper(answer, usePath);
		assertEquals(expected, hosts.get(hostName, path));
	}
	
	public Object[] dataCombined() {
		return new Object[] {
			new Object[] { null, null, null, false },
			new Object[] { "not-existing", null, null, false },
			new Object[] { null, "not-existing", null, false },
			
			new Object[] { "h11", null, null, false },
			new Object[] { "h11", "p11", 1, true },
			new Object[] { "h11", "not-existing", null, false },
			
			new Object[] { null, "p21", 2, true },
			new Object[] { "something", "p21", 2, true },
			
			new Object[] { "h21", null, 3, false },
			new Object[] { "h21", "something", 3, false },
			new Object[] { "h21", "p11", 3, false }
		};
	}
}
