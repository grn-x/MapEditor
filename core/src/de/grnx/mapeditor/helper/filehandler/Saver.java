package de.grnx.mapeditor.helper.filehandler;

import static de.grnx.mapeditor.canvas.World.defaultSize;

import java.io.File;

import de.grnx.mapeditor.canvas.World;
import de.grnx.mapeditor.chunk.Chunk;
import de.grnx.mapeditor.chunk.Cregion;
import de.grnx.mapeditor.helper.filehandler.ExtensionAPI;
import de.grnx.mapeditor.helper.filehandler.FileHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;;

//TODO: Rework this world save system.
public class Saver
{
	public static void save(World world, ExtensionAPI ext)
	{
		DisplayMode currentMode = Gdx.graphics.getDisplayMode();
		boolean call = false;
		if (Gdx.graphics.isFullscreen())
		{
			currentMode = Gdx.graphics.getDisplayMode();
			Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height);
			call = true;
		}
		
		File selectedFile = FileHandler.winFileSaver(ext, null);
		
		
		if (selectedFile != null)
		{
			int xSize = defaultSize*Chunk.SIZE;
			int ySize = Cregion.LENGTH*Chunk.SIZE;
			int zSize = defaultSize*Chunk.SIZE;
			
			//int xFix = xSize/2;
			//int zFix = zSize/2;
			
			byte[] blocks = new byte[xSize*ySize*zSize];
			int i = 0;
			for (int x = 0; x < xSize; x++)
			{
				for (int y = 0; y < ySize; y++)
				{
					for (int z = 0; z < zSize; z++)
					{
						blocks[i] = world.getBlock(x, y, z);
						i++;
					}
				}
			}
			
			String path = selectedFile.getAbsolutePath();
			System.out.println(selectedFile.getAbsolutePath());

			FileHandler.writeAbsoluteBytes(path, blocks);
			if (call) Gdx.graphics.setFullscreenMode(currentMode);
			return;
		}
		
		System.out.println("No File selected.");
		
		System.gc();
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (call) Gdx.graphics.setFullscreenMode(currentMode);
	}
	
	public static World load(ExtensionAPI ext)
	{		
		File selectedFile = FileHandler.winFileChooser(ext, null);

		byte[] blocks = null;
		if (selectedFile != null) {
			try {
//				blocks = Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath()));
				blocks = FileHandler.readAbsoluteBytes(selectedFile.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
				Gdx.app.exit();
			}
			// blocks = Gdx.files.absolute(selectedFile.getAbsolutePath()).readBytes();
		} else {
			
			return new World(true);
		}
		
		
		World world = new World(false);
		int xSize = defaultSize*Chunk.SIZE;
		int ySize = Cregion.HEIGHT;
		int zSize = defaultSize*Chunk.SIZE;
		
		int i = 0;
		for (int x = 0; x < xSize; x++)
		{
			for (int y = 0; y < ySize; y++)
			{
				for (int z = 0; z < zSize; z++)
				{
					//world.setBlock(x-xFix, y, z-zFix, blocks[i++]);
					world.setBlock(x, y, z, blocks[i++]);
				}
			}
		}
		
		/* TODO: lighting disabled.
		for (int x = 0; x < defaultSize; x++)
		{
			for (int z = 0; z < defaultSize; z++)
			{
				world.regions[x][z].reLighting();;
			}
		} */
		
		return world;
	}
	
	public static World load(String str, ExtensionAPI ext)
	{
		try {
			if(!new File(str).exists()) {
				System.out.println("invalid path: \t" + str);
				throw new Exception(); //string passed into this method neednt be checked
			}
			
			
			File selectedFile = new File(str);
			System.out.println("Proceeding to load\t"+ str);
			byte[] blocks = null;
				try {
//					blocks = Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath()));
					blocks = FileHandler.readAbsoluteBytes(selectedFile.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
					Gdx.app.exit();
				}
				// blocks = Gdx.files.absolute(selectedFile.getAbsolutePath()).readBytes();
			
			
			World world = new World(false);
			int xSize = defaultSize*Chunk.SIZE;
			int ySize = Cregion.HEIGHT;
			int zSize = defaultSize*Chunk.SIZE;
			
			int i = 0;
			for (int x = 0; x < xSize; x++)
			{
				for (int y = 0; y < ySize; y++)
				{
					for (int z = 0; z < zSize; z++)
					{
						//world.setBlock(x-xFix, y, z-zFix, blocks[i++]);
						world.setBlock(x, y, z, blocks[i++]);
					}
				}
			}
			
			
			return world;
		
			
		}catch(Exception e) {
			
			File selectedFile = FileHandler.winFileChooser(ext, null);

			byte[] blocks = null;
			if (selectedFile != null) {
				try {
//					blocks = Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath()));
					System.out.println("Trying to load\t"+ selectedFile);

					blocks = FileHandler.readAbsoluteBytes(selectedFile.getAbsolutePath());
				} catch (Exception e1) {
					e1.printStackTrace();
					Gdx.app.exit();
				}
				// blocks = Gdx.files.absolute(selectedFile.getAbsolutePath()).readBytes();
			} else {
				
				return new World(true);
			}
			
			
			World world = new World(false);
			int xSize = defaultSize*Chunk.SIZE;
			int ySize = Cregion.HEIGHT;
			int zSize = defaultSize*Chunk.SIZE;
			
			int i = 0;
			for (int x = 0; x < xSize; x++)
			{
				for (int y = 0; y < ySize; y++)
				{
					for (int z = 0; z < zSize; z++)
					{
						//world.setBlock(x-xFix, y, z-zFix, blocks[i++]);
						world.setBlock(x, y, z, blocks[i++]);
					}
				}
			}
			
			
			return world;
			
		}
		
		
	}

}
