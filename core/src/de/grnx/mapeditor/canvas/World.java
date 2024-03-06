package de.grnx.mapeditor.canvas;

import de.grnx.mapeditor.buildableConf.Blocks;
import de.grnx.mapeditor.canvas.defaultSetup.SimpleGen;
import de.grnx.mapeditor.chunk.Chunk;
import de.grnx.mapeditor.chunk.Cregion;
import de.grnx.mapeditor.helper.BlockPos;
import de.grnx.mapeditor.helper.Camera;
import de.grnx.mapeditor.helper.Util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

public class World implements Disposable
{
	public static World world;
	
	public static final int defaultSize = 64; // 64
	public static final int renderDist =24;
	public static final int LENGTH = defaultSize*Chunk.SIZE;
	public static final int CENTER = LENGTH /2;
	
	public final Cregion[][] regions;
	
	private WorldRenderer render;
	//public IParticleSystem parts;
	
	public final EditableInterface editable = new EditableInterface(this);
	
	public World(boolean gen) {
		world = this;
		regions = new Cregion[defaultSize][defaultSize];
		for (int x = 0; x < defaultSize; x++)
		for (int z = 0; z < defaultSize; z++) {
			regions[x][z] = new Cregion(this, x, z);
		}
		if (gen) {
			//new FlatGen().gen(this);
			new SimpleGen().gen(this);
			//new BlockGen().gen(this);
			/* TODO: height lighting disabled.
			for (int x = 0; x < defaultSize; x++)
			{
				for (int z = 0; z < defaultSize; z++)
				{
					regions[x][z].reLighting();;
				}
			} */
		}
		
	}
	
	public void intsRender(Camera cam, int maxDis) {
		render = new WorldRenderer(this, maxDis);
	}
	
	public void render(Camera cam) {
		
		WorldRenderer.indices.bind();
		render.render(cam);
		//parts.render();
		WorldRenderer.indices.unbind();
		
	}
	
	public Cregion getChunkRegion(int x, int z) {
		if (x < 0 || z < 0 || x >= defaultSize || z >= defaultSize) return null;
		return regions[x][z];
	}
	
	public byte getBlock(int x, int y, int z) {
		if (y < 0 || y >= Cregion.HEIGHT) return Blocks.AIR;
		Cregion region = getChunkRegion(x>>4, z>>4);
		return region == null ? Blocks.AIR : region.getBlock(x, y, z);
	}
	
	public void setBlock(int x, int y, int z, byte id) {
		if (y < 0 || y >= Cregion.HEIGHT) return;
		Cregion region = getChunkRegion(x>>4, z>>4);
		if (region == null) return;
		region.setBlock(x, y, z, id);
	}
	
	/** */
	public void forceDirty() {
		render.forceDirty = true;
	}

	public byte getBlock(float x, float y, float z) {
		return getBlock(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(z));
	}

	public void setBlock(BlockPos pos, byte block) {
		setBlock(pos.x, pos.y, pos.z, block);
	}

	public short getLight(int x, int z) {
		Cregion region = getChunkRegion(x>>4, z>>4);
		return region == null ? 0 : region.getLight(x, z);
	}
	
	public Chunk getChunk(int x, int y, int z) {
		Cregion region = getChunkRegion(x, z);
		return region == null ? null : region.getChunk(y);
	}
	
	public Chunk getChunkAt(int x, int y, int z) {
		return getChunk(x>>4, y>>4, z>>4);
	}
	
	@Override
	public void dispose() {
		Util.disposes(render);
		//added
	}
}
