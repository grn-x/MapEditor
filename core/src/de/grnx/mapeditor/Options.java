package de.grnx.mapeditor;

import static de.grnx.mapeditor.buildableConf.Blocks.blocks;

import java.io.File;

import de.grnx.mapeditor.buildableConf.Block;
import de.grnx.mapeditor.buildableConf.Blocks;

public final class Options{
	
	public static boolean mouseCaught = false;
		
	public static final boolean jarExport = true;
	
	//public static final Block bottom_unbreakable = blocks[Blocks.BEDROCK];
	//String currentJar = new File(MapEditorMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
	public static final String externalBlockPath = jarExport==false?"C:\\Users\\bened\\eclipse-workspace\\libGDXmapEditorLWJGL2\\.ExternalBlocks\\block.json":".\\blocks.json";
	
}
