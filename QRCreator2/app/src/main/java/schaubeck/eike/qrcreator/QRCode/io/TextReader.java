package schaubeck.eike.qrcreator.QRCode.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextReader {

	public static boolean[][] read(InputStream in) throws IOException {

	    if (in.available() == 0) throw new IOException("Eingabestrom leer");

	    Reader reader = new InputStreamReader(in);
	    BufferedReader br = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String s;
	    while ((s = br.readLine()) != null) {
            if (s.length()==0 || s.substring(0,1).equals("#")) {
            } else sb.append(s);
        }

        char[] removeSpace = sb.toString().toCharArray();

	    StringBuilder sb2 = new StringBuilder();

	    for (int i = 0; i < removeSpace.length; i++) {
	        if (removeSpace[i] != ' ' && removeSpace[i] != '1' && removeSpace[i] != '0') throw new IOException("Falsches Zeichen");
	        if (removeSpace[i] != ' ') sb2.append(removeSpace[i]);
        }


        
        int size = (int) Math.sqrt(sb2.length());
        boolean[][] data = new boolean[size][size];
        int counter = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                char tt = sb2.charAt(counter);
                if (sb2.charAt(counter) == '1') {
                    data[i][j] = true;
                } else data[i][j] = false;
                counter++;
            }
        }
        
        return data;
	}
}
