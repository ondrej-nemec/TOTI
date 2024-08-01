package ji.common.functions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class EnvTest {
	
	@Test
	public void testSubEnv() {
		Properties properties = new Properties();
		properties.put("key", "value");
		properties.put("submodule.key2", "submodule-value");
		
		Env env = new Env(properties);
		assertEquals("value", env.get("key"));
		assertEquals("submodule-value", env.get("submodule.key2"));
		assertNull(env.get("key2"));
		
		Env sub = env.getModule("submodule");
		assertEquals("submodule-value", sub.get("key2"));
		assertNull(sub.get("submodule.key2"));
		assertNull(sub.get("key"));
	}
	
	@Test(expected = IOException.class)
	public void testConstructorForFilesThrowIfNoFileInDir() throws FileNotFoundException, IOException {
		new Env("functions/env/not-existing.properties");
	}
	
	@Test
	@Parameters({"tests/functions/env","functions/env"})
	public void testConstructorForFilesWorksForClasspathAndPathOnly(String path) throws FileNotFoundException, IOException {
		new Env(path + "/app.properties");
		assertTrue(true);
	}
	
	@Test
	@Parameters
	public void testConstructorForFileFindCorrectProperties(final String subDir)
			throws FileNotFoundException, IOException {
		Env e = new Env("functions/env/env." + subDir + ".properties");
		assertEquals("value", e.getProperties().getProperty("key"));
	}
	
	public Collection<Object[]> parametersForTestConstructorForFileFindCorrectProperties() {
		return Arrays.asList(
				new Object[] {"prod"},
				new Object[] {"dev"},
				new Object[] {"test"}
		);
	}

}
