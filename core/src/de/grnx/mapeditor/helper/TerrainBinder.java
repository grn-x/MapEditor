package de.grnx.mapeditor.helper;


import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import de.grnx.mapeditor.helper.filehandler.FileHandler;
import de.grnx.mapeditor.gles30.mesh.verts.TerrainVert;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

public final class TerrainBinder {
	public static final String projTran = "u_projTrans";
	
	public static ShaderProgram terrain;
	public static int[] locations;
	
	public static void bindTerrain(Matrix4 combine) {
		terrain.bind();
		terrain.setUniformMatrix(projTran, combine);
	}
	
	
	

	
	/** @return true if success. */
	public static boolean loadShaders() {
		try {
			terrain = new ShaderProgram(FileHandler.getFile("shaders/terrain.vert"), FileHandler.getFile("shaders/terrain.frag"));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		String log = "Shader log: \n";
		boolean error = false;
		if (!terrain.isCompiled()) {
			error = true;
			log += terrain.getLog() + "\n";
		}
		
		if (error) {
			System.out.println(log);
		} else {
			locations = locateAttributes(terrain, TerrainVert.attributes);
		}
		return !error;
	}
	public static int[] locateAttributes(final ShaderProgram shader, final VertexAttributes attributes) {
		final int s = attributes.size();
		final int[] locations = new int[s];
		for (int i = 0; i < s; i++) {
			final VertexAttribute attribute = attributes.get(i);
			locations[i] = shader.getAttributeLocation(attribute.alias);
		}
		return locations;
	}
}
