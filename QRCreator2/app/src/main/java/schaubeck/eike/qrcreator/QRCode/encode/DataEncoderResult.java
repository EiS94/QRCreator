package schaubeck.eike.qrcreator.QRCode.encode;

import jpp.qrcode.Version;

public final class DataEncoderResult {

	byte[] bytes;
	Version version;

	public DataEncoderResult(byte[] bytes, Version version) {
		this.version = version;
		this.bytes = bytes;
	}

	public byte[] bytes() {
		return bytes;
	}

	public Version version() {
		return version;
	}
}
