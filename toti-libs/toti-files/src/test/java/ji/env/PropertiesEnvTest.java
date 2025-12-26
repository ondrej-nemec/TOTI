package ji.env;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import ji.common.functions.Env;

public class PropertiesEnvTest {
	
	@Test
	public void testSubEnv() {
		Properties properties = new Properties();
		properties.put("key", "value");
		properties.put("submodule.key2", "submodule-value");
		
		Env env = new PropertiesEnv(properties);
		assertEquals("value", env.get("key"));
		assertEquals("submodule-value", env.get("submodule.key2"));
		assertNull(env.get("key2"));
		
		Env sub = env.getModule("submodule");
		assertEquals("submodule-value", sub.get("key2"));
		assertNull(sub.get("submodule.key2"));
		assertNull(sub.get("key"));
	}
	
	@Test
	public void testConstructorForFilesThrowIfNoFileInDir() {
		IOException expectzed = assertThrows(IOException.class, ()->{
			PropertiesEnv.create("functions/env/not-existing.properties");
		});
		assertNotNull(expectzed);
	}
	
	@ParameterizedTest
	@ValueSource(strings={"tests/functions/env","functions/env"})
	public void testConstructorForFilesWorksForClasspathAndPathOnly(String path) throws FileNotFoundException, IOException {
		PropertiesEnv.create(path + "/app.properties");
		assertTrue(true);
	}
	
	@ParameterizedTest
	@MethodSource("dataConstructorForFileFindCorrectProperties")
	public void testConstructorForFileFindCorrectProperties(final String subDir)
			throws FileNotFoundException, IOException {
		Env e = PropertiesEnv.create("functions/env/env." + subDir + ".properties");
		assertEquals("value", e.get("key"));
	}
	
	public static Collection<Object[]> dataConstructorForFileFindCorrectProperties() {
		return Arrays.asList(
				new Object[] {"prod"},
				new Object[] {"dev"},
				new Object[] {"test"}
		);
	}

}
