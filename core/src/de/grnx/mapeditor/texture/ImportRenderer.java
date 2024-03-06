package de.grnx.mapeditor.texture;

import de.grnx.mapeditor.helper.BlockPos;
import de.grnx.mapeditor.helper.Camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Disposable;

public class ImportRenderer implements Disposable {
	private final Camera cam;
	private final ShapeRenderer shape;
	private final BlockPos[] pos;
	

	public ImportRenderer(Camera cam, BlockPos[] pos) {
		this.cam = cam;
		shape = new ShapeRenderer(pos.length*32);
		shape.setAutoShapeType(true);
		this.pos=pos;
	}


	public void render(BlockPos offset) {
			
			shape.setProjectionMatrix(cam.combined);
			shape.begin(ShapeType.Line);
			Gdx.gl.glLineWidth(1);
			for(BlockPos pos:this.pos) {
			//pos.add(offset);
			shape.box(pos.x+offset.x - 0.01f, pos.y+offset.y - 0.01f, pos.z +offset.z+ 1.01f, 1.02f, 1.02f, 1.02f);
			}
			shape.setColor(Color.RED);
			shape.end();
		
	}

	@Override
	public void dispose() {
		shape.dispose();
	}
}
