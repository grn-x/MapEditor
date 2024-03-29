package de.grnx.mapeditor.gles30.mesh;

import de.grnx.mapeditor.buildableConf.Block;
import de.grnx.mapeditor.chunk.Chunk;
import de.grnx.mapeditor.gles30.VertContext;
import de.grnx.mapeditor.helper.TerrainBinder;
import de.grnx.mapeditor.gles30.mesh.verts.TerrainVert;
import de.grnx.mapeditor.gles30.mesh.verts.VertInfo;
import de.grnx.mapeditor.texture.AssetRefs;

import static de.grnx.mapeditor.buildableConf.Blocks.blocks;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TerrainBuilder extends MeshBuilder
{
	private static VertContext context = new VertContext() {
		public ShaderProgram getShader() {
			return TerrainBinder.terrain;
		}
		public VertexAttributes getAttrs() {
			return TerrainVert.attributes;
		}
		public int getLocation(int i) {
			return TerrainBinder.locations[i];
		}
		@Override
		public int getAttrsSize() {
			return getAttrs().vertexSize/Float.BYTES;
		}
	};
	
	public static final float 
	lightHigh = 1.0f, // old: 1.0f  new: 1.0f
	lightMed = 0.86f, // old: 0.82f new: 0.86f
	lightLow = 0.75f, // old: 0.68f new: 0.75f
	lightDim = 0.69f, // old: 0.6f  new: 0.65f
	power = 0.75f; // 0.75f
	
	private final VertInfo v1, v2, v3, v4;
	
	public TerrainBuilder() {
		v1 = new VertInfo();
		v2 = new VertInfo();
		v3 = new VertInfo();
		v4 = new VertInfo();
		
		v1.uv.set(0f, 1f);
		v2.uv.set(1f, 1f);
		v3.uv.set(1f, 0f);
		v4.uv.set(0f, 0f);
	}
	
	private void rect(TextureRegion region) {
		begin();
		setUVRange(region);
		vertex(v1.pos, v1.lit, v1.uv);
		vertex(v2.pos, v2.lit, v2.uv);
		vertex(v3.pos, v3.lit, v3.uv);
		vertex(v4.pos, v4.lit, v4.uv);
	}
	
	private void vertex(final Vector3 pos, final float lit, final Vector2 uv) {
		vertices.add(pos.x, pos.y, pos.z, lit);
		vertices.add(uOffset+uScale*uv.x, vOffset+vScale*uv.y);
	}
	
	public ChunkMesh create(Chunk chunk) {
		if (!isBuilding) return null;
		isBuilding = false;
		return new ChunkMesh(chunk, vertices, context);
	}
	
	public void bNorth(Block block, Chunk chunk, int x, int y, int z) {
		++z;
		v1.pos.set(x, y, z);
		v2.pos.set(x+1, y, z);
		v3.pos.set(x+1, y+1, z);
		v4.pos.set(x, y+1, z);
		setLight(lightMed);
		// lighting
		if (blocks[chunk.getBlockSmart(x+1, y, z)].isSolid) {
			v3.lit *= power;
			v2.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x, y+1, z)].isSolid) {
			v3.lit *= power;
			v4.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x-1, y, z)].isSolid) {
			v4.lit *= power;
			v1.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x, y-1, z)].isSolid) {
			v1.lit *= power;
			v2.lit *= power;
		}
		if (v3.lit == lightMed && blocks[chunk.getBlockSmart(x+1, y+1, z)].isSolid) {
			v3.lit *= power;
		}
		if (v4.lit == lightMed && blocks[chunk.getBlockSmart(x-1, y+1, z)].isSolid) {
			v4.lit *= power;
		}
		if (v1.lit == lightMed && blocks[chunk.getBlockSmart(x-1, y-1, z)].isSolid) {
			v1.lit *= power;
		}
		if (v2.lit == lightMed && blocks[chunk.getBlockSmart(x+1, y-1, z)].isSolid) {
			v2.lit *= power;
		}
		rect(AssetRefs.textST.getTextureRegion(block.side));
	}

	public void bEast(Block block, Chunk chunk, int x, int y, int z) {
		++x;
		v1.pos.set(x, y, z+1);
		v2.pos.set(x, y, z);
		v3.pos.set(x, y+1, z);
		v4.pos.set(x, y+1, z+1);
		setLight(lightLow);
		// lighting
		if (blocks[chunk.getBlockSmart(x, y, z+1)].isSolid) {
			v4.lit *= power;
			v1.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x, y+1, z)].isSolid) {
			v3.lit *= power;
			v4.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x, y, z-1)].isSolid) {
			v3.lit *= power;
			v2.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x, y-1, z)].isSolid) {
			v1.lit *= power;
			v2.lit *= power;
		}
		if (v3.lit == lightLow && blocks[chunk.getBlockSmart(x, y+1, z-1)].isSolid) {
			v3.lit *= power;
		}
		if (v4.lit == lightLow && blocks[chunk.getBlockSmart(x, y+1, z+1)].isSolid) {
			v4.lit *= power;
		}
		if (v1.lit == lightLow && blocks[chunk.getBlockSmart(x, y-1, z+1)].isSolid) {
			v1.lit *= power;
		}
		if (v2.lit == lightLow && blocks[chunk.getBlockSmart(x, y-1, z-1)].isSolid) {
			v2.lit *= power;
		}
		rect(AssetRefs.textST.getTextureRegion(block.side));

	}

	public void bSouth(Block block, Chunk chunk, int x, int y, int z) {
		v1.pos.set(x+1, y, z);
		v2.pos.set(x, y, z);
		v3.pos.set(x, y+1, z);
		v4.pos.set(x+1, y+1, z);
		setLight(lightMed);
		// lighting
		--z;
		if (blocks[chunk.getBlockSmart(x+1, y, z)].isSolid) {
			v4.lit *= power;
			v1.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x, y+1, z)].isSolid) {
			v3.lit *= power;
			v4.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x-1, y, z)].isSolid) {
			v3.lit *= power;
			v2.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x, y-1, z)].isSolid) {
			v1.lit *= power;
			v2.lit *= power;
		}
		if (v3.lit == lightMed && blocks[chunk.getBlockSmart(x-1, y+1, z)].isSolid) {
			v3.lit *= power;
		}
		if (v4.lit == lightMed && blocks[chunk.getBlockSmart(x+1, y+1, z)].isSolid) {
			v4.lit *= power;
		}
		if (v1.lit == lightMed && blocks[chunk.getBlockSmart(x+1, y-1, z)].isSolid) {
			v1.lit *= power;
		}
		if (v2.lit == lightMed && blocks[chunk.getBlockSmart(x-1, y-1, z)].isSolid) {
			v2.lit *= power;
		}
		rect(AssetRefs.textST.getTextureRegion(block.side));

	}

	public void bWest(Block block, Chunk chunk, int x, int y, int z) {
		v1.pos.set(x, y, z);
		v2.pos.set(x, y, z+1);
		v3.pos.set(x, y+1, z+1);
		v4.pos.set(x, y+1, z);
		setLight(lightLow);
		--x;
		// lighting
		if (blocks[chunk.getBlockSmart(x, y, z+1)].isSolid) {
			v3.lit *= power;
			v2.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x, y+1, z)].isSolid) {
			v3.lit *= power;
			v4.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x, y, z-1)].isSolid) {
			v4.lit *= power;
			v1.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x, y-1, z)].isSolid) {
			v1.lit *= power;
			v2.lit *= power;
		}
		if (v3.lit == lightLow && blocks[chunk.getBlockSmart(x, y+1, z+1)].isSolid) {
			v3.lit *= power;
		}
		if (v4.lit == lightLow && blocks[chunk.getBlockSmart(x, y+1, z-1)].isSolid) {
			v4.lit *= power;
		}
		if (v1.lit == lightLow && blocks[chunk.getBlockSmart(x, y-1, z-1)].isSolid) {
			v1.lit *= power;
		}
		if (v2.lit == lightLow && blocks[chunk.getBlockSmart(x, y-1, z+1)].isSolid) {
			v2.lit *= power;
		}
		rect(AssetRefs.textST.getTextureRegion(block.side));

	}

	public void bTop(Block block, Chunk chunk, int x, int y, int z) {
		++y;
		v1.pos.set(x+1, y, z);
		v2.pos.set(x, y, z);
		v3.pos.set(x, y, z+1);
		v4.pos.set(x+1, y, z+1);
		setLight(lightHigh);
		// lighting
		if (blocks[chunk.getBlockSmart(x+1, y, z)].isSolid) {
			v4.lit *= power-0.f;
			v1.lit *= power-0.f;
		}
		if (blocks[chunk.getBlockSmart(x, y, z+1)].isSolid) {
			v3.lit *= power-0.f;
			v4.lit *= power-0.f;
		}
		if (blocks[chunk.getBlockSmart(x-1, y, z)].isSolid) {
			v3.lit *= power-0.f;
			v2.lit *= power-0.f;
		}
		if (blocks[chunk.getBlockSmart(x, y, z-1)].isSolid) {
			v1.lit *= power-0.f;
			v2.lit *= power-0.f;
		}
		if (v3.lit == lightHigh && blocks[chunk.getBlockSmart(x-1, y, z+1)].isSolid) {
			v3.lit *= power-0.f;
		}
		if (v4.lit == lightHigh && blocks[chunk.getBlockSmart(x+1, y, z+1)].isSolid) {
			v4.lit *= power-0.f;
		}
		if (v1.lit == lightHigh && blocks[chunk.getBlockSmart(x+1, y, z-1)].isSolid) {
			v1.lit *= power-0.f;
		}
		if (v2.lit == lightHigh && blocks[chunk.getBlockSmart(x-1, y, z-1)].isSolid) {
			v2.lit *= power-0.f;
		}
		rect(AssetRefs.textST.getTextureRegion(block.top));

	}

	public void bBottom(Block block, Chunk chunk, int x, int y, int z) {
		v1.pos.set(x+1, y, z);
		v2.pos.set(x+1, y, z+1);
		v3.pos.set(x, y, z+1);
		v4.pos.set(x, y, z);
		setLight(lightDim);
		--y;
		// lighting
		if (blocks[chunk.getBlockSmart(x+1, y, z)].isSolid) {
			v1.lit *= power;
			v2.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x, y, z+1)].isSolid) {
			v2.lit *= power;
			v3.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x-1, y, z)].isSolid) {
			v3.lit *= power;
			v4.lit *= power;
		}
		if (blocks[chunk.getBlockSmart(x, y, z-1)].isSolid) {
			v1.lit *= power;
			v4.lit *= power;
		}
		if (v3.lit == lightDim && blocks[chunk.getBlockSmart(x-1, y, z+1)].isSolid) {
			v3.lit *= power;
		}
		if (v2.lit == lightDim && blocks[chunk.getBlockSmart(x+1, y, z+1)].isSolid) {
			v2.lit *= power;
		}
		if (v1.lit == lightDim && blocks[chunk.getBlockSmart(x+1, y, z-1)].isSolid) {
			v1.lit *= power;
		}
		if (v4.lit == lightDim && blocks[chunk.getBlockSmart(x-1, y, z-1)].isSolid) {
			v4.lit *= power;
		}
		rect(AssetRefs.textST.getTextureRegion(block.bottom));

	}
	
	private void setLight(float light) {
		v1.lit = light;
		v2.lit = light;
		v3.lit = light;
		v4.lit = light;
	}
}