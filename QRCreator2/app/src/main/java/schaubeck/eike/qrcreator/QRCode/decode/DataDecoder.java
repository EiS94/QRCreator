package schaubeck.eike.qrcreator.QRCode.decode;

import android.os.Build;
import android.support.annotation.RequiresApi;

import jpp.qrcode.ErrorCorrection;
import jpp.qrcode.Version;
import schaubeck.eike.qrcreator.QRCode.Encoding;

import java.nio.charset.StandardCharsets;

public class DataDecoder {
	public static Encoding readEncoding(byte[] bytes) {
		byte code = 0;
		for (int i = 4; i < 8; i++) {
			if ((bytes[0] & (1 << i)) != 0) {
				code |= (1 << i - 4);
			}
		}
		return Encoding.fromBits(code);
	}
	
	public static int readCharacterCount(byte[] bytes, int count) {
		int characterCount = 0;
		if (count == 8) {
			for (int i = 0; i < 4; i++) {
				if ((bytes[0] & (1 << i)) != 0)	{
					characterCount |= (1 << i + 4);
				}
			}
			for (int i = 4; i < 8; i++) {
				if ((bytes[1] & (1 << i)) != 0) {
					characterCount |= (1 << i - 4);
				}
			}
		}
		else if (count == 16) {
			for (int i = 0; i < 4; i++) {
				if ((bytes[0] & (1 << i)) != 0)	{
					characterCount = characterCount + (int) Math.pow(2, (8 + i + 4));
				}
			}
			for (int i = 0; i < 8; i++) {
				if ((bytes[1] & (1 << i)) != 0) {
					characterCount = characterCount + (int) Math.pow(2, i + 4);
				}
			}
			for (int i = 4; i < 8; i++) {
				if ((bytes[2] & (1 << i)) != 0) {
					characterCount = characterCount + (int) Math.pow(2, i - 4);
				}
			}
		}
		return characterCount;
	}
	
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String decodeToString(byte[] bytes, Version version, ErrorCorrection errorCorrection) {
            if (!(version.equals(Version.forDataBytesCount(bytes.length, errorCorrection))))
                throw new IllegalArgumentException("Version passt nicht");
            if (version.correctionInformationFor(errorCorrection).totalDataByteCount() != bytes.length)
                throw new IllegalArgumentException("Version passt nicht");
            if (readEncoding(bytes) != Encoding.BYTE) throw new QRDecodeException("Ist nicht BYTE");
            int characterCount = 0;
            if (version.number() > 9) {
                characterCount = readCharacterCount(bytes, 16);
                if (characterCount > bytes.length) throw new QRDecodeException("Byteanzahl zu klein");
                if (characterCount + 3> version.correctionInformationFor(errorCorrection).totalDataByteCount())
                    throw new QRDecodeException("Byteanzahl zu klein");
            } else {
                characterCount = readCharacterCount(bytes, 8);
                if (characterCount > bytes.length) throw new QRDecodeException("Byteanzahl zu klein");
                if (characterCount + 2> version.correctionInformationFor(errorCorrection).totalDataByteCount())
                    throw new QRDecodeException("Byteanzahl zu klein");
            }
            byte[] data = new byte[characterCount];
            int bytesCounter;
            int dataCounter = 0;
        if (version.number() < 10) {
            bytesCounter = 1;
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < 4; j++) {
                    if ((bytes[bytesCounter] & (1 << j)) != 0) {
                        data[dataCounter] |= (1 << j + 4);
                    }
                }
                bytesCounter++;
                for (int j = 4; j < 8; j++) {
                    if ((bytes[bytesCounter] & (1 << j)) != 0) {
                        data[dataCounter] |= (1 << j - 4);
                    }
                }
                dataCounter++;
            }
        } else {
            bytesCounter = 2;
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < 4; j++) {
                    if ((bytes[bytesCounter] & (1 << j)) != 0) {
                        data[dataCounter] |= (1 << j + 4);
                    }
                }
                bytesCounter++;
                for (int j = 4; j < 8; j++) {
                    if ((bytes[bytesCounter] & (1 << j)) != 0) {
                        data[dataCounter] |= (1 << j - 4);
                    }
                }
                dataCounter++;
            }
        }

        String i = new String(data, StandardCharsets.ISO_8859_1);
		return i;
	}
}
