package multimedia.audio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.jupiter.api.Test;

import multimedia.AudioInputStreamFactory;
import multimedia.audio.AudioLoader;

public class AudioLoaderTest {
	
	private final AudioInputStream stream;
	private final AudioLoader loader;
	
	public AudioLoaderTest() {
		this.stream = mock(AudioInputStream.class);
		this.loader = new AudioLoader(stream);
	}
	
	@Test
	public void testLoadWorks() throws IOException {
		when(stream.read(any(), eq(0), eq(loader.BUFFER_SIZE))).thenReturn(0).thenReturn(-1);
		AudioLoader.Writer writer = mock(AudioLoader.Writer.class);
		
		loader.load(stream, writer);
		verify(writer, times(1)).write(any(), eq(0), anyInt());
	}
		
	@Test
	public void testLoadWithByteArrayEndToEnd() throws UnsupportedAudioFileException, IOException {
		AudioInputStream stream = AudioInputStreamFactory.getStream(
				getClass().getResourceAsStream("/multimedia/sound-input.wav")
		);
		
		AudioLoader loader = new AudioLoader(stream);
		ByteArrayInputStream data = loader.load();
		byte[] b = new byte[40];
		data.read(b);
		assertArrayEquals(
			new byte[] {
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			},
			b
		);
		assertEquals(-1, data.read(b));
	}
}
