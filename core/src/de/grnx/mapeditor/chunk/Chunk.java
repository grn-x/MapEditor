package de.grnx.mapeditor.chunk;

import static de.grnx.mapeditor.canvas.World.world;

import de.grnx.mapeditor.buildableConf.Block;
import de.grnx.mapeditor.buildableConf.Blocks;
import de.grnx.mapeditor.canvas.World;

/** The chunk with  */
public class Chunk 
{
	/** Direct access to the block data. */
	public static final int SIZE = 16;
	
	/** The direct access to this block data. */
	public final byte[][][] blocks;
	
	/** The ChunkRegion of this chunk's parents. */
	public final Cregion region;
	
	/** Chunk position. */
	public final int x, y, z;
	
	/** If this chunk needs update their mesh. */
	public boolean isDirty = false;
	
	/** If this a new unloaded chunk. Then build the chunk model when player enters */
	public boolean isNewChunk = true;
	
	/** If this chunk safe to modify blocks in */
	public volatile boolean isChunkSafe = false;
	
	public Chunk(Cregion region, int xChunk, int yChunk, int zChunk)
	{
		this.region = region;
		this.x = xChunk;
		this.y = yChunk;
		this.z = zChunk;
		blocks = new byte[SIZE][SIZE][SIZE];
	}
	
	public byte getBlock(int x, int y, int z)
	{
		if (x < 0 || y < 0 || z < 0 || x > 15 || y > 15 || z > 15)
			return Blocks.AIR;
		
		return blocks[x][y][z];
	}
	
	public byte getBlockSmart(int x, int y, int z)
	{

		return world.getBlock(x, y, z);
	}
	
	public void setBlock(int x, int y, int z, byte ID)
	{
		if (x < 0 || y < 0 || z < 0 || x > 15 || y > 15 || z > 15)
			return;
		
		blocks[x][y][z] = ID;
	}
	
	/** &15 (mod) will be applied in this method. */
	public void editBlock(int x, int y, int z, Block block)
	{
		final int xFix   = x&15, yFix   = y&15, zFix   = z&15;
		final int xChunk = x>>4, yChunk = y>>4, zChunk = z>>4;
		blocks[xFix][yFix][zFix] = block.id;
		isDirty = true;
		
		Chunk chunk;
		final World world = getWorld();
		if (yFix == 0) {
			chunk = world.getChunk(xChunk, yChunk-1, zChunk);
			if (chunk != null) chunk.isDirty = true;
		}
		if (yFix == 15) {
			chunk = world.getChunk(xChunk, yChunk+1, zChunk);
			if (chunk != null) chunk.isDirty = true;
		}
		if (xFix == 0) {
			chunk = world.getChunk(xChunk-1, yChunk, zChunk);
			if (chunk != null) chunk.isDirty = true;
		}
		if (xFix == 15) {
			chunk = world.getChunk(xChunk+1, yChunk, zChunk);
			if (chunk != null) chunk.isDirty = true;
		}
		if (zFix == 0) {
			chunk = world.getChunk(xChunk, yChunk, zChunk-1);
			if (chunk != null) chunk.isDirty = true;
		}
		if (zFix == 15) {
			chunk = world.getChunk(xChunk, yChunk, zChunk+1);
			if (chunk != null) chunk.isDirty = true;
		}
	}
	
	public void clear() {
		for (int x = 0; x < SIZE; x++)
		{
			for (int y = 0; y < SIZE; y++)
			{
				for (int z = 0; z < SIZE; z++)
				{
					blocks[x][y][z] = Blocks.AIR;
				}
			}
		}		
	}
	
	public void setNewChunk(boolean isNew) {
		isNewChunk = isNew;
		isChunkSafe = isNew;
	}
	
	public World getWorld() {
		return region.world;
	}
}
