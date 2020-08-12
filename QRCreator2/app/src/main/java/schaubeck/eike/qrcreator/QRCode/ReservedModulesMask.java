package schaubeck.eike.qrcreator.QRCode;
import java.util.ArrayList;
import java.util.List;

import jpp.qrcode.Version;

public class ReservedModulesMask {

    boolean[][] mask;

    public ReservedModulesMask(boolean[][] mask) {
        this.mask = mask;
    }

    public boolean isReserved(int i, int j) {
        boolean reserved = false;
        if (mask[i][j]) reserved = true;
        return reserved;
    }

    public int size() {
        return mask.length;
    }

    public static ReservedModulesMask forVersion(Version version) {
        boolean[][] mask = new boolean[version.size()][version.size()];

        //OrientationPatterns
        setOrientationPattern(0, 0, mask);
        setOrientationPattern(0, mask.length - 7, mask);
        setOrientationPattern(mask.length - 7, 0, mask);

        //DarkModule
        mask[version.size() - 8][8] = true;

        //AlignmentPatterns
        List<Tuple> tuples = new ArrayList<Tuple>();
        int[] alignmentPositions = version.alignmentPositions();
        for (int i = 0; i < alignmentPositions.length; i++) {
            for (int j = 0; j < alignmentPositions.length; j++) {
                tuples.add(new Tuple(alignmentPositions[i], alignmentPositions[j]));
            }
        }
        for (Tuple tuplesTemp : tuples) {
            if (tuplesTemp.x - 2 <= 6 && tuplesTemp.y - 2 <= 6 || tuplesTemp.x + 2 >= mask.length - 6 &&
                    tuplesTemp.y - 2 <= 6 || tuplesTemp.x - 2 <= 6 && tuplesTemp.y + 2 >= mask.length - 6) {
            } else setAlignmentPattern(tuplesTemp.y, tuplesTemp.x, mask);
        }

        //TimingPatterns
        for (int x = 8; x < mask.length - 7; x++) {
            mask[6][x] = true;
        }
        for (int y = 8; y < mask.length - 7; y++) {
            mask[y][6] = true;
        }

        //FormatInfos
        for (int x = 0; x < mask.length; x++) {
            if (x <= 8 || x >= mask.length - 8) {
                mask[8][x] = true;
            }
        }
        for (int y = 0; y < mask.length; y++) {
            if (y <= 8 || y >= mask.length - 8) {
                mask[y][8] = true;
            }
        }

        //VersionInfos
        if (version.number() > 6) {
            for (int x = 0; x < 6; x++) {
                for (int y = mask.length - 11; y < mask.length - 8; y++) {
                    mask[y][x] = true;
                }
            }
            for (int x = mask.length - 11; x < mask.length - 8; x++) {
                for (int y = 0; y < 6; y++) {
                    mask[y][x] = true;
                }
            }
        }

        ReservedModulesMask temp = new ReservedModulesMask(mask);
        return temp;
    }

    public static void setOrientationPattern(int i, int j, boolean[][] mask) {
        int xEnd = j + 6;
        int yEnd = i + 6;
        int xBegin = j;
        int yBegin = i;
        for (int x = j; x <= xEnd; x++) {
            for (int y = i; y <= yEnd; y++) {
                if (x == xBegin || x == xEnd || y == yBegin || y == yEnd) {
                    mask[y][x] = true;
                } else if (x >= xBegin + 2 && x <= xEnd - 2 && y >= yBegin + 2 && y <= yEnd - 2) {
                    mask[y][x] = true;
                } else mask[y][x] = true;
            }
        }
        if (xBegin > 0 && yBegin == 0) {
            for (int y = yBegin; y <= yEnd + 1; y++) {
                mask[y][xBegin - 1] = true;
            }
            for (int x = xBegin; x <= xEnd; x++) {
                mask[yEnd + 1][x] = true;
            }
        } else if (xBegin == 0 && yBegin == 0) {
            for (int y = yBegin; y <= yEnd + 1; y++) {
                mask[y][xEnd + 1] = true;
            }
            for (int x = xBegin; x <= xEnd; x++) {
                mask[yEnd + 1][x] = true;
            }
        } else if (xBegin == 0 && yBegin > 0) {
            for (int y = yBegin - 1; y <= yEnd; y++) {
                mask[y][xEnd + 1] = true;
            }
            for (int x = xBegin; x <= xEnd; x++) {
                mask[yBegin - 1][x] = true;
            }
        }
    }

    public static void setAlignmentPattern(int i, int j, boolean[][] mask) {
        int xBegin = j - 2;
        int xEnd = j + 2;
        int yBegin = i - 2;
        int yEnd = i + 2;
        for (int x = xBegin; x <= xEnd; x++) {
            for (int y = yBegin; y <= yEnd; y++) {
                mask[y][x] = true;
            }
        }
    }
}
