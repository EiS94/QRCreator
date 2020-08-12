package schaubeck.eike.qrcreator.QRCode.decode;

import jpp.qrcode.ErrorCorrectionGroup;
import jpp.qrcode.ErrorCorrectionInformation;
import jpp.qrcode.reedsolomon.ReedSolomon;
import jpp.qrcode.reedsolomon.ReedSolomonException;
import schaubeck.eike.qrcreator.QRCode.DataBlock;

public class DataDestructurer {
    public static byte[] join(DataBlock[] blocks, ErrorCorrectionInformation errorCorrectionInformation) throws ReedSolomonException {
        for (int i = 0; i < blocks.length; i++) {
            try {
                ReedSolomon.correct(blocks[i].dataBytes(), blocks[i].correctionBytes());
            } catch (ReedSolomonException e) {
                throw new QRDecodeException("Korrektur nicht moeglich");
            }
        }
            byte[] join = new byte[errorCorrectionInformation.totalDataByteCount()];
            int blocksGroup1 = errorCorrectionInformation.correctionGroups()[0].blockCount();
            if (errorCorrectionInformation.correctionGroups().length > 1) {
                int blocksGroup2 = errorCorrectionInformation.correctionGroups()[1].blockCount();
            }
            int DataLength1 = errorCorrectionInformation.lowerDataByteCount();
            int blockCount = errorCorrectionInformation.totalBlockCount();
            int counter = 0;
            for (int i = 0; i < blockCount; i++) {
                for (int j = 0; j < blocks[i].dataBytes().length; j++) {
                    join[counter] = blocks[i].dataBytes()[j];
                    counter++;
                }
        }
        return join;
    }

    public static DataBlock[] deinterleave(byte[] data, ErrorCorrectionInformation errorCorrectionInformation) {
        int totalBlocks = errorCorrectionInformation.totalBlockCount();
        DataBlock[] blocks = new DataBlock[totalBlocks];
        ErrorCorrectionGroup[] test = errorCorrectionInformation.correctionGroups();
        if (errorCorrectionInformation.correctionGroups().length == 1) {
            int dataLength = errorCorrectionInformation.lowerDataByteCount();
            int correctionLength = errorCorrectionInformation.correctionBytesPerBlock();
            int counter = 0;
            for (int i = 0; i < totalBlocks; i++) {
                byte[] dataBytes = new byte[dataLength];
                byte[] correctionBytes = new byte[correctionLength];
                for (int j = i; j < (totalBlocks * dataLength); j = j + totalBlocks) {
                    dataBytes[counter] = data[j];
                    counter++;
                }
                counter = 0;
                for (int j = (totalBlocks * dataLength) + i; j < data.length; j = j + totalBlocks) {
                    correctionBytes[counter] = data[j];
                    counter++;
                }
                blocks[i] = new DataBlock(dataBytes, correctionBytes);
                counter = 0;
            }
        } else {
            int blocksGroup1 = errorCorrectionInformation.correctionGroups()[0].blockCount();
            int blocksGroup2 = errorCorrectionInformation.correctionGroups()[1].blockCount();
            int dataLength = blocksGroup1 * errorCorrectionInformation.lowerDataByteCount() +
                    blocksGroup2 * (errorCorrectionInformation.lowerDataByteCount() + 1);
            int counter = 0;
            for (int i = 0; i < blocksGroup1; i++) {
                byte[] dataBytes = new byte[errorCorrectionInformation.lowerDataByteCount()];
                byte[] correctionBytes = new byte[errorCorrectionInformation.correctionBytesPerBlock()];
                for (int j = i; j < dataLength; j = j + totalBlocks) {
                    if (counter < errorCorrectionInformation.lowerDataByteCount()) {
                        dataBytes[counter] = data[j];
                        counter++;
                    }
                }
                counter = 0;
                for (int j = (dataLength) + i; j < data.length; j = j + totalBlocks) {
                    correctionBytes[counter] = data[j];
                    counter++;
                }
                blocks[i] = new DataBlock(dataBytes, correctionBytes);
                counter = 0;
            }
            for (int i = blocksGroup1; i < totalBlocks; i++) {
                byte[] dataBytes = new byte[errorCorrectionInformation.lowerDataByteCount() + 1];
                byte[] correctionBytes = new byte[errorCorrectionInformation.correctionBytesPerBlock()];
                for (int j = i; j <= dataLength; j = j + totalBlocks) {
                    dataBytes[counter] = data[j];
                    counter++;
                    if (counter == errorCorrectionInformation.lowerDataByteCount())
                        j = j - blocksGroup1;
                }
                counter = 0;
                for (int j = (dataLength) + i; j < data.length; j = j + totalBlocks) {
                    correctionBytes[counter] = data[j];
                    counter++;
                }
                blocks[i] = new DataBlock(dataBytes, correctionBytes);
                counter = 0;
            }
        }
        return blocks;
    }

    public static byte[] destructure(byte[] data, ErrorCorrectionInformation ecBlocks) throws ReedSolomonException {
        if (data.length != ecBlocks.totalByteCount())
            throw new IllegalArgumentException("Data ungleich erwarteter Groesse");
        byte[] destr = join(deinterleave(data, ecBlocks), ecBlocks);
        return destr;
    }
}
