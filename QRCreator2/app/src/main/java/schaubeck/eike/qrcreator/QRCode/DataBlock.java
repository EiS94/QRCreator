package schaubeck.eike.qrcreator.QRCode;

public class DataBlock {

    byte[] dataBytes;
    byte[] correctionBytes;

    public DataBlock(byte[] dataBytes, byte[] correctionBytes) {
        this.dataBytes = dataBytes;
        this.correctionBytes = correctionBytes;
    }

    public byte[] dataBytes() {
        return dataBytes;
    }

    public byte[] correctionBytes() {
        return correctionBytes;
    }

}
