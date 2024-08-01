package multimedia.sound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.junit.Test;

import multimedia.DataLineFactory;

public class MicrophoneTest {
	
	private final TargetDataLine line;
	private final AudioFormat format;
	private final Microphone mic;
	
	public MicrophoneTest() {
		this.line = mock(TargetDataLine.class);
		this.format = new AudioFormat(8000.0f, 16, 1, true, true);
		this.mic = new Microphone(line);
	}

	@Test
	public void testCaptureWorks() throws LineUnavailableException, IOException {
		when(line.read(any(), eq(0), eq(mic.getBufferSize()))).thenReturn(1).thenReturn(-1);		
		Microphone.Writer writer = mock(Microphone.Writer.class);
		
		mic.capture(format, writer);
		
		verify(writer, times(1)).write(any(), eq(0), anyInt());
		verify(line, times(1)).open(format);
		verify(line, times(1)).start();
		verify(line, times(1)).stop();
		verify(line, times(1)).drain();
		verify(line, times(1)).close();

	}
	
	@Test
	public void testCaptureWithByteArrayEndToEnd() throws LineUnavailableException, InterruptedException, ExecutionException, IOException {
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true); 
		TargetDataLine line = DataLineFactory.getTargetLine(format);
		
		Microphone mic = new Microphone(line);
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		Future<ByteArrayInputStream> result = executor.submit(()->{
			try {
				return mic.capture(format);
			} catch (LineUnavailableException e) {
				fail("LineUnavailableException: " + e.getMessage());
				return null;
			}
		});
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		mic.capture = false;
		
		ByteArrayInputStream stream = result.get();
		
		byte[] b = new byte[1];
		while (stream.read(b) != -1) {
			assertEquals(0, b[0], 1);
		}
	}
}
