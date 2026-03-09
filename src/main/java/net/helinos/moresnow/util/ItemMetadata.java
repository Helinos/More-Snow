package net.helinos.moresnow.util;

public class ItemMetadata {
	public static final int BLOCK_LENGTH = 16;
	public static final int NIBBLE_LENGTH = 8;
	private static final int MAX_METAVALUE = 0b1111_1111_1111_1111;

	private ItemMetadata() {/* no need to initiate*/}

	public static boolean isSet(int metadata, int index) {
		return (ItemMetadata.getBit(metadata, index) & 1) == 1;
	}

	public static int getBit(int metadata, int index) {
		if (index >= BLOCK_LENGTH || index < 0) {
			return Metadata.NOT_METADATA;
		}
		return ((metadata & MAX_METAVALUE) >>> index) & 1;
	}

	public static int getBitBlock(int metadata, int startIndex, int endIndex) {
		if (startIndex >= BLOCK_LENGTH || startIndex < 0 || endIndex >= BLOCK_LENGTH || endIndex < 0) {
			return Metadata.NOT_METADATA;
		}
		int len = endIndex - startIndex;
		if (len < 0) {
			return Metadata.NOT_METADATA;
		}
		if (len == 0) {
			return Metadata.rawGetBit(metadata & MAX_METAVALUE, startIndex);
		}
		return Metadata.rawGetBitBlock(BLOCK_LENGTH, metadata & MAX_METAVALUE, startIndex, len + 1);
	}

	public static int getUpperBlock(int metadata) {
		return Metadata.rawGetUpperBlock(NIBBLE_LENGTH, metadata & MAX_METAVALUE);
	}

	public static int getLowerBlock(int metadata) {
		return Metadata.rawGetLowerBlock(NIBBLE_LENGTH, metadata);
	}

	public static int setBit(int metadata, int index) {
		return ItemMetadata.setBit(metadata, index, 1);
	}

	public static int setBit(int metadata, int index, int value) {
		if (index >= BLOCK_LENGTH || index < 0) {
			return Metadata.NOT_METADATA;
		}
		return Metadata.rawSetBit(metadata & MAX_METAVALUE, index, value & 1);
	}

	public static int setBitBlock(int metadata, int startIndex, int endIndex, int value) {
		if (value > (MAX_METAVALUE >>> startIndex) || startIndex >= BLOCK_LENGTH || startIndex < 0 || startIndex > endIndex || endIndex > 8) {
			return Metadata.NOT_METADATA;
		}
		return Metadata.rawSetBitBlock(metadata & MAX_METAVALUE, startIndex, endIndex - startIndex + 1, value);
	}

	public static int flipBit(int metadata, int index) {
		if (index >= BLOCK_LENGTH || index < 0) {
			return Metadata.NOT_METADATA;
		}
		return Metadata.rawFlipBit(metadata & MAX_METAVALUE, index);
	}
}
