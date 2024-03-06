package de.grnx.mapeditor.canvas.defaultSetup;
import de.grnx.mapeditor.buildableConf.Blocks;
import de.grnx.mapeditor.canvas.World;
import de.grnx.mapeditor.chunk.Chunk;
import de.grnx.mapeditor.chunk.Cregion;

public class SimpleGen{


    public void gen(World world){
        for (int x = 0; x < World.defaultSize; x++)
        {
            for (int z = 0; z < World.defaultSize; z++)
            {
                final Cregion region = world.regions[x][z];
                genRegion(region);
                //dec(region);
            }
        }

    }

    private void genRegion_dpr(Cregion region)
    {
        for (int x = 0; x < Chunk.SIZE; x++)
        {
            float realX = x + (region.xR*Chunk.SIZE);
            for (int z = 0; z < Chunk.SIZE; z++)
            {

                region.setBlockChunk(x, 0, z, Blocks.BEDROCK);
                region.setBlockChunk(x, 1, z, Blocks.BEDROCK);
                region.setBlockChunk(x, 2, z, Blocks.BEDROCK);

                region.setBlockChunkf(x, 0, z, Blocks.BEDROCK);


            }
            }
        }


    private void genRegion(Cregion region)
    {
        for (int x = 0; x < Chunk.SIZE; x++)
        {
            //float realX = x + (region.xR*Chunk.SIZE);
            for (int z = 0; z < Chunk.SIZE; z++)
            {

                region.setBlockChunk(x, 1, z, Blocks.BEDROCK);
//                region.setBlockChunk(x, (int)ChunkRegion.HEIGHT/2, z, Blocks.STONE);



                //float value =1000f; // 35
                for (int y = 0; y < Cregion.HEIGHT; y++)
                {

                }
            }
        }
    }
    }


