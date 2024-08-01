package multimedia;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class DataLineFactory {

	public static Clip getClip(final AudioInputStream stream) throws LineUnavailableException {
 		return (Clip) getLine(stream, Clip.class);
 	}
	
	public static SourceDataLine getSourceLine(final AudioInputStream stream) throws LineUnavailableException {
		return (SourceDataLine) getLine(stream, SourceDataLine.class);
	}
	
	private static Line getLine(final AudioInputStream stream, final Class<?> clazz) throws LineUnavailableException {
		AudioFormat format = stream.getFormat();
		DataLine.Info info = new DataLine.Info(clazz, format);
		return AudioSystem.getLine(info);
	}
	
	public static TargetDataLine getTargetLine(final AudioFormat format) throws LineUnavailableException {
		return AudioSystem.getTargetDataLine(format);
	}
}
