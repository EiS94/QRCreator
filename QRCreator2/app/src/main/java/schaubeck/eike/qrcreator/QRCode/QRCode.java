package schaubeck.eike.qrcreator.QRCode;

import java.util.ArrayList;
import java.util.List;

import jpp.qrcode.ErrorCorrection;
import jpp.qrcode.FormatInformation;
import jpp.qrcode.MaskPattern;
import jpp.qrcode.Version;
import jpp.qrcode.VersionInformation;

public class QRCode {

    boolean[][] validatedQRCode;
    Version version;
    MaskPattern pattern;
    ErrorCorrection correction;

    public QRCode(boolean[][] validatedQRCode, Version version, MaskPattern pattern, ErrorCorrection correction) {
        this.validatedQRCode = validatedQRCode;
        this.version = version;
        this.pattern = pattern;
        this.correction = correction;
    }

    public boolean[][] data() {
        return validatedQRCode;
    }

    public Version version() {
        return version;
    }

    public MaskPattern maskPattern() {
        return pattern;
    }

    public ErrorCorrection errorCorrection() {
        return correction;
    }

    public String matrixToString() {
        StringBuilder qrString = new StringBuilder();
        for (int i = 0; i < validatedQRCode.length; i++) {
            for (int j = 0; j < validatedQRCode[i].length; j++) {
                if (validatedQRCode[i][j]) {
                    qrString.append((char) 0x2588);
                    qrString.append((char) 0x2588);
                } else {
                    qrString.append((char) 0x2591);
                    qrString.append((char) 0x2591);
                }
            }
            qrString.append("\r\n");
        }
        if (qrString.length() > 0) {
            qrString.setLength(qrString.length() - 1);
        }
        String out = qrString.toString();
        return out;
    }

    public static QRCode createValidatedFromBooleans(boolean[][] data) {
        // Matrix checken
        if (Utils.isNull(data)) throw new InvalidQRCodeException("Matrix null");
        boolean square = true;
        for (int i = 0; i < data.length; i++) {
            if (data[i].length != data.length) square = false;
        }
        if (Utils.isNull(data.length) || data.length == 0 || !square)
            throw new InvalidQRCodeException("Matrix leer oder nicht quadratisch");

        // Version checken
        if (!((data.length - 17) / 4 >= 1 && (data.length - 17) / 4 <= 40 && ((data.length - 17) % 4 == 0))) {
            throw new InvalidQRCodeException("Groesse passt zu keiner Version");
        }
        Version version = Version.fromNumber((data.length - 17) / 4);

        // OrientationPattern checken
        if (!checkOrientationPattern(0, 0, data) ||
                !checkOrientationPattern(data.length - 7, 0, data) ||
                !checkOrientationPattern(0, data.length - 7, data)) {
            throw new InvalidQRCodeException("OrientationPattern falsch");
        }

        // TimingPattern checken
        if (!checkTimingPattern(data)) throw new InvalidQRCodeException("TimingPattern falsch");

        //AlignmentPattern checken
        List<Tuple> tuples = new ArrayList<Tuple>();
        int[] alignmentPositions = version.alignmentPositions();
        boolean correctAlignment = true;
        for (int i = 0; i < alignmentPositions.length; i++) {
            for (int j = 0; j < alignmentPositions.length; j++) {
                tuples.add(new Tuple(alignmentPositions[i], alignmentPositions[j]));
            }
        }
        for (Tuple tuplesTemp : tuples) {
            if (tuplesTemp.x - 2 <= 6 && tuplesTemp.y - 2 <= 6 || tuplesTemp.x + 2 >= data.length - 6 &&
                    tuplesTemp.y - 2 <= 6 || tuplesTemp.x - 2 <= 6 && tuplesTemp.y + 2 >= data.length - 6) {
            } else {
                correctAlignment = checkAlignmentPattern(tuplesTemp.y, tuplesTemp.x, data);
                if (!correctAlignment) throw new InvalidQRCodeException("AlignmentPattern falsch");
            }
        }

        // Version Information + Dark Module checken
        if (data[version.size() - 8][8] != true) throw new InvalidQRCodeException("DarkModule falsch");
        if (version.number() > 6) {
            String versionInfoDown = "";
            String versionInfoUp = "";
            for (int x = 5; x >= 0; x--) {
                for (int y = data.length - 9; y >= data.length - 11; y--) {
                    if (data[y][x]) {
                        versionInfoDown = versionInfoDown + "1";
                    } else versionInfoDown = versionInfoDown + "0";
                }
            }
            for (int y = 5; y >= 0; y--) {
                for (int x = data.length - 9; x >= data.length - 11; x--)
                    if (data[y][x]) {
                        versionInfoUp = versionInfoUp + "1";
                    } else versionInfoUp = versionInfoUp + "0";
            }
            int versionInfoDownInt = Integer.parseInt(versionInfoDown, 2);
            int versionInfoUpInt = Integer.parseInt(versionInfoUp, 2);
            if (!(VersionInformation.forVersion(version) == versionInfoDownInt ||
                    VersionInformation.forVersion(version) == versionInfoUpInt)) {
                throw new InvalidQRCodeException("Falsche VersionInformation");
            }
        }

        //FormatInfos checken
        String formatInfo1 = "";
        String formatInfo2 = "";
        for (int x = 0; x <= 8; x++) {
            if (x != 6) {
                if (data[8][x]) {
                    formatInfo1 = formatInfo1 + "1";
                } else formatInfo1 = formatInfo1 + "0";
            }
        }
        for (int y = 7; y >= 0; y--) {
            if (y != 6) {
                if (data[y][8]) {
                    formatInfo1 = formatInfo1 + "1";
                } else formatInfo1 = formatInfo1 + "0";
            }
        }
        for (int y = data.length - 1; y > data.length - 8; y--) {
            if (data[y][8]) {
                formatInfo2 = formatInfo2 + "1";
            } else formatInfo2 = formatInfo2 + "0";

        }
        for (int x = data.length - 8; x < data.length; x++) {
            if (data[8][x]) {
                formatInfo2 = formatInfo2 + "1";
            } else formatInfo2 = formatInfo2 + "0";
        }
        int formatInfo1Int = Integer.parseInt(formatInfo1, 2);
        int formatInfo2Int = Integer.parseInt(formatInfo2, 2);
        MaskPattern pattern = null;
        ErrorCorrection correction = null;
        if (FormatInformation.fromBits(formatInfo1Int) != null || FormatInformation.fromBits(formatInfo2Int) != null) {
            FormatInformation formatInformation = FormatInformation.fromBits(formatInfo1Int);
            if (formatInformation == null) formatInformation = FormatInformation.fromBits(formatInfo2Int);
            pattern = formatInformation.maskPattern();
            correction = formatInformation.errorCorrection();
        } else throw new InvalidQRCodeException("Falsche FormatInfo");

        //QRCode erstellen und zurzueckgeben
        QRCode qrCode = new QRCode(data, version, pattern, correction);
        return qrCode;
    }

    public static boolean checkTimingPattern(boolean[][] data) {
        boolean checker = true;
        boolean alternatly = false;
        int x = 8;
        int y = 8;
        while (x <= data.length - 9) {
            if (data[6][x] == alternatly) checker = false;
            if (alternatly == true) alternatly = false;
            else alternatly = true;
            x++;
        }
        alternatly = false;
        while (y <= data.length - 9) {
            if (data[y][6] == alternatly) checker = false;
            if (alternatly == true) alternatly = false;
            else alternatly = true;
            y++;
        }
        return checker;
    }

    public static boolean checkOrientationPattern(int i, int j, boolean[][] data) {
        boolean isCorrect = true;
        int xBegin = j;
        int xEnd = j + 6;
        int yBegin = i;
        int yEnd = i + 6;
        for (int x = xBegin; x <= xEnd; x++) {
            if (!(data[yBegin][x])) isCorrect = false;
            if (!(data[yEnd][x])) isCorrect = false;
        }
        for (int x = xBegin + 1; x <= xEnd - 1; x++) {
            if ((data[yBegin + 1][x])) isCorrect = false;
            if (data[yEnd - 1][x]) isCorrect = false;
        }
        for (int x = xBegin + 2; x <= xEnd - 2; x++) {
            if (!(data[yBegin + 2][x])) isCorrect = false;
            if (!(data[yEnd - 2][x])) isCorrect = false;
            if (!(data[yBegin + 3][x])) isCorrect = false;
        }
        if (!(data[xBegin][yBegin + 1])) isCorrect = false;
        if (!(data[xBegin][yBegin + 2])) isCorrect = false;
        if (!(data[xBegin][yBegin + 3])) isCorrect = false;
        if (!(data[xBegin][yBegin + 4])) isCorrect = false;
        if (!(data[xBegin][yBegin + 5])) isCorrect = false;
        if (!(data[xEnd][yBegin + 1])) isCorrect = false;
        if (!(data[xEnd][yBegin + 2])) isCorrect = false;
        if (!(data[xEnd][yBegin + 3])) isCorrect = false;
        if (!(data[xEnd][yBegin + 4])) isCorrect = false;
        if (!(data[xEnd][yBegin + 5])) isCorrect = false;
        if (data[xBegin + 1][yBegin + 2]) isCorrect = false;
        if (data[xBegin + 1][yBegin + 3]) isCorrect = false;
        if (data[xBegin + 1][yBegin + 4]) isCorrect = false;
        if (data[xEnd - 1][yBegin + 2]) isCorrect = false;
        if (data[xEnd - 1][yBegin + 3]) isCorrect = false;
        if (data[xEnd - 1][yBegin + 4]) isCorrect = false;

        if (xBegin > 0 && yBegin == 0) {
            for (int y = yBegin; y <= yEnd + 1; y++) {
                if (data[y][xBegin - 1]) isCorrect = false;
            }
            for (int x = xBegin; x <= xEnd; x++) {
                if (data[yEnd + 1][x]) isCorrect = false;
            }
        } else if (xBegin == 0 && yBegin == 0) {
            for (int y = yBegin; y <= yEnd + 1; y++) {
                if (data[y][xEnd + 1]) isCorrect = false;
            }
            for (int x = xBegin; x <= xEnd; x++) {
                if (data[yEnd + 1][x]) isCorrect = false;
            }
        } else if (xBegin == 0 && yBegin > 0) {
            for (int y = yBegin - 1; y <= yEnd; y++) {
                if (data[y][xEnd + 1]) isCorrect = false;
            }
            for (int x = xBegin; x <= xEnd; x++) {
                if (data[yBegin - 1][x]) isCorrect = false;
            }
        }
        return isCorrect;
    }

    public static void orientationPatternPosition(int i, int j, boolean[][] data) {
        int xEnd = j + 6;
        int yEnd = i + 6;
        int xBegin = j;
        int yBegin = i;
        for (int x = j; x <= xEnd; x++) {
            for (int y = i; y <= yEnd; y++) {
                if (x == xBegin || x == xEnd || y == yBegin || y == yEnd) {
                    data[y][x] = true;
                } else if (x >= xBegin + 2 && x <= xEnd - 2 && y >= yBegin + 2 && y <= yEnd - 2) {
                    data[y][x] = true;
                } else data[y][x] = false;
            }
        }
        if (xBegin > 0 && yBegin == 0) {
            for (int y = yBegin; y <= yEnd + 1; y++) {
                data[y][xBegin - 1] = false;
            }
            for (int x = xBegin; x <= xEnd; x++) {
                data[yEnd + 1][x] = false;
            }
        } else if (xBegin == 0 && yBegin == 0) {
            for (int y = yBegin; y <= yEnd + 1; y++) {
                data[y][xEnd + 1] = false;
            }
            for (int x = xBegin; x <= xEnd; x++) {
                data[yEnd + 1][x] = false;
            }
        } else if (xBegin == 0 && yBegin > 0) {
            for (int y = yBegin - 1; y <= yEnd; y++) {
                data[y][xEnd + 1] = false;
            }
            for (int x = xBegin; x <= xEnd; x++) {
                data[yBegin - 1][x] = false;
            }
        }
    }

    public static boolean checkAlignmentPattern(int i, int j, boolean[][] data) {
        boolean isCorrect = true;
        int xBegin = j - 2;
        int xEnd = j + 2;
        int yBegin = i - 2;
        int yEnd = i + 2;
        for (int x = xBegin; x <= xEnd; x++) {
            if (!data[yBegin][x]) isCorrect = false;
            if (!data[yEnd][x]) isCorrect = false;
        }
        if (!(data[yBegin + 1][xBegin])) isCorrect = false;
        if (!(data[yBegin + 2][xBegin])) isCorrect = false;
        if (!(data[yBegin + 3][xBegin])) isCorrect = false;
        if (!(data[yBegin + 1][xEnd])) isCorrect = false;
        if (!(data[yBegin + 2][xEnd])) isCorrect = false;
        if (!(data[yBegin + 3][xEnd])) isCorrect = false;
        if (!(data[i][j])) isCorrect = false;
        if (data[yBegin + 1][xBegin + 1]) isCorrect = false;
        if (data[yBegin + 1][xBegin + 2]) isCorrect = false;
        if (data[yBegin + 1][xBegin + 3]) isCorrect = false;
        if (data[yBegin + 2][xBegin + 1]) isCorrect = false;
        if (data[yBegin + 2][xBegin + 3]) isCorrect = false;
        if (data[yBegin + 3][xBegin + 1]) isCorrect = false;
        if (data[yBegin + 3][xBegin + 2]) isCorrect = false;
        if (data[yBegin + 3][xBegin + 3]) isCorrect = false;
        return isCorrect;
    }
}
