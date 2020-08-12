package schaubeck.eike.qrcreator.QRCode.decode;

import schaubeck.eike.qrcreator.QRCode.DataPositions;
import schaubeck.eike.qrcreator.QRCode.ReservedModulesMask;

public class DataExtractor {
    public static byte[] extract(boolean[][] data, ReservedModulesMask mask, int byteCount) {
        if (mask.size() != data.length) throw new IllegalArgumentException("Maske passt nicht zur Matrix");
        byte[] daten = new byte[byteCount];
        DataPositions position = new DataPositions(mask);
        int counter = 0;
        int byteCounter = 0;
        boolean next = true;
        int i = position.i();
        int j = position.j();
        while (byteCounter < byteCount && next) {
            i = position.i();
            j = position.j();
            if (data[i][j]) {
                daten[byteCounter] |= (1 << 7 - counter);
            }
            counter++;
            next = position.next();
            if (counter%8 == 0) {
                counter = 0;
                byteCounter++;
            }

        }
        return daten;
    }
}
