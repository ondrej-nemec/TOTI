package multimedia.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

import javax.sound.sampled.AudioInputStream;

public class AudioLoader {
	
	protected interface Writer {		
		void write(byte[] data, int off, int len) throws IOException;		
	}

	private final AudioInputStream stream;
	
	protected final int BUFFER_SIZE = 128000;

	public AudioLoader(final AudioInputStream stream) {
		this.stream = stream;
	}
	
	public void load(final Consumer<byte[]> consumer) throws IOException {
		load(stream, (data, off, len)->{consumer.accept(data);});
	}
	
	public ByteArrayInputStream load() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		load(stream, (data, off, len)->{out.write(data, off, len);});
		return new ByteArrayInputStream(out.toByteArray());
	}
	
	protected void load(final AudioInputStream stream, final Writer writer) throws IOException {
		byte[] data = new byte[BUFFER_SIZE];
		int nBytesRead = 0;
		
		while(nBytesRead != -1){
			nBytesRead = stream.read(data, 0, data.length);
			if (nBytesRead >= 0)
				writer.write(data, 0, nBytesRead);
		}
	}
}
