package de.grnx.mapeditor.buildableConf;

public enum BlockType {
	AIR, STONE, SOIL, WOOD, GLASS,  SAND, WOOL, METAL, GRAVEL; //mostly unimplemented as of yet, could be used for custom models
	public static BlockType parseBlockType(String typeString) {
		try {
			return BlockType.valueOf(typeString.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			return BlockType.STONE; // Default to STONE
		}
	}
}
