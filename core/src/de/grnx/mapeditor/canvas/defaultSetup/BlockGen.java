package de.grnx.mapeditor.canvas.defaultSetup;
import de.grnx.mapeditor.buildableConf.Blocks;
import de.grnx.mapeditor.canvas.World;
import de.grnx.mapeditor.chunk.Chunk;
import de.grnx.mapeditor.chunk.Cregion;

public class BlockGen{


    public void gen(World world){
        for (int x = 0; x < World.defaultSize; x++)
        {
            for (int z = 0; z < World.defaultSize; z++)
            {
                final Cregion region = world.regions[x][z];
            	//region.setBlockChunk(World.CENTER, 1, World.CENTER, Blocks.STONE);
            	region.setBlock(x, 0, z, Blocks.BEDROCK);
                //genRegion(region);
            }
        }

    }

    private void genRegion(Cregion region)
    {
    	
    	region.setBlockChunk(World.CENTER, 1, World.CENTER, Blocks.BEDROCK);
        for (int x = 0; x < Chunk.SIZE; x++)
        {
            for (int z = 0; z < Chunk.SIZE; z++)
            {

                //region.setBlockChunk(x, 1, z, Blocks.STONE);
                for (int y = 0; y < Cregion.HEIGHT; y++)
                {

                }
            }
        }
    }
    }


