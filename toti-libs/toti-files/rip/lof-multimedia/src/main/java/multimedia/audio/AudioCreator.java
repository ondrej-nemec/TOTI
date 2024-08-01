package multimedia.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class AudioCreator {
	
	protected final int BUFFER_SIZE = 128000;

	private final OutputStream os;
	
	public AudioCreator(final OutputStream destination) {
		this.os = destination;
	}
	
	public void save(final AudioFormat format, final AudioFileFormat.Type type, final ByteArrayOutputStream data) throws IOException {
		byte[] audioData = data.toByteArray();
        AudioInputStream audioInputStream = new AudioInputStream(
        		new ByteArrayInputStream(audioData),
        		format,
                audioData.length / format.getFrameSize());
		AudioSystem.write(audioInputStream, type, os);
	}
}
