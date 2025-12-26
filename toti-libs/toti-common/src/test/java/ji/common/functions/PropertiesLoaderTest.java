package ji.common.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import org.junit.jupiter.params.provider.ValueSource;

public class PropertiesLoaderTest {

	@Test
	public void testConstructorForFilesThrowIfNoFileInDir() throws FileNotFoundException, IOException {
		IOException ex = assertThrows(IOException.class, ()->{
			PropertiesLoader.loadProperties("functions/env/env/not-existing.properties");
		});
		assertNotNull(ex);
	}

	@ParameterizedTest
	@ValueSource(strings={"tests/functions/env","functions/env"})
	public void testConstructorWorks(String path) throws FileNotFoundException, IOException {
		Properties prop = PropertiesLoader.loadProperties(path + "/app.properties");
		assertEquals("DEV", prop.get("app.mode"));
	}

}
