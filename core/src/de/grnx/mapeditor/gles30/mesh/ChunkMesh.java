package de.grnx.mapeditor.gles30.mesh;



import java.nio.ShortBuffer;

import de.grnx.mapeditor.chunk.Chunk;
import de.grnx.mapeditor.gles30.VBO;
import de.grnx.mapeditor.gles30.VertContext;
import de.grnx.mapeditor.gles30.Vertex;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;


public final class ChunkMesh implements Disposable
{
	private final Vertex vertex;
	private final int count;
	
	/** Pointer to the chunk. */
	public final Chunk chunk;
	
	public ChunkMesh(Chunk chunk, FloatArray verts, VertContext context) {
		this.chunk = chunk;
		
			vertex = new VBO(verts, context);
		
		count = verts.size/Float.BYTES;
	}
	
	public void render(final IndexData indices) {
		vertex.bind();
			Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, count, GL20.GL_UNSIGNED_SHORT, 0);
		
		vertex.unbind();
	}
	
	@Override
	public void dispose() {
		vertex.dispose();
	}
}
