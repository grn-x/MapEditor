package de.grnx.mapeditor.gles30.mesh.verts;

import static com.badlogic.gdx.graphics.glutils.ShaderProgram.*;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public final class TerrainVert
{
	public static final VertexAttributes attributes = new VertexAttributes(
			 	new VertexAttribute(Usage.Position, 3, POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.Generic, 1, "a_light"),
				new VertexAttribute(Usage.TextureCoordinates, 2, TEXCOORD_ATTRIBUTE)
			);
	
	public static final int byteSize = attributes.vertexSize;
	
	public static final int floatSize = byteSize/Float.BYTES;
}
