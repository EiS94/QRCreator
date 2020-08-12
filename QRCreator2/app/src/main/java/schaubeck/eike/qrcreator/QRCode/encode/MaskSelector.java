package schaubeck.eike.qrcreator.QRCode.encode;

import jpp.qrcode.*;
import schaubeck.eike.qrcreator.QRCode.MaskApplier;
import schaubeck.eike.qrcreator.QRCode.ReservedModulesMask;

public class MaskSelector {

    public static void placeFormatInformation(boolean[][] res, int formatInformation) {
        byte low = (byte) formatInformation;
        byte high = (byte) (formatInformation >> 8);
        int[] bits = new int[8];

        for (int i = 0; i <= 7; i++) {
            if ((low & (1 << i)) != 0) bits[i] = 1;
            else bits[i] = 0;
        }
        int counter = 0;
        for (int j = res.length - 1; j >= res.length - 8; j--) {
            if (counter <= 7) {
                if (bits[counter] == 1) res[8][j] = true;
                else res[8][j] = false;
                counter++;
            }
        }
        counter = 0;
        for (int i = 0; i <= 5; i++) {
            if ((bits[counter]) == 1) res[i][8] = true;
            else res[i][8] = false;
            counter++;
        }
        if (bits[6] == 1) res[7][8] = true;
        else res[7][8] = false;
        if (bits[7] == 1) res[8][8] = true;
        else res[8][8] = false;


        for (int i = 0; i <= 7; i++) {
            if ((high & (1 << i)) != 0) bits[i] = 1;
            else bits[i] = 0;
        }
        counter = 0;
        for (int i = res.length - 7; i <= res.length - 1; i++) {
            if (counter <= 7) {
                if (bits[counter] == 1) res[i][8] = true;
                else res[i][8] = false;
                counter++;
            }
        }
        if (bits[0] == 1) res[8][7] = true;
        else res[8][7] = false;
        counter = 1;
        for (int j = 5; j >= 0; j--) {
            if (counter <= 7) {
                if (bits[counter] == 1) res[8][j] = true;
                else res[8][j] = false;
                counter++;
            }
        }
    }

    public static int calculatePenaltySameColored(boolean[][] data) {
        int penalty = 0;
        int counter;
        boolean color;
        for (int i = 0; i < data.length; i++) {
            counter = 1;
            color = data[i][0];
            for (int j = 1; j < data.length; j++) {
                if (data[i][j] == color) {
                    counter++;
                    if (counter == 5) penalty = penalty + 3;
                    if (counter > 5) penalty++;
                } else {
                    color = data[i][j];
                    counter = 1;
                }
            }
        }
        for (int j = 0; j < data.length; j++) {
            counter = 1;
            color = data[0][j];
            for (int i = 1; i < data.length; i++) {
                if (data[i][j] == color) {
                    counter++;
                    if (counter == 5) penalty = penalty + 3;
                    if (counter > 5) penalty++;
                } else {
                    color = data[i][j];
                    counter = 1;
                }
            }
        }
        return penalty;
    }

    public static int calculatePenalty2x2(boolean[][] arr) {
        int penalty = 0;
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1; j++) {
                if (arr[i][j] == arr[i + 1][j] && arr[i][j] == arr[i + 1][j + 1] && arr[i][j] == arr[i][j + 1]) {
                    penalty = penalty + 3;
                }
            }
        }
        return penalty;
    }

    public static int calculatePenaltyBlackWhite(boolean[][] arr) {
        int blackCounter = 0;
        int all = arr.length * arr.length;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                if (arr[i][j]) blackCounter++;
            }
        }
        return 10 * ((Math.abs(2 * blackCounter - all) * 10) / all);
    }

    public static int calculatePenaltyPattern(boolean[][] arr) {
        int penalty = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length - 6; j++) {
                if (arr[i][j] && !arr[i][j + 1] && arr[i][j + 2] && arr[i][j + 3] && arr[i][j + 4] && !arr[i][j + 5] && arr[i][j + 6]) {
                    if (j > 3 && !arr[i][j - 1] && !arr[i][j - 2] && !arr[i][j - 3] && !arr[i][j - 4] ||
                            j < arr.length - 10 && !arr[i][j + 7] && !arr[i][j + 8] && !arr[i][j + 9] && !arr[i][j + 10]) {
                        penalty = penalty + 40;
                    }
                }
            }
        }

        for (int j = 0; j < arr.length; j++) {
            for (int i = 0; i < arr.length - 6; i++) {
                if (arr[i][j] && !arr[i + 1][j] && arr[i + 2][j] && arr[i + 3 ][j] && arr[i + 4][j] && !arr[i + 5][j] && arr[i + 6][j]) {
                    if (i > 3 && !arr[i - 1][j] && !arr[i - 2][j] && !arr[i - 3][j] && !arr[i - 4][j] ||
                          i < arr.length - 10 && !arr[i + 7][j] && !arr[i + 8][j] && !arr[i + 9][j] && !arr[i + 10][j]) {
                        penalty = penalty + 40;
                    }
                }
            }
        }
        return penalty;
    }

    public static int calculatePenaltyFor(boolean[][] data) {
        return calculatePenaltyPattern(data) + calculatePenaltyBlackWhite(data) +
                calculatePenalty2x2(data) + calculatePenaltySameColored(data);
    }

    public static MaskPattern maskWithBestMask(boolean[][] data, ErrorCorrection correction, ReservedModulesMask modulesMask) {
        if (modulesMask.size() != data.length) throw new IllegalArgumentException("Falsche Maskengroesse");
        int minPenalty = Integer.MAX_VALUE;
        MaskPattern pattern = null;
        int penalty;
        for(MaskPattern mp : MaskPattern.values()) {
            penalty = penaltyForMask(data, correction, modulesMask, mp);
            if (penalty < minPenalty) {
                minPenalty = penalty;
                pattern = mp;
            }

        }

        FormatInformation info = FormatInformation.get(correction, pattern);
        MaskApplier.applyTo(data, pattern.maskFunction(), modulesMask);
        placeFormatInformation(data, info.formatInfo());
        return pattern;
    }

    public static int penaltyForMask(boolean[][] data, ErrorCorrection correction, ReservedModulesMask mask, MaskPattern pattern) {
        FormatInformation info = FormatInformation.get(correction, pattern);
        MaskApplier.applyTo(data, pattern.maskFunction(), mask);
        placeFormatInformation(data, info.formatInfo());
        int penalty = calculatePenaltyFor(data);
        MaskApplier.applyTo(data, pattern.maskFunction(), mask);
        return penalty;
    }
}
