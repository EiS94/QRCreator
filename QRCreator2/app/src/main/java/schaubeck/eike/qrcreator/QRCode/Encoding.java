package schaubeck.eike.qrcreator.QRCode;

public enum Encoding {

    NUMERIC(0b001), ALPHANUMERIC(0b010), BYTE(0b100), KANJI(0b1000), ECI(0b111), INVALID(-1);

    int bits;

    Encoding(int bits) {
        this.bits = bits;
    }

    public static Encoding fromBits(int i) {
        switch (i) {
            case 0b001:
                return NUMERIC;
            case 0b010:
                return ALPHANUMERIC;
            case 0b100:
                return BYTE;
            case 0b1000:
                return KANJI;
            case 0b111:
                return ECI;
            default:
                return INVALID;
        }
    }

    public int bits() {
        return bits;
    }
}
