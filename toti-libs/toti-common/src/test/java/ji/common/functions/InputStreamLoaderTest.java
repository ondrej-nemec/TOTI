package ji.common.functions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class InputStreamLoaderTest {

	@Test
	public void testConstructorForFilesThrowIfNoFileInDir() throws IOException {
		FileNotFoundException ex = assertThrows(FileNotFoundException.class, ()->{
			InputStreamLoader.createInputStream(getClass(), "----/not-existing.properties");
		});
		assertNotNull(ex);
	}

	@ParameterizedTest
	@ValueSource(strings={"functions/env","tests/functions/env"})
	public void testConstructorForFilesWorksForClasspathAndPathOnly(String path) throws IOException {
		InputStreamLoader.createInputStream(getClass(), path + "/app.properties");
		assertTrue(true);
	}

}