package schaubeck.eike.qrcreator.QRCode.encode;


import jpp.qrcode.Version;
import jpp.qrcode.VersionInformation;
import schaubeck.eike.qrcreator.QRCode.QRCode;
import schaubeck.eike.qrcreator.QRCode.Tuple;

import java.util.ArrayList;
import java.util.List;

public class PatternPlacer {
	public static void placeOrientation(boolean[][] res, Version version) {
		QRCode.orientationPatternPosition(0,0, res);
		QRCode.orientationPatternPosition(res.length - 7,0, res);
		QRCode.orientationPatternPosition(0, res.length - 7, res);
	}
	
	public static void placeTiming(boolean[][] res, Version version) {
		boolean alternatly = true;
		int x = 8;
		int y = 8;
		while (x <= res.length - 9) {
			res[6][x] = alternatly;
			if (alternatly == true) alternatly = false;
			else alternatly = true;
			x++;
		}
		alternatly = true;
		while (y <= res.length - 9) {
			res[y][6] = alternatly;
			if (alternatly == true) alternatly = false;
			else alternatly = true;
			y++;
		}
	}
	
	public static void placeAlignment(boolean[][] res, Version version) {
		List<Tuple> tuples = new ArrayList<Tuple>();
		int[] alignmentPositions = version.alignmentPositions();
		boolean correctAlignment = true;
		for (int i = 0; i < alignmentPositions.length; i++) {
			for (int j = 0; j < alignmentPositions.length; j++) {
				tuples.add(new Tuple(alignmentPositions[i], alignmentPositions[j]));
			}
		}
		for (Tuple tuplesTemp : tuples) {
			if (tuplesTemp.getX() - 2 <= 6 && tuplesTemp.getY() - 2 <= 6 || tuplesTemp.getX() + 2 >= res.length - 6 &&
					tuplesTemp.getY() - 2 <= 6 || tuplesTemp.getX() - 2 <= 6 && tuplesTemp.getY() + 2 >= res.length - 6) {
			} else {
				setAlignmentPattern(tuplesTemp.getX(), tuplesTemp.getY(), res);
			}
		}
	}

	public static void setAlignmentPattern(int i, int j, boolean[][] data) {
		int xBegin = j - 2;
		int xEnd = j + 2;
		int yBegin = i - 2;
		int yEnd = i + 2;
		for (int x = xBegin; x <= xEnd; x++) {
			data[yBegin][x] = true;
			data[yEnd][x] = true;
		}
		data[yBegin + 1][xBegin] = true;
		data[yBegin + 2][xBegin] = true;
		data[yBegin + 3][xBegin] = true;
		data[yBegin + 1][xEnd] = true;
		data[yBegin + 2][xEnd] = true;
		data[yBegin + 3][xEnd] = true;
		data[i][j] = true;
		data[yBegin + 1][xBegin + 1] = false;
		data[yBegin + 1][xBegin + 2] = false;
		data[yBegin + 1][xBegin + 3] = false;
		data[yBegin + 2][xBegin + 1] = false;
		data[yBegin + 2][xBegin + 3] = false;
		data[yBegin + 3][xBegin + 1] = false;
		data[yBegin + 3][xBegin + 2] = false;
		data[yBegin + 3][xBegin + 3] = false;
	}
	
	public static void placeVersionInformation(boolean[][] data, int versionInformation) {
		if (data.length >= 45) {

			// Place Upper VersionInfo
			byte low = (byte) versionInformation;
			byte middle = (byte) (versionInformation >> 8);
			byte high = (byte) (versionInformation >> 16);
			int[] bits = new int[8];

			for (int i = 0; i <= 7; i++) {
				if ((low & (1 << i)) != 0) bits[i] = 1;
				else bits[i] = 0;
			}
			int counter = 0;
			for (int j = 0; j <= 2; j++) {
				for (int i = data.length - 11; i <= data.length - 9; i++) {
					if (counter <= 7) {
						if (bits[counter] == 1) data[i][j] = true;
						counter++;
					}
				}
			}

			for (int i = 0; i <= 7; i++) {
				if ((middle & (1 << i)) != 0) bits[i] = 1;
				else bits[i] = 0;
			}
			if (bits[0] == 1) data[2][data.length - 9] = true;
			counter = 1;
			for (int i = 3; i <= 5; i++) {
				for (int j = data.length - 11; j <= data.length - 9; j++) {
					if (counter <= 7) {
						if (bits[counter] == 1) data[i][j] = true;
						counter++;
					}
				}
			}
			if ((high & (1 << 0)) != 0) data[5][data.length - 10] = true;
			if ((high & (1 << 1)) != 0) data[5][data.length - 9] = true;

			//Place Down VersionInfo
			for (int i = 0; i <= 7; i++) {
				if ((low & (1 << i)) != 0) bits[i] = 1;
				else bits[i] = 0;
			}
			counter = 0;
			for (int i = 0; i <= 2; i++) {
				for (int j = data.length - 11; j <= data.length - 9; j++) {
					if (counter <= 7) {
						if (bits[counter] == 1) data[i][j] = true;
						counter++;
					}
				}
			}

			for (int i = 0; i <= 7; i++) {
				if ((middle & (1 << i)) != 0) bits[i] = 1;
				else bits[i] = 0;
			}
			if (bits[0] == 1) data[data.length - 9][2] = true;
			counter = 1;
			for (int j = 3; j <= 5; j++) {
				for (int i = data.length - 11; i <= data.length - 9; i++) {
					if (counter <= 7) {
						if (bits[counter] == 1) data[i][j] = true;
						counter++;
					}
				}
			}
			if ((high & (1 << 0)) != 0) data[data.length - 10][5] = true;
			if ((high & (1 << 1)) != 0) data[data.length - 9][5] = true;

		}
	}
	
	public static boolean[][] createBlankForVersion(Version version) {
		boolean[][] data = new boolean[version.size()][version.size()];
		placeOrientation(data, version);
		placeTiming(data, version);
		placeAlignment(data, version);
		data[version.size() - 8][8] = true;
		if (version.number() > 6) {
			placeVersionInformation(data, VersionInformation.forVersion(version));
		}
		return data;
	}
}
