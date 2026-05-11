package net.helinos.moresnow.util;

public class Metadata {
	public static final int NOT_METADATA = -1;

	private Metadata() {/* no need to initiate*/}

	protected static int rawFlipBit(int metadata, int index) {
		return Metadata.rawSetBit(metadata, index, 1 - Metadata.rawGetBit(metadata, index));
	}

	protected static int rawSetBit(int metadata, int index, int value) {
		if (value == 0) {
			return metadata & ~(1 << index);
		}
		return metadata | (1 << index);
	}

	protected static int rawSetBitBlock(int metadata, int startIndex, int bitBlockLength, int value) {
		int mask = ((1 << bitBlockLength) - 1) << startIndex;
		return (metadata & ~mask) | ((value << startIndex) & mask);
	}

	protected static int rawGetBit(int metadata, int index) {
		return (metadata >>> index) & 1;
	}


	protected static int rawGetUpperBlock(int maskLength, int metadata){
		int mask = (1 << maskLength) - 1;
		return (metadata >>> maskLength) & mask;
	}
	protected static int rawGetLowerBlock(int maskLength, int metadata){
		int mask = (1 << maskLength) - 1;
		return metadata & mask;
	}
	protected static int rawGetBitBlock(int blockLength, int metadata, int startIndex, int len){
		int mask = (1 << len) - 1;
		return ((metadata & ((1 << blockLength) - 1)) >>> startIndex) & mask;
	}
}
