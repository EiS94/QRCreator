package schaubeck.eike.qrcreator.QRCode.decode;

import jpp.qrcode.ErrorCorrection;
import jpp.qrcode.reedsolomon.ReedSolomonException;
import schaubeck.eike.qrcreator.QRCode.MaskApplier;
import schaubeck.eike.qrcreator.QRCode.QRCode;
import schaubeck.eike.qrcreator.QRCode.ReservedModulesMask;

public class Decoder {
	public static String decodeToString(QRCode qrCode) throws ReedSolomonException {
		boolean[][] matrix = qrCode.data().clone();
		ReservedModulesMask mask = ReservedModulesMask.forVersion(qrCode.version());
		MaskApplier.applyTo(matrix, qrCode.maskPattern().maskFunction(), mask);
		byte[] extracted = DataExtractor.extract(matrix, mask, qrCode.version().totalByteCount());
		ErrorCorrection correction = qrCode.errorCorrection();
		extracted = DataDestructurer.destructure(extracted, qrCode.version().correctionInformationFor(correction));
		String output = DataDecoder.decodeToString(extracted, qrCode.version(), correction);
		return output;
	}
}
