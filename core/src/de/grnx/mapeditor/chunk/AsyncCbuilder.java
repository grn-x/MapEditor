package de.grnx.mapeditor.chunk;

import de.grnx.mapeditor.buildableConf.Block;
import de.grnx.mapeditor.buildableConf.Blocks;
import de.grnx.mapeditor.canvas.World;
import de.grnx.mapeditor.gles30.mesh.ChunkMesh;
import de.grnx.mapeditor.gles30.mesh.TerrainBuilder;

import static de.grnx.mapeditor.buildableConf.Blocks.canAddFace;

import com.badlogic.gdx.utils.Disposable;


public class AsyncCbuilder
{
	private final World world;

	private final TerrainBuilder tBuild = new TerrainBuilder();

	private final VolatileMeshPacket packet = new VolatileMeshPacket();

	public AsyncCbuilder(final World world) {
		this.world = world;
	}

	public VolatileMeshPacket all(final Chunk chunk, VolatileMeshPacket packet)
	{
		final Cregion tempNorth = world.getChunkRegion(chunk.x, chunk.z+1);
		final Cregion tempSouth = world.getChunkRegion(chunk.x, chunk.z-1);
		final Cregion tempEast = world.getChunkRegion(chunk.x+1, chunk.z);
		final Cregion tempWest = world.getChunkRegion(chunk.x-1, chunk.z);
		final Cregion temp = world.getChunkRegion(chunk.x, chunk.z);

		final int size = Chunk.SIZE;
		final int maskSize = size-1;
		final int sizeX = chunk.x*size;
		final int sizeY = chunk.y*size;
		final int sizeZ = chunk.z*size;
		final byte[][][] blocks = chunk.blocks;

		for (int x = 0; x < size; x++)
		{
			for (int y = 0; y < size; y++)
			{
				for (int z = 0; z < size; z++)
				{
					final byte id = blocks[x][y][z];
					if (id == Blocks.AIR) continue;
					final Block block = Blocks.blocks[id];



					// check south Z-
					if (z-1 == -1)
					{
						if (tempSouth != null) {
							if (canAddFace(block, tempSouth.chunks[chunk.y].blocks[x][y][z+maskSize])) {
								tBuild.bSouth(block, chunk, x+sizeX, y+sizeY, z+sizeZ);

							}
						}
					}  else {
						if (canAddFace(block, blocks[x][y][z-1])) {
							tBuild.bSouth(block, chunk, x+sizeX, y+sizeY, z+sizeZ);

						}
					}

					// check north Z+
					if (z+1 == size)
					{
						if (tempNorth != null) {
							if (canAddFace(block, tempNorth.chunks[chunk.y].blocks[x][y][z-maskSize])) {
								tBuild.bNorth(block, chunk, x+sizeX, y+sizeY, z+sizeZ);

							}
						}
					} else if (canAddFace(block, blocks[x][y][z+1])) {
						tBuild.bNorth(block, chunk, x+sizeX, y+sizeY, z+sizeZ);

					}

					// check west X-
					if (x-1 == -1)
					{
						if (tempWest != null) {
							if (canAddFace(block, tempWest.chunks[chunk.y].blocks[x+maskSize][y][z])) {
								tBuild.bWest(block, chunk, x+sizeX, y+sizeY, z+sizeZ);

							}
						}
					} else if (canAddFace(block, blocks[x-1][y][z])) {
						tBuild.bWest(block, chunk, x+sizeX, y+sizeY, z+sizeZ);

					}

					// check east X+
					if (x+1 == size)
					{
						if (tempEast != null)	{
							if (canAddFace(block, tempEast.chunks[chunk.y].blocks[x-maskSize][y][z])) {
								tBuild.bEast(block, chunk, x+sizeX, y+sizeY, z+sizeZ);

							}
						}
					} else if (canAddFace(block, blocks[x+1][y][z])) {
						tBuild.bEast(block, chunk, x+sizeX, y+sizeY, z+sizeZ);

					}

					// check up Y+
					if (y+1 == size)
					{
						Chunk chunk1 = temp.getChunk(chunk.y+1);
						if (chunk1 != null) {
							if (canAddFace(block, chunk1.blocks[x][y-maskSize][z])) {
								tBuild.bTop(block, chunk, x+sizeX, y+sizeY, z+sizeZ);

							}
						}
					} else if (canAddFace(block, blocks[x][y+1][z])) {
						tBuild.bTop(block, chunk, x+sizeX, y+sizeY, z+sizeZ);

					}

					// check down Y-
					if (y-1 == -1)
					{
						Chunk chunk1 = temp.getChunk(chunk.y-1);
						if (chunk1 != null) {
							if (canAddFace(block, chunk1.blocks[x][y+maskSize][z])) {
								tBuild.bBottom(block, chunk, x+sizeX, y+sizeY, z+sizeZ);

							}
						}
					} else if (canAddFace(block, blocks[x][y-1][z])) {
						tBuild.bBottom(block, chunk, x+sizeX, y+sizeY, z+sizeZ);

					}
				}
			}
		}

		packet = packet == null ? this.packet : packet;
		packet.terrain = tBuild.create(chunk);
		packet.chunk = chunk;
		return packet;
	}

	public static class VolatileMeshPacket implements Disposable
	{
		public volatile ChunkMesh terrain, plant, water;
		public volatile Chunk chunk;

		public boolean isEmpty() {
			return terrain == null && plant == null && water == null;
		}

		@Override
		public void dispose() {
			if (terrain != null) terrain.dispose();
			if (plant != null) plant.dispose();
			if (water != null) water.dispose();
			if (chunk != null) {
				chunk.setNewChunk(true);
			}
		}
	}
}

