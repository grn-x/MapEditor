package de.grnx.mapeditor.controllable;

import de.grnx.mapeditor.buildableConf.Blocks;
import de.grnx.mapeditor.canvas.World;
import de.grnx.mapeditor.helper.Camera;
import de.grnx.mapeditor.helper.Util;
import de.grnx.mapeditor.texture.AssetRefs;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.*;

import static de.grnx.mapeditor.buildableConf.Blocks.blocks;

import java.io.PrintWriter;
import java.io.StringWriter;

/** Render the GUI. */
public final class Overlay {
	public static int blockPick = 4;

	public static void update() {
		InputHolder.holder.add(new InputAdapter() {
			public boolean scrolled(int amount) {
				if (amount == 1) { // Left
					if (blockPick - 3 < 1) //TODO subtract Tmsw to account for unobtainable blocks
						return true;
					blockPick--;
				}
				if (amount == -1) { // Right
					if (blockPick + 1 > blocks.length - 1)
						return true;
					blockPick++;
				}
				return false;
			}
		});
	}

	public static void render(SpriteBatch batch, Camera cam, BlockTrace.RayInfo ray, World world) {

		final int height = Util.world.height;
		final int id = blockPick;
		final int sWidth = 32;// 6width
		final int padding = 3;
		final int sHeight = 64;
		final int bWidth = 64;
		final int bHeight = 64;
		// center todo 
		BitmapFont font = new BitmapFont();

		try {
//			font.draw(batch, round(Player.cam.position.x-512,1) + " \t \t " +round(Player.cam.position.y,1) + " \t \t " +round(Player.cam.position.z-512,1), 32+40, height-90);
			
		
					
			batch.draw(AssetRefs.textST.getTextureRegion(AssetRefs.crossairRef), (Util.world.width / 2f) - 16f, (Util.world.height / 2f) - 16f, 32f, 32f);
			// batch.draw(AssetRefs.textST.getTextureRegion(blocks_external[0].side), (Util.world.w / 2f) - 16f, (Util.world.h / 2f) - 16f, 128f, 128f);
			// batch.draw(AssetRefs.textST.getCombinedTexture(), (Util.world.w / 2f) - 16f,
			// (Util.world.h / 2f) - 16f, 32f, 32f);
			
			if (id - 4 > 0) {
				batch.draw(AssetRefs.renderAbsoluteRef(blocks[id - 2].side), sWidth, sHeight, sWidth, sWidth);
			}

			if (id - 3 > 0) {
				batch.draw(AssetRefs.renderAbsoluteRef(blocks[id - 1].side), (sWidth + padding) * 2, sHeight, sWidth,
						sWidth);
			}

			batch.draw(AssetRefs.renderAbsoluteRef(blocks[id].side), 3 + (sWidth + padding) * 3, sHeight - 16, 64, 64);
			font.draw(batch, blocks[id].name, 3 + (sWidth + padding) * 3, sHeight - 16);
			if (id + 1 < blocks.length) {
				batch.draw(AssetRefs.renderAbsoluteRef(blocks[id + 1].side), bWidth + 6 + (sWidth + padding) * 3,
						sHeight, sWidth, sWidth);
			}

			if (id + 2 < blocks.length) {
				batch.draw(AssetRefs.renderAbsoluteRef(blocks[id + 2].side), bWidth + 6 + (sWidth + padding) * 4,
						sHeight, sWidth, sWidth);

			}

		try {	
			final int textLength = 120;
			font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), font.getLineHeight(), font.getLineHeight() * 2);
			font.draw(batch, "Blocks Loaded: " + Blocks.blocks.length, cam.viewportWidth-textLength, font.getLineHeight() * 4);
			font.draw(batch, "Internal (-3): "+ Blocks.blocks_internal.length, cam.viewportWidth-textLength, font.getLineHeight() * 3);
			font.draw(batch, "External : "+ Blocks.blocks_external.length,  cam.viewportWidth-textLength, font.getLineHeight() * 2); //if no external blocks are loaded, code after this statement will not execute as it probably throwa a null pointer exception from the external Block array
			//batch.draw(AssetRefs.textST.getTextureRegion(AssetRefs.crossairRef), (Util.world.w / 2f) - 16f,(Util.world.h / 2f) - 16f, 32f, 32f);// without constraints
		}catch(Exception e) {
			//cursed nested catch, beacuse external blocks will be null and i dont want that to interrupt the parent try catch			
		}
			
			//put this in here so that if something failes the screen is blank except the error
			
			font.draw(batch, "Viewer Pos: \t\t"+
					round(cam.position.x - World.CENTER, 1) + " \t \t " + round(cam.position.y, 1) + " \t \t "
							+ round(cam.position.z - World.CENTER, 1),
					font.getLineHeight(), cam.viewportHeight - font.getLineHeight());
			
			if(ray!=null) {
			font.draw(batch, "Block Pos: \t\t"+
					round(ray.in.x - World.CENTER, 1) + " \t \t " + round(ray.in.y, 1) + " \t \t "
							+ round(ray.in.z - World.CENTER, 1),
					font.getLineHeight(), cam.viewportHeight - font.getLineHeight()*2);
			
			font.draw(batch, "\t\t --> \t\t"+
					blocks[world.getBlock(ray.in.x, ray.in.y, ray.in.z)].name ,
					font.getLineHeight(), cam.viewportHeight - font.getLineHeight()*3);
			}else {
				font.draw(batch, "Block Pos: \t\t"+
						"/./" + " \t \t " + "/./" + " \t \t "
								+ "/./",
						font.getLineHeight(), cam.viewportHeight - font.getLineHeight()*2);
				
				font.draw(batch, "\t\t --> \t\t"+
						"//" ,
						font.getLineHeight(), cam.viewportHeight - font.getLineHeight()*3);
			}
			
			
		
		} catch (Exception e) {
			//e.printStackTrace();
			font.draw(batch, stackToString(e) ,1 , Gdx.graphics.getHeight());

		}
		// font = new
		// BitmapFont(Gdx.files.internal("Calibri.fnt"),Gdx.files.internal("Calibri.png"),false);

	
		
		

	}

	public static String stackToString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String sStackTrace = sw.toString(); // stack trace as a string
		return sStackTrace;
	}
	
	private static double round(double value, int precision) {
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
	}

}
