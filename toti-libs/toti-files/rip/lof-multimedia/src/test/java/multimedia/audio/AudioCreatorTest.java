package multimedia.audio;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

import org.junit.Ignore;
import org.junit.Test;

public class AudioCreatorTest {

	private final AudioFormat format;
	private final AudioFileFormat.Type type;
	private final OutputStream os;
	private final AudioCreator creator;
		
	public AudioCreatorTest() {
		this.format = mock(AudioFormat.class);
		this.type = mock(AudioFileFormat.Type.class);
		this.os = mock(OutputStream.class);
		this.creator = spy(new AudioCreator(os));
	}

	@Ignore("hard test - static function audio system")
	@Test
	public void testSaveWithByteArrayWorks() throws IOException {
		ByteArrayOutputStream data = mock(ByteArrayOutputStream.class);
		when(data.toByteArray()).thenReturn(new byte[] {0});
		when(format.getFrameSize()).thenReturn(1);
		
		creator.save(format, type, data);
		
		verify(data, times(1)).toByteArray();
		verify(format, times(1)).getFrameSize();
	}
	
	@Test
	public void testSaveWithByteArrayEndToEnd() throws IOException {
		String file = getClass().getResource("/multimedia/sound-output.wav").getFile();
		try (OutputStream os = new FileOutputStream(file)) {
			AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
			AudioCreator cr = new AudioCreator(os);
			
			ByteArrayInputStream aux = new ByteArrayInputStream(new byte[] {
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
			});	
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			byte[] array = new byte[10];
			while (aux.read(array) != -1) {
				data.write(array);
			}			
			
			cr.save(format, AudioFileFormat.Type.AU, data);
		} catch (IOException e) {
			throw new IOException(e);
		}
	}
	
}
