package de.grnx.mapeditor.texture;

import de.grnx.mapeditor.buildableConf.Blocks;
import de.grnx.mapeditor.canvas.World;
import de.grnx.mapeditor.controllable.BlockTrace.RayInfo;
import de.grnx.mapeditor.helper.BlockPos;
import de.grnx.mapeditor.helper.Camera;

import static de.grnx.mapeditor.buildableConf.Blocks.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Disposable;

public class BoxRenderer implements Disposable {
	private final Camera cam;
	private final ShapeRenderer shape;

	public BoxRenderer(Camera cam) {
		this.cam = cam;
		shape = new ShapeRenderer(32);
		shape.setAutoShapeType(true);
	}

	// 10754

	public void render(final RayInfo ray, World world) {
		if (ray == null)
			return;
		if (blocks[world.getBlock(ray.in.x, ray.in.y, ray.in.z)] == blocks[Blocks.BEDROCK]) {
			final BlockPos pos = ray.in;
			Gdx.gl.glLineWidth(4);
			shape.setProjectionMatrix(cam.combined);
			shape.begin(ShapeType.Line);
			shape.box(pos.x - 0.01f, pos.y - 0.01f, pos.z + 1.01f, 1.02f, 1.02f, 1.02f);
			shape.setColor(Color.BLACK);
			shape.end();
		} else if (blocks[world.getBlock(ray.in.x, ray.in.y, ray.in.z)] == blocks[Blocks.MARKER]) {
			final BlockPos pos = ray.in;
			Gdx.gl.glLineWidth(6);
			shape.setProjectionMatrix(cam.combined);
			shape.begin(ShapeType.Line);
			shape.box(pos.x - 0.01f, pos.y - 0.01f, pos.z + 1.01f, 1.02f, 1.02f, 1.02f);
			shape.setColor(Color.YELLOW);
			shape.end();

		} else {
			final BlockPos pos = ray.in;
			Gdx.gl.glLineWidth(2);
			shape.setProjectionMatrix(cam.combined);
			shape.begin(ShapeType.Line);
			shape.box(pos.x - 0.01f, pos.y - 0.01f, pos.z + 1.01f, 1.02f, 1.02f, 1.02f);
			shape.setColor(Color.WHITE);
			shape.end();
		}
	}

	@Override
	public void dispose() {
		shape.dispose();
	}
}
