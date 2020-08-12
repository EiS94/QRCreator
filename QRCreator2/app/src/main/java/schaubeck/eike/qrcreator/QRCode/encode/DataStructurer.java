package schaubeck.eike.qrcreator.QRCode.encode;

import jpp.qrcode.ErrorCorrection;
import jpp.qrcode.ErrorCorrectionGroup;
import jpp.qrcode.ErrorCorrectionInformation;
import jpp.qrcode.reedsolomon.ReedSolomon;
import schaubeck.eike.qrcreator.QRCode.DataBlock;

public class DataStructurer {
    public static DataBlock[] split(byte[] data, ErrorCorrectionInformation errorCorrectionInformation) {

        ErrorCorrectionGroup[] correctionGroups = errorCorrectionInformation.correctionGroups();
        int correctionBytesCounter = errorCorrectionInformation.correctionBytesPerBlock();
        int blockCount = errorCorrectionInformation.totalBlockCount();
        int totalByteCount = errorCorrectionInformation.totalByteCount();
        int lowerData = errorCorrectionInformation.lowerDataByteCount();

        DataBlock[] blocks = new DataBlock[blockCount];

        if (correctionGroups.length == 1) {
            byte[][] dBytes = new byte[blocks.length][correctionGroups[0].dataByteCount()];
            int counter = 0;
            for (int i = 0; i < blocks.length; i++) {
                for (int j = 0; j < lowerData; j++) {
                    dBytes[i][j] = data[counter];
                    counter++;
                    if (counter % lowerData == 0) {
                        blocks[i] = new DataBlock(dBytes[i], ReedSolomon.calculateCorrectionBytes(dBytes[i], correctionBytesCounter));
                    }
                }
            }
        }

        if (correctionGroups.length == 2) {
            int lengthGroup1 = correctionGroups[0].blockCount();
            int lengthGroup2 = correctionGroups[1].blockCount();
            int counter = 0;
            for (int i = 0; i < lengthGroup1; i++) {
                byte[] dataBytes1 = new byte[errorCorrectionInformation.lowerDataByteCount()];
                for (int j = 0; j < dataBytes1.length; j++) {
                    dataBytes1[j] = data[counter];
                    counter++;
                }
                blocks[i] = new DataBlock(dataBytes1, ReedSolomon.calculateCorrectionBytes(dataBytes1, correctionBytesCounter));
            }
            for (int i = lengthGroup1; i < (lengthGroup1+lengthGroup2); i++) {
                byte[] dataBytes2 = new byte[errorCorrectionInformation.lowerDataByteCount() + 1];
                for (int j = 0; j < dataBytes2.length; j++) {
                    dataBytes2[j] = data[counter];
                    counter++;
                }
                blocks[i] = new DataBlock(dataBytes2, ReedSolomon.calculateCorrectionBytes(dataBytes2, correctionBytesCounter));
            }
        }
        return blocks;
    }

    public static byte[] interleave(DataBlock[] blocks, ErrorCorrectionInformation ecBlocks) {
        byte[] interleave = new byte[ecBlocks.totalByteCount()];
        int max = 0;
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i].dataBytes().length > max) max = blocks[i].dataBytes().length;
        }
        int counter = 0;
        for (int i = 0; i < max; i++) {
                for (int j = 0; j < blocks.length; j++) {
                    if (i < blocks[j].dataBytes().length) {
                    interleave[counter] = blocks[j].dataBytes()[i];
                    counter++;
                }
            }
        }
        for (int i = 0; i < blocks[0].correctionBytes().length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                interleave[counter] = blocks[j].correctionBytes()[i];
                counter++;
            }
        }
        return interleave;
    }

    public static byte[] structure(byte[] data, ErrorCorrectionInformation ecBlocks) {
        if (data.length != ecBlocks.totalDataByteCount())
            throw new IllegalArgumentException("Datengrosse passt nicht");
        return interleave(split(data, ecBlocks), ecBlocks);
    }
}
