package de.grnx.mapeditor.buildableConf;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapGroupLayer;

import de.grnx.mapeditor.Options;
import de.grnx.mapeditor.helper.filehandler.FileHandler;
import de.grnx.mapeditor.texture.ManagerInit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

public final class Blocks {
	private static byte i = 0;
	//public static final byte AIR = i++, UNSUPPORTED = i++, BEDROCK = i++, MARKER = i++, STONE = i++, OAK_LOG = i++,  GRAY_GLASS = i++, RESPAWN_ANCHOR = i++,IRON_BLOCK = i++;
	public static final byte AIR = i++, UNSUPPORTED = i++, /*MISSING = i++,*/ BEDROCK = i++, MARKER = i++, GENERIC_TILE = i++,GLASS = i++, UNCOLORED_TILE = i++, BLUE = i++, BROWN = i++, DARK_BLUE = i++, FLAX = i++, FOREST_GREEN = i++, FUCHSIA = i++,
            GRASS_GREEN = i++, GREEN = i++, GREY = i++, LIGHT_BLUE = i++, LILAC = i++, MAUVE = i++,  MUSTARD = i++, ORANGE = i++, PINK = i++,
            PURPLE = i++, SKY_BLUE = i++, TEAL = i++, TURQUOISE = i++, 
            VERMILLIA = i++, VIOLET = i++, YELLOW = i++;
	

//	boolean bool = canAddFace(new Block((byte) 2, "", false , BlockType.AIR), 1);
	public static final Block[] blocks_internal = new Block[i];
	public static Block[] blocks_external;
	public static List<String> extTextsMGR = new ArrayList<String>();

//	public static final Block[] blocks =new Block[blocks_atlas.length+10];
	public static Block[] blocks;

	/** Make sure the textures loaded first. */
	public static void loadBlocks() {
		blocks_internal[AIR] = new Block(AIR, "Air", false, BlockType.AIR);
		blocks_internal[UNSUPPORTED] = new Block(UNSUPPORTED, "Unsupported Block", true, false, true, BlockType.STONE);
		blocks_internal[BEDROCK] = new Block(BEDROCK, "Bedrock", true, false, true, BlockType.STONE);
		/*blocks_internal[STONE] = new Block(STONE, "Stone", true, BlockType.STONE);
		blocks_internal[OAK_LOG] = new Block(OAK_LOG, "Oak Log", true, BlockType.WOOD);
		blocks_internal[GRAY_GLASS] = new Block(GRAY_GLASS, "Gray Glass", false, true, true, BlockType.GLASS);
		blocks_internal[RESPAWN_ANCHOR] = new Block(RESPAWN_ANCHOR, "Respawn Anchor", true, BlockType.STONE);
		blocks_internal[IRON_BLOCK] = new Block(IRON_BLOCK, "Iron Block", true, BlockType.METAL);
		*/
		blocks_internal[MARKER] = new Block(MARKER, "Mark Spots", false, true, false, BlockType.GLASS);
	    blocks_internal[BLUE] = new Block(BLUE, "Blue", true, BlockType.STONE);
	    blocks_internal[BROWN] = new Block(BROWN, "Brown", true, BlockType.STONE);
	    blocks_internal[DARK_BLUE] = new Block(DARK_BLUE, "Dark Blue", true, BlockType.STONE);
	    blocks_internal[FLAX] = new Block(FLAX, "Flax", true, BlockType.STONE);
	    blocks_internal[FOREST_GREEN] = new Block(FOREST_GREEN, "Forest Green", true, BlockType.STONE);
	    blocks_internal[FUCHSIA] = new Block(FUCHSIA, "Fuchsia", true, BlockType.STONE);
	    blocks_internal[GENERIC_TILE] = new Block(GENERIC_TILE, "Generic Tile", true, BlockType.STONE);
	    blocks_internal[GLASS] = new Block(GLASS, "Glass", false, true, true, BlockType.GLASS);
	    blocks_internal[GRASS_GREEN] = new Block(GRASS_GREEN, "Grass Green", true, BlockType.STONE);
	    blocks_internal[GREEN] = new Block(GREEN, "Green", true, BlockType.STONE);
	    blocks_internal[GREY] = new Block(GREY, "Grey", true, BlockType.STONE);
	    blocks_internal[LIGHT_BLUE] = new Block(LIGHT_BLUE, "Light Blue", true, BlockType.STONE);
	    blocks_internal[LILAC] = new Block(LILAC, "Lilac", true, BlockType.STONE);
//	     blocks_internal[MARKER_NEW] = new Block(MARKER_NEW, "Marker New", false, true, false, BlockType.GLASS);
	    blocks_internal[MAUVE] = new Block(MAUVE, "Mauve", true, BlockType.STONE);
//	    blocks_internal[MISSING] = new Block(MISSING, "Missing", true, BlockType.STONE);
	    blocks_internal[MUSTARD] = new Block(MUSTARD, "Mustard", true, BlockType.STONE);
	    blocks_internal[ORANGE] = new Block(ORANGE, "Orange", true, BlockType.STONE);
	    blocks_internal[PINK] = new Block(PINK, "Pink", true, BlockType.STONE);
	    blocks_internal[PURPLE] = new Block(PURPLE, "Purple", true, BlockType.STONE);
	    blocks_internal[SKY_BLUE] = new Block(SKY_BLUE, "Sky Blue", true, BlockType.STONE);
	    blocks_internal[TEAL] = new Block(TEAL, "Teal", true, BlockType.STONE);
	    blocks_internal[TURQUOISE] = new Block(TURQUOISE, "Turquoise", true, BlockType.STONE);
	    blocks_internal[UNCOLORED_TILE] = new Block(UNCOLORED_TILE, "Uncolored Tile", true, BlockType.STONE);
//	    blocks_internal[UNSUPPORTED_NEW] = new Block(UNSUPPORTED_NEW, "Unsupported New", true, BlockType.STONE);
	    blocks_internal[VERMILLIA] = new Block(VERMILLIA, "Vermillia", true, BlockType.STONE);
	    blocks_internal[VIOLET] = new Block(VIOLET, "Violet", true, BlockType.STONE);
	    blocks_internal[YELLOW] = new Block(YELLOW, "Yellow", true, BlockType.STONE);

		
		
		blocks = blocks_internal;// will get overwritten if external blocks get loaded
		loadExternalBlocks();

	}

	public static void loadTextures(AssetManager mgr) {
		/*
		
		blocks_internal[STONE].tex(mgr, ManagerInit.prefixPath + "stone.png");
		blocks_internal[OAK_LOG].tex(mgr, ManagerInit.prefixPath + "oak_log.png",
				ManagerInit.prefixPath + "oak_log_top.png");
		blocks_internal[GRAY_GLASS].tex(mgr, ManagerInit.prefixPath + "glass.png");
		blocks_internal[RESPAWN_ANCHOR].tex(mgr, ManagerInit.prefixPath + "respawn_anchor_side.png",
				ManagerInit.prefixPath + "respawn_anchor_top.png", ManagerInit.prefixPath + "respawn_anchor_bottom.png");
		blocks_internal[IRON_BLOCK].tex(mgr, ManagerInit.prefixPath + "ironblock.png");
		*/

		blocks_internal[BEDROCK].tex(mgr, ManagerInit.prefixPath + "bedrock.png");
		blocks_internal[UNSUPPORTED].tex(mgr, ManagerInit.prefixPath + "unsupported.png");
		blocks_internal[MARKER].tex(mgr, ManagerInit.prefixPath + "marker.png");
		
	    blocks_internal[BLUE].tex(mgr, ManagerInit.prefixPath + "blue.png");
	    blocks_internal[BROWN].tex(mgr, ManagerInit.prefixPath + "brown.png");
	    blocks_internal[DARK_BLUE].tex(mgr, ManagerInit.prefixPath + "dark-blue.png");
	    blocks_internal[FLAX].tex(mgr, ManagerInit.prefixPath + "flax.png");
	    blocks_internal[FOREST_GREEN].tex(mgr, ManagerInit.prefixPath + "forest-green.png");
	    blocks_internal[FUCHSIA].tex(mgr, ManagerInit.prefixPath + "fuchsia.png");
	    blocks_internal[GENERIC_TILE].tex(mgr, ManagerInit.prefixPath + "generic-tile.png");
	    blocks_internal[GLASS].tex(mgr, ManagerInit.prefixPath + "glass.png");
	    blocks_internal[GRASS_GREEN].tex(mgr, ManagerInit.prefixPath + "grass-green.png");
	    blocks_internal[GREEN].tex(mgr, ManagerInit.prefixPath + "green.png");
	    blocks_internal[GREY].tex(mgr, ManagerInit.prefixPath + "grey.png");
	    blocks_internal[LIGHT_BLUE].tex(mgr, ManagerInit.prefixPath + "light-blue.png");
	    blocks_internal[LILAC].tex(mgr, ManagerInit.prefixPath + "lilac.png");
//	    blocks_internal[MARKER_NEW].tex(mgr, ManagerInit.prefixPath + "marker.png");
	    blocks_internal[MAUVE].tex(mgr, ManagerInit.prefixPath + "mauve.png");
//	    blocks_internal[MISSING].tex(mgr, ManagerInit.prefixPath + "missing.png");
	    blocks_internal[MUSTARD].tex(mgr, ManagerInit.prefixPath + "mustard.png");
	    blocks_internal[ORANGE].tex(mgr, ManagerInit.prefixPath + "orange.png");
	    blocks_internal[PINK].tex(mgr, ManagerInit.prefixPath + "pink.png");
	    blocks_internal[PURPLE].tex(mgr, ManagerInit.prefixPath + "purple.png");
	    blocks_internal[SKY_BLUE].tex(mgr, ManagerInit.prefixPath + "sky-blue.png");
	    blocks_internal[TEAL].tex(mgr, ManagerInit.prefixPath + "teal.png");
	    blocks_internal[TURQUOISE].tex(mgr, ManagerInit.prefixPath + "turquoise.png");
	    blocks_internal[UNCOLORED_TILE].tex(mgr, ManagerInit.prefixPath + "uncolored-tile.png");
//	    blocks_internal[UNSUPPORTED_NEW].tex(mgr, ManagerInit.prefixPath + "unsupported.png");
	    blocks_internal[VERMILLIA].tex(mgr, ManagerInit.prefixPath + "vermillia.png");
	    blocks_internal[VIOLET].tex(mgr, ManagerInit.prefixPath + "violet.png");
	    blocks_internal[YELLOW].tex(mgr, ManagerInit.prefixPath + "yellows.png");

		
		checkExternalTextures(mgr);

	}

	private static void loadExternalBlocks() {
		if (!new File(Options.externalBlockPath).exists())
			return;
		ExternalBlockLoaderIterable iteratable = new ExternalBlockLoaderIterable(Options.externalBlockPath);
		blocks_external = new Block[iteratable.size];
		byte externalSize = 0;

		for (JsonObject blockObject : iteratable) {
			String name = blockObject.getString("name").contains(":") ? blockObject.getString("name")
					: "ext:".concat(blockObject.getString("name"));
			JsonArray textureArray = blockObject.getJsonArray("texture");
			boolean solid = blockObject.getBoolean("solid");
			boolean transparent = blockObject.getBoolean("transparent");
			boolean collision = blockObject.getBoolean("collision");
			String type = blockObject.getString("type");

			ArrayList<String> textureList = new ArrayList<String>();
			for (JsonValue value : textureArray) {
				textureList.add(value.toString().replaceAll("^\"|\"$", "")); // Adjust this based on the actual JSON
																				// structure
			}
			// combine everything into single expression/function call? :DDDD
			blocks_external[externalSize++] = new Block(i++, name, solid, transparent, collision,
					BlockType.parseBlockType(type), lET_(textureList));

		}

//		blocks = blocks_internal;
		blocks = new Block[blocks_internal.length + blocks_external.length];
		System.arraycopy(blocks_internal, 0, blocks, 0, blocks_internal.length);
		System.arraycopy(blocks_external, 0, blocks, blocks_internal.length, blocks_external.length);// blocks_length
	}

	private static void checkExternalTextures(AssetManager mgr) {
		if(blocks_external==null)return;
		for (Block b : blocks_external) {
			b.texLoaded(mgr);
		}

		

	}

	/** loadExternalTextures */
	public static String[] lET(Object... object) {
		// extTextsMGR.addAll(List.of(strings)); //fucking java 7
		extTextsMGR.addAll(Arrays.asList((String[]) object));// this might be highly inefficient, but it looks neat, and
																// who cares, i optimize my code at places where it
																// matters, and here it certainly does not.
		return (String[]) object;
	}

	/** loadExternalTextures */
	public static String[] lET_(List<String> str) {
		extTextsMGR.addAll(str);
		return str.toArray(new String[str.size()]);
	}

	public static boolean canAddFace(Block block, int id) {
		if (id == AIR)
			return true;
		//try{
			final Block secondary = blocks[id];
		
		if (block.isSolid == secondary.isSolid)
			return false;
		if (block.isSolid == true && secondary.isSolid == false)
			return true; // primary is solid and secondary is trans.
		if (block.isSolid == false && secondary.isSolid == true)
			return false; // primary is trans and secondary is solid.
		return false;
		//}catch(Exception e) {return true;}
			//crashes when unsupported inpord id out of range
			
		
	}
}
