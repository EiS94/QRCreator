package schaubeck.eike.qrcreator.QRCode.encode;

import jpp.qrcode.ErrorCorrection;
import jpp.qrcode.MaskPattern;
import schaubeck.eike.qrcreator.QRCode.QRCode;
import schaubeck.eike.qrcreator.QRCode.ReservedModulesMask;

import java.io.UnsupportedEncodingException;

public class Encoder {
	public static QRCode createFromString(String msg, ErrorCorrection correction) throws UnsupportedEncodingException {
		DataEncoderResult result = DataEncoder.encodeForCorrectionLevel(msg, correction);
		byte[] data = DataStructurer.structure(result.bytes, result.version.correctionInformationFor(correction));
		boolean[][] matrix = PatternPlacer.createBlankForVersion(result.version);
		ReservedModulesMask mask = ReservedModulesMask.forVersion(result.version);
		DataInserter.insert(matrix, mask, data);
		MaskPattern pattern = MaskSelector.maskWithBestMask(matrix, correction, mask);
		QRCode qrCode = new QRCode(matrix, result.version, pattern, correction);
		return qrCode;
	}
}
