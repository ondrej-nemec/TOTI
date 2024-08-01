package multimedia.sound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Reproductor {
	protected interface Reader {
		int read(byte[] readed) throws IOException, LineUnavailableException;
	}
	
	protected final int BUFFER_SIZE = 128000;
	
	private final SourceDataLine line;
	public boolean play = true;
	
	public Reproductor(final SourceDataLine line) {
		this.line = line;
	}

	public void play(final AudioFormat format, final ByteArrayOutputStream data) throws IOException, LineUnavailableException {
		ByteArrayInputStream in = new ByteArrayInputStream(data.toByteArray());
		play(format, (readed)->{return in.read(readed);});
	}
	
	protected void play (final AudioFormat format, final Reader reader) throws IOException, LineUnavailableException {
		line.open(format);
		line.start();
		int nBytesRead = 0;
		byte[] readed = new byte[BUFFER_SIZE];
		while(nBytesRead != -1 && play){
			nBytesRead = reader.read(readed);
			if(nBytesRead >= 0){
				/*int nBytesWritten =*/ line.write(readed, 0, nBytesRead);
			}
		}
		line.stop();
		line.close();
	}
}
