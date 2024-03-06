package de.grnx.mapeditor.gles30;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public interface VertContext 
{
	public ShaderProgram getShader();
	public VertexAttributes getAttrs();
	public int getLocation(int i);
	
	/** @return vertexSize/Float.BYTE */
	public int getAttrsSize();
//	public default int getAttrsSize() {
//		return getAttrs().vertexSize/Float.BYTES;
//	}
}
