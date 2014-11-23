package livros.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileIOTest {

	public static void main(String[] args) {
		try {
			byte[] b = new byte[4096];
			int readByte = 0, totalByte = 0;
      
			// From test.jpg
			DataInputStream dataInStream = 
				new DataInputStream(
					new BufferedInputStream(
						new FileInputStream("./data.bin")));
			// To copy.jpg
			DataOutputStream dataOutStream = 
				new DataOutputStream(
					new BufferedOutputStream(
						 new FileOutputStream("./data.bin")));

			while(-1 != (readByte = dataInStream.read(b))){
				dataOutStream.write(b, 0, readByte);
				totalByte += readByte;
				System.out.println("Read: " + readByte + " Total: " + totalByte);
			}
      
			dataInStream.close();
			dataOutStream.close();
      
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
