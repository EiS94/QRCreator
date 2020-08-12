package schaubeck.eike.qrcreator.QRCode.io;

import java.io.*;

public class TextWriter {
	public static void write(OutputStream stream, boolean[][] data) throws IOException {

		Writer writer = new OutputStreamWriter(stream);
		BufferedWriter bw = new BufferedWriter(writer);

		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				if (data[i][j]) {
					bw.write("1");
				}
				else {
					bw.write("0");
				}
				if (j == data.length - 1) {
					bw.write("\n");
				}
			}
		}
		bw.close();
	}
}


