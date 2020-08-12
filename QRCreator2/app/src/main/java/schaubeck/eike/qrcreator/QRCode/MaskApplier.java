package schaubeck.eike.qrcreator.QRCode;

import jpp.qrcode.MaskFunction;

public class MaskApplier {

    public static void applyTo(boolean[][] matrix, MaskFunction mask, ReservedModulesMask reserved) {
        if (matrix.length != reserved.mask.length || matrix.length != reserved.mask[0].length)
            throw new IllegalArgumentException("Matrixlaenge ist ungleich Masklaenge");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (mask.mask(i,j) && !reserved.isReserved(i,j)) {
                    if (matrix[i][j]) matrix[i][j] = false;
                    else matrix[i][j] = true;
                }
            }
        }
    }
}
