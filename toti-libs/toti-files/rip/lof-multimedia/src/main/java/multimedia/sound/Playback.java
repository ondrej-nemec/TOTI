package multimedia.sound;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class Playback {

	private final AudioInputStream stream;
	private final Clip clip;
	
	public Playback(final AudioInputStream stream, final Clip clip) {
		this.stream = stream;
		this.clip = clip;
	}
	
	public void play() throws LineUnavailableException, IOException {
		if (!clip.isOpen())
			clip.open(stream);
		clip.start();
	}
	
	public void pause() {
		clip.stop();
	}
	
	public void stop() {
		clip.setMicrosecondPosition(0);
		clip.stop();
	}
		
	public void setLoop(final int count) {
		if (count < 0)
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		else
			clip.loop(count);
	}
	
	public void foward(final long microSeconds) {
		if (getMicroSecondPosition() + microSeconds > getDuration())
			clip.setMicrosecondPosition(getDuration());
		else
			clip.setMicrosecondPosition(getMicroSecondPosition() + microSeconds);
	}
	
	public void back(final long microSeconds) {
		if (getMicroSecondPosition() - microSeconds < 0)
			clip.setMicrosecondPosition(0);
		else
			clip.setMicrosecondPosition(getMicroSecondPosition() - microSeconds);
	}
	
	public long getDuration() {
		return clip.getMicrosecondLength();
	}
	
	public long getMicroSecondPosition() {
		return clip.getMicrosecondPosition();
	}
	
	@Override
	protected void finalize() throws Throwable {
		clip.stop();
		clip.close();
	}
}
