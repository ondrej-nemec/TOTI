package multimedia.sound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class Microphone {
	protected interface Writer {		
		void write(byte[] data, int off, int len) throws LineUnavailableException;		
	}

	public boolean capture = true;
	
	private int bufferSize;
	
	private final TargetDataLine line;
	
	public Microphone(final TargetDataLine line) {
		this.line = line;
	}
	
	public void capture(final AudioFormat format, final Consumer<byte[]> consumer) throws LineUnavailableException {
		capture(format, (data, off, len)->{consumer.accept(data);});
	}
	
	public ByteArrayInputStream capture(final AudioFormat format) throws LineUnavailableException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		capture(format, (data, off, len)->{stream.write(data, off, len);});
		return new ByteArrayInputStream(stream.toByteArray());
	}
	
	public int getBufferSize() {
		return bufferSize;
	}
	
	protected void capture(final AudioFormat format, final Writer writer)  throws LineUnavailableException {
		line.open(format);
	    
	    int numBytesRead = 0;
	    byte[] data = new byte[line.getBufferSize() / 5];
	    
	    line.start();
	    while (numBytesRead != -1 && capture) {
		   numBytesRead =  line.read(data, 0, data.length);
		   if (numBytesRead >= 0)
			   writer.write(data, 0, numBytesRead);
	    }
	    line.stop();
	    line.drain();
	    line.close();
	}
}
