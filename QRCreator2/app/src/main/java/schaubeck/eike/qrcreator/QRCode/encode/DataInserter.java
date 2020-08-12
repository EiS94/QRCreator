package schaubeck.eike.qrcreator.QRCode.encode;

import schaubeck.eike.qrcreator.QRCode.DataPositions;
import schaubeck.eike.qrcreator.QRCode.ReservedModulesMask;

public class DataInserter {
	public static void insert(boolean[][] target, ReservedModulesMask mask, byte[] data) {
		DataPositions positions = new DataPositions(mask);
		int i = positions.i();
		int j = positions.j();
		for (int k = 0; k < data.length; k++) {
			for (int b = 7; b >= 0; b--) {
				if ((data[k] & (1 << b)) != 0) target[i][j] = true;
				else target[i][j] = false;
				positions.next();
				i = positions.i();
				j = positions.j();
			}
		}
	}
}
