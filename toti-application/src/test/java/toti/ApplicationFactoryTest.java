package toti;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Properties;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.functions.Env;
import ji.common.structures.MapInit;
import ji.env.PropertiesEnv;
import ji.testing.TestCase;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.application.Module;
import toti.application.Task;

@RunWith(JUnitParamsRunner.class)
public class ApplicationFactoryTest implements TestCase {
	
	// TODO test without modules ?
	// TODO test without aliases ?
	// TODO full test with env, manual set, def values
	
	@Test
	public void test() {
		fail("TODO");
	}
	
	@Test
	@Parameters(method="data")
	@Ignore
	public void test(Properties properties, Consumer<ApplicationFactory> setFactory) throws Exception {
		Env env = new PropertiesEnv(properties);
		Env applicationsEnv = mock(Env.class);
		when(applicationsEnv.getModule(any())).thenReturn(env);
		Env baseEnv = mock(Env.class);
		when(baseEnv.getModule(any())).thenReturn(applicationsEnv);
		
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
		
		verify(baseEnv, times(1)).getModule("applications");
		verify(applicationsEnv, times(1)).getModule("hostName");
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
	
	public Object[] data() {
		return new Object[] {
			// default values
			new Object[] {
				MapInit.create().toProperties(),
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
