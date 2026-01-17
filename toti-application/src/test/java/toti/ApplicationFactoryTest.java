package toti;

import java.util.Arrays;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static toti.lib.common.tests.TestCase.assertEquals;
import static toti.lib.common.tests.TestCase.consumer;

import toti.application.Module;
import toti.application.Task;
import toti.files.env.Env;

public class ApplicationFactoryTest {
	
	// TODO test without modules ?
	// TODO test without aliases ?
	// TODO full test with env, manual set, def values
	
	@Test
	public void test() {
		fail("TODO");
	}
	
	@ParameterizedTest
	@MethodSource("data")
	@Disabled
	public void test(Env env, Consumer<ApplicationFactory> setFactory) throws Exception {
		Env applicationsEnv = mock(Env.class);
		when(applicationsEnv.getSection(any())).thenReturn(env);
		Env baseEnv = mock(Env.class);
		when(baseEnv.getSection(any())).thenReturn(applicationsEnv);
		
		Logger totiLogger = mock(Logger.class);
		
		ApplicationFactory factory = new ApplicationFactory(
			"appId", env, "charset", Arrays.asList("hostName"), Arrays.asList("alias1", "alias2")
		);
		setFactory.accept(factory);
		
		Task task11 = mock(Task.class);
		Task task12 = mock(Task.class);
		Task task21 = mock(Task.class);
		
		Module module1 = mock(Module.class);
		when(module1.getName()).thenReturn("module1");
		when(module1.initInstances(any(), any(), any())).thenReturn(Arrays.asList(task11, task12));
		Module module2 = mock(Module.class);
		when(module2.getName()).thenReturn("module2");
		when(module2.initInstances(any(), any(), any())).thenReturn(Arrays.asList(task21));
		
		Application application = factory.create(Arrays.asList(module1, module2), totiLogger);
		
		verify(baseEnv, times(1)).getSection("applications");
		verify(applicationsEnv, times(1)).getSection("hostName");
		verifyNoMoreInteractions(baseEnv, applicationsEnv);
		
		assertNotNull(application.getLink());
		assertNotNull(application.getRegister());
		assertNotNull(application.getRequestAnswer());
		assertEquals(Arrays.asList("alias1", "alias"), application.getPaths());
		
		verify(module1).getName();
		verify(module2).getName();
		
		// TODO verify answers
		// TODO verify extensions
		// TODO verify env db: type, url, external, schema-name, password, pool-size
	}
	
	public static Object[] data() {
		return new Object[] {
			// default values
			new Object[] {
				Env.empty(),
				consumer(ApplicationFactory.class, (f)->{})
			},
			/*// TODO
			new Object[] {
				MapInit.create().toProperties(),
				consumer(ApplicationFactory.class, (f)->{
					
				}),
				false
			},
			// TODO
			new Object[] {
				MapInit.create().toProperties(),
				consumer(ApplicationFactory.class, (f)->{
					
				}),
				false
			}*/
		};
	}
	

}
