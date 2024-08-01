package multimedia.sound;

import org.junit.Test;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class PlaybackTest {
	
	private Clip clip;
	private AudioInputStream stream;
	private Playback play;
	
	public PlaybackTest() {
		this.stream = mock(AudioInputStream.class);
		this.clip = mock(Clip.class);
		this.play = new Playback(stream, clip);
	}

	@Test
	public void testPlayWorks() throws LineUnavailableException, IOException {
		when(clip.isOpen()).thenReturn(false);
		play.play();
		
		when(clip.isOpen()).thenReturn(true);
		play.play();
				
		verify(clip, times(2)).isOpen();
		verify(clip, times(1)).open(stream);
		verify(clip, times(2)).start();
		verifyNoMoreInteractions(clip);
	}
	
	@Test
	public void testPauseWorks() {
		play.pause();
	
		verify(clip, times(1)).stop();
		verifyNoMoreInteractions(clip);
	}
	
	@Test
	public void testStopWorks() {
		play.stop();
				
		verify(clip, times(1)).stop();
		verify(clip, times(1)).setMicrosecondPosition(0);
		verifyNoMoreInteractions(clip);
	}

	@Test
	public void testBackWorks() {
		when(clip.getMicrosecondPosition()).thenReturn((long)10);
		
		play.back(4);		
		verify(clip).setMicrosecondPosition(6);
		
		play.back(12);
		verify(clip).setMicrosecondPosition(0);
	}

	@Test
	public void testFowardWorks() {
		when(clip.getMicrosecondPosition()).thenReturn((long)10);
		when(clip.getMicrosecondLength()).thenReturn((long)20);
		
		play.foward(4);		
		verify(clip).setMicrosecondPosition(14);
		
		play.foward(12);
		verify(clip).setMicrosecondPosition(20);
	}
}
