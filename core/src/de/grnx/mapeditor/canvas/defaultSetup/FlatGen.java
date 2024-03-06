package de.grnx.mapeditor.canvas.defaultSetup;

import de.grnx.mapeditor.buildableConf.Blocks;
import de.grnx.mapeditor.canvas.World;
import de.grnx.mapeditor.chunk.Chunk;
import de.grnx.mapeditor.chunk.Cregion;

//import de.grnx.mapeditor.world.gen.features.Lands;
//import de.grnx.mapeditor.world.gen.features.OrePatch;
//import de.grnx.mapeditor.world.gen.features.PlentPatch;
//import de.grnx.mapeditor.world.gen.features.Tree;
//import de.grnx.mapeditor.world.gen.structure.Structure;
//import de.grnx.mapeditor.world.gen.structure.StrutBuilder;


public class FlatGen{

	
	public FlatGen() {

	}
	

	public void gen(World world) {
		for (int x = 0; x < World.defaultSize; x++)
		{
			for (int z = 0; z < World.defaultSize; z++)
			{
				final Cregion region = world.regions[x][z];
				genRegion(region);
				//dec(region);
			}
		}
		
		//worldGen(world);
	}
	
	private void genRegion(Cregion region)
	{
		for (int x = 0; x < Chunk.SIZE; x++)
		{
			float realX = x + (region.xR*Chunk.SIZE);
			for (int z = 0; z < Chunk.SIZE; z++)
			{
				float value = 0.5f;//lands.GetPerlinFractal(realX, z + (region.zR*Chunk.SIZE));
				value *= 38f; // 35
				//System.out.println("Noise: " + value);
				for (int y = 0; y < Cregion.HEIGHT; y++)
				{				
					if (y-46 < value) { // 30
						region.setBlockChunk(x, y, z, Blocks.BEDROCK);
					}
				}
			}
		}
	}
	


}
