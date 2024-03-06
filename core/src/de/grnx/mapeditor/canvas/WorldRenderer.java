package de.grnx.mapeditor.canvas;

//import de.grnx.mapeditor.Options;
//import de.grnx.mapeditor.PartiBench;
import de.grnx.mapeditor.chunk.Chunk;
import de.grnx.mapeditor.chunk.Cbuilder;
import de.grnx.mapeditor.chunk.Cbuilder.MeshPacket;
import de.grnx.mapeditor.chunk.AsyncCbuilder.VolatileMeshPacket;
import de.grnx.mapeditor.chunk.Cregion;
import de.grnx.mapeditor.chunk.AsyncCbuilderHandler;
import de.grnx.mapeditor.helper.Camera;
import de.grnx.mapeditor.helper.FastCulling;
import de.grnx.mapeditor.helper.TerrainBinder;
import de.grnx.mapeditor.helper.Util;
import de.grnx.mapeditor.helper.AtomicPointedCollection;
import de.grnx.mapeditor.gles30.mesh.ChunkMesh;
import de.grnx.mapeditor.texture.AssetRefs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
		import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Queue;

public final class WorldRenderer implements Disposable
{	
	/** Index Buffer - enables to reuse the vertex to render the "quad" */
	public static IndexData indices;
	
	/** Render table. */
	private final Array<ChunkMesh> 
	terrain = new Array<ChunkMesh>(false, 32);

	
	/** Chunk/Mesh builder. For the render thread. */
	private final Cbuilder build;

	private final World world;
	
	boolean forceDirty = false;
	private final Queue<Chunk> updateQueue = new Queue<Chunk>(64);
	private final Queue<Chunk> dirtyQueue  = new Queue<Chunk>(16);
	
	
	private final AsyncCbuilderHandler loader;
	
	WorldRenderer(World world, int maxDis) {
		this.world = world;
		loader = new AsyncCbuilderHandler(world, 8);
		build = new Cbuilder(world);
		setMaxDistance(maxDis);
		
		final int len = 98304;
		final short[] index = new short[len];
		for (int i = 0, v = 0; i < len; i += 6, v += 4) {
			index[i] = (short)v;
			index[i+1] = (short)(v+1);
			index[i+2] = (short)(v+2);
			index[i+3] = (short)(v+2);
			index[i+4] = (short)(v+3);
			index[i+5] = (short)v;
		}
		
			indices = new IndexBufferObject(true, len);
		
		indices.setIndices(index, 0, len);
		
	}
	
	private final GridPoint3 lastPos  = new GridPoint3();
	private final GridPoint3 chunkPos = new GridPoint3();
	
	void render(Camera cam) {

			chunkPos.set(MathUtils.floor(cam.position.x)>>4, MathUtils.floor(cam.position.y)>>4, MathUtils.floor(cam.position.z)>>4);

		
		
		final Plane[] planes = cam.frustum.planes;
		
		if (loader.isDone()) {

			if (Gdx.input.isKeyJustPressed(Keys.L)) {
				clear();
				loader.clear();
				dirtyQueue.clear();
				lastPos.set(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
				return;
			}
			
			if (forceDirty) {
				forceDirty = false;
				dirtyQueue.clear();
				checkChunk(0);
			}
			
			if (dirtyQueue.notEmpty()) {
				handleChunk(build.create(dirtyQueue.removeFirst()));
			}
			
			final AtomicPointedCollection<VolatileMeshPacket> packets = loader.get();
			if (packets != null) {
				final int size = packets.size;
				for (int i = 0; i < size; i++) {
					handleChunk(packets.get(i));
				}
			}
			
			if (lastPos.x != chunkPos.x || lastPos.z != chunkPos.z) {
				lastPos.set(chunkPos);
				for (Chunk chunk : updateQueue)
					chunk.isNewChunk = true;
				updateQueue.clear();
				checkNewChunk(0);
			}
			
			if (updateQueue.notEmpty()) {
				final AtomicPointedCollection<Chunk> chunks = loader.chunks;
				chunks.clear();
				final int len = loader.length;
				while (updateQueue.notEmpty()) {
					final Chunk chunk = updateQueue.removeFirst();
					chunk.isChunkSafe = false;
					chunks.add(chunk);
					if (chunks.size == len) break;
				}
				loader.start();
			}
		}
		
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);

		AssetRefs.textST.getTextureRegion(AssetRefs.missingName).getTexture().bind();
		
		TerrainBinder.bindTerrain(cam.combined);
		for (int i = 0; i < terrain.size; i++) {
			ChunkMesh mesh = terrain.get(i);
			Chunk chunk = mesh.chunk;
			if (chunk.x > chunkPos.x+renderMax || chunk.z > chunkPos.z+renderMax || 
				chunk.x < chunkPos.x-renderMax || chunk.z < chunkPos.z-renderMax) {
				mesh.dispose();
				chunk.setNewChunk(true);
				terrain.removeIndex(i--);
				continue;
			}
			if (FastCulling.frustBounds(planes, chunk))
				mesh.render(indices);
		}

		
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);

		Gdx.gl.glEnable(GL20.GL_BLEND);

		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	private int renderMaxSize;
	private int renderMax;
	
	private void checkNewChunk(int renderSize)
	{
		for (int x = -renderSize; x < renderSize+1; x++)
		{
			for (int z = -renderSize; z < renderSize+1; z++)
			{
				Cregion region = world.getChunkRegion(x+chunkPos.x, z+chunkPos.z);
				if (region == null) continue;
				for (int i = Cregion.LENGTH-1; i > -1; i--)	{
					final Chunk chunk = region.chunks[i];
					if (chunk.isNewChunk) {
						chunk.isNewChunk = false;
						updateQueue.addLast(chunk);
					}
				}
			}
		}
		
		if (renderSize >= renderMaxSize)
			return;
		
		checkNewChunk(renderSize+1);		
	}
	
	private void checkChunk(int renderSize)
	{
		for (int x = -renderSize; x < renderSize+1; x++)
		{
			for (int z = -renderSize; z < renderSize+1; z++)
			{
				Cregion region = world.getChunkRegion(x+chunkPos.x, z+chunkPos.z);
				if (region == null) continue;
				for (int i = Cregion.LENGTH-1; i > -1; i--)	{
					final Chunk chunk = region.chunks[i];
					if (chunk.isDirty) {
						chunk.isDirty = false;
						dirtyQueue.addLast(chunk);
					}
				}
			}
		}
		
		if (renderSize >= renderMaxSize)
			return;
		
		checkChunk(renderSize+1);		
	}

	
	void setMaxDistance(int chunks) {
		renderMaxSize = chunks;
		renderMax = chunks+1; // TODO: changed from 2 to 1
	}
	
	void clear() {
		for (int i = 0; i < terrain.size; i++) {
			ChunkMesh mesh = terrain.get(i);
			mesh.chunk.setNewChunk(true);
			mesh.dispose();
		}

		terrain.clear();

	}

	@Override
	public void dispose() {
		int i, s;
		for (i = 0, s = terrain.size; i < s; i++) {
			terrain.get(i).dispose();
		}
		Util.disposes(loader, indices);
		indices = null;
	}
	
	private void handleChunk(VolatileMeshPacket packet) {
		if (packet == null) return;
		final Chunk chunk = packet.chunk;
		testMesh(chunk, packet.terrain, terrain);

	}
	
	private void handleChunk(MeshPacket packet) {
		if (packet == null) return;
		final Chunk chunk = packet.chunk;
		testMesh(chunk, packet.terrain, terrain);

	}
	
	private void testMesh(Chunk chunk, ChunkMesh newMesh, Array<ChunkMesh> meshs) {
		if (newMesh == null) {
			newMesh = findChunkMesh(meshs, chunk);
			if (newMesh != null) {
				newMesh.dispose();
				meshs.removeIndex(iLast);
			}
		} else {
			ChunkMesh mesh = findChunkMesh(meshs, chunk);
			if (mesh != null) {
				mesh.dispose();
				meshs.removeIndex(iLast);
			}
			meshs.add(newMesh);
		}
	}
	
	private int iLast;
	private ChunkMesh findChunkMesh(Array<ChunkMesh> meshs, Chunk chunk) {
		iLast = -1;
		final int size = meshs.size;
		for (int i = 0; i < size; i++) {
			ChunkMesh mesh = meshs.get(i);
			if (mesh.chunk == chunk) {
				iLast = i;
				return mesh;
			}
		}
		return null;
	}
}