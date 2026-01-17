package toti.files.text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import toti.common.structures.ThrowingConsumer;

public class Binary {
	
	public static Binary get() {
		return new Binary();
	}
	
	public void read(ThrowingConsumer<DataInputStream, IOException> consumer, InputStream stream) throws IOException {
		try (DataInputStream dis = new DataInputStream(stream)) {
			consumer.accept(dis);
		}
	}

	public void write(ThrowingConsumer<DataOutputStream, IOException> consumer, OutputStream stream) throws IOException {
		try (DataOutputStream dis = new DataOutputStream(stream)) {
			consumer.accept(dis);
		}
	}
	
	public void write(ThrowingConsumer<DataOutputStream, IOException> consumer, String file) throws IOException {
		write(consumer, new FileOutputStream(file));
	}
}
