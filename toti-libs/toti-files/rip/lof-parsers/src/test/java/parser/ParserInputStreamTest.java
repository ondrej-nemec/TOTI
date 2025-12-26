package parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

public class ParserInputStreamTest {
	
	@Test
	public void testNextWorks() throws IOException {		
		char t = 't';
		char f = 'f';
		
		InputStream is = mock(InputStream.class);
		
		ParserInputStream parser = spy(new ParserInputStream(is) {
			@Override
			protected boolean parse(char car) {return false;}
		});
		
		when(parser.parse(t)).thenReturn(true);
		when(parser.parse(f)).thenReturn(false);
		
		when(is.read()).thenReturn((int)t).thenReturn((int)t).thenReturn((int)f);		
		assertTrue(parser.next());
		
		verify(parser, times(2)).parse(t);
		verify(parser, times(1)).parse(f);
		
		when(is.read()).thenReturn(-1);
		assertFalse(parser.next());	
	}

}
