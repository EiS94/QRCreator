package schaubeck.eike.qrcreator.QRCode.encode;

import jpp.qrcode.ErrorCorrection;
import jpp.qrcode.ErrorCorrectionInformation;
import jpp.qrcode.Version;

import java.io.UnsupportedEncodingException;
import java.util.*;

public final class DataEncoder {

    public static DataEncoderResult encodeForCorrectionLevel(String str, ErrorCorrection level) throws UnsupportedEncodingException {

        //Version herausfinden
        int laenge = 4 + 8 + (str.length() * 8) + 4;
        while (laenge % 8 != 0) {
            laenge++;
        }
        Version version = Version.forDataBytesCount((laenge / 8), level);
        if (version.number() > 9) {
            laenge = 4 + 16 + (str.length() * 8) + 4;
            while (laenge % 8 != 0) {
                laenge++;
            }
            version = Version.forDataBytesCount((laenge / 8), level);
        }

        int dataBytes = version.correctionInformationFor(level).totalDataByteCount();

        // Codieren
        byte[] data = str.getBytes("ISO-8859-1");
        byte[] coded = new byte[dataBytes];

        // Setzen der IndicatorBits
        coded[0] |= (1 << 6);

        int outerByteCounter = 0;
        if (version.number() <= 9) {

            // Setzen der CciBits
            int byteCounter = 0;
            int byteCounterDefault = byteCounter;
            byte strlength = (byte) str.length();
            for (int i = 0; i <= 7; i++) {
                if ((strlength & (1 << i)) != 0) {
                    if (i > 3) {
                        byteCounter = byteCounterDefault;
                        coded[byteCounter] |= (1 << i - 4);
                    } else {
                        byteCounter = byteCounterDefault + 1;
                        coded[byteCounter] |= (1 << i + 4);
                    }
                }
            }
            if (byteCounter == byteCounterDefault) byteCounter++;

            //Setzten der zu codierenden Nachricht
            for (int i = 0; i < data.length; i++) {
                byteCounterDefault = byteCounter;
                for (int j = 0; j <= 7; j++) {
                    if ((data[i] & (1 << j)) != 0) {
                        if (j > 3) {
                            byteCounter = byteCounterDefault;
                            coded[byteCounter] |= (1 << j - 4);
                        } else {
                            byteCounter = byteCounterDefault + 1;
                            coded[byteCounter] |= (1 << j + 4);
                        }
                    }
                }
                if (byteCounter == byteCounterDefault) byteCounter++;
                outerByteCounter = byteCounter;
            }
        }
        else {

            //Setzen der CciBits
            int byteCounter = 0;
            int cciLength = str.length();
            byte low = (byte)cciLength;
            byte high = (byte)(cciLength >> 8);
            int[] cciBits1 = {8,8,8,8,8,8,8,8};
            int counter = 0;
            for (int i = 0; i <= 7; i++) {
                if ((high & (1 << i)) != 0) {
                    cciBits1[counter] = i;
                    counter++;
                }
            }
            counter = 0;
            int bit;
            int byteCounterDefault = byteCounter;
            while (counter < 8 && cciBits1[counter] != 8) {
                bit = cciBits1[counter];
                if (bit > 3) {
                    byteCounter = byteCounterDefault;
                    coded[byteCounter] |= (1 << bit - 4);
                } else {
                    byteCounter = byteCounterDefault + 1;
                    coded[byteCounter] |= (1 << bit + 4);
                }
                counter++;
            }
            if (byteCounter == byteCounterDefault) byteCounter++;

            int[] cciBits2 = {8,8,8,8,8,8,8,8};
            counter = 0;
            for (int i = 0; i <= 7; i++) {
                if ((low & (1 << i)) != 0) {
                    cciBits2[counter] = i;
                    counter++;
                }
            }
            counter = 0;
            byteCounterDefault = byteCounter;
            while (counter < 8 && cciBits2[counter] != 8) {
                bit = cciBits2[counter];
                if (bit > 3) {
                    byteCounter = byteCounterDefault;
                    coded[byteCounter] |= (1 << bit - 4);
                } else {
                    byteCounter = byteCounterDefault + 1;
                    coded[byteCounter] |= (1 << bit + 4);
                }
                counter++;
            }
            if (byteCounter == byteCounterDefault) byteCounter++;

            //Setzen der zu codierenden Nachricht
            for (int i = 0; i < data.length; i++) {
                byteCounterDefault = byteCounter;
                for (int j = 0; j <= 7; j++) {
                    if ((data[i] & (1 << j)) != 0) {
                        if (j > 3) {
                            byteCounter = byteCounterDefault;
                            coded[byteCounter] |= (1 << j - 4);
                        } else {
                            byteCounter = byteCounterDefault + 1;
                            coded[byteCounter] |= (1 << j + 4);
                        }
                    }
                }
                if (byteCounter == byteCounterDefault) byteCounter++;
                outerByteCounter = byteCounter;
            }
        }

        outerByteCounter++;
        if (outerByteCounter == 1) outerByteCounter++;

        while (outerByteCounter < dataBytes) {
            coded[outerByteCounter] = -20;
            outerByteCounter++;
            if (outerByteCounter < dataBytes) {
                coded[outerByteCounter] = 17;
                outerByteCounter++;
            }
        }
        return new DataEncoderResult(coded, version);
    }
}
