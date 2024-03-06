package de.grnx.mapeditor.helper.filehandler;

import static de.grnx.mapeditor.buildableConf.Blocks.blocks;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.grnx.mapeditor.MapEditorMain;
import de.grnx.mapeditor.controllable.HistoryQueue;
import de.grnx.mapeditor.helper.BlockPos;

public class SerQsaver {
	@Deprecated
	public static boolean save_deprecated(String path, HistoryQueue q) {

		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

			objectOutputStream.writeObject(q);
			objectOutputStream.flush();
			objectOutputStream.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Deprecated
	public static HistoryQueue load_deprecated(String path) {
		try (FileInputStream fileInputStream = new FileInputStream(path);
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
			return (HistoryQueue) objectInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return new HistoryQueue(new BlockPos(0, 0, 0), blocks[MapEditorMain.world.getBlock(0, 0, 0)]);
		}
	}

	public static boolean save(String path, HistoryQueue q) {
		FileOutputStream fileOutputStream;
		
		try {
			fileOutputStream = new FileOutputStream(path);
			fileOutputStream.write(SerializationUtils.serialize(q));
			fileOutputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static HistoryQueue load(String path) {
		System.out.println("path: loadSerQSaver" + path);
		try (FileInputStream fileInputStream = new FileInputStream(path)){
			return (HistoryQueue) SerializationUtils.deserialize(fileInputStream);
		} catch (Exception e) {
			e.printStackTrace();
			return new HistoryQueue(new BlockPos(0, 0, 0), blocks[MapEditorMain.world.getBlock(0, 0, 0)]);
		}
		
	}



public static boolean save(String path, String contents) {
	FileOutputStream fileOutputStream;
	
	try {
		fileOutputStream = new FileOutputStream(path);
		fileOutputStream.write(contents.getBytes());
		fileOutputStream.close();

	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}
	return true;
}



}

class SerializationExample {
	public static void save(String path, HistoryQueue q) {
		// Serialize
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("historyQueue.ser"))) {
			// Populate historyQueue with data

			oos.writeObject(q);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static HistoryQueue load(String path) {

		// Deserialize
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("historyQueue.ser"))) {
			return (HistoryQueue) ois.readObject();
			// Use deserializedHistoryQueue
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}

	}
}

/*
 * public static void save(World world) { DisplayMode currentMode =
 * Gdx.graphics.getDisplayMode(); boolean call = false; if
 * (Gdx.graphics.isFullscreen()) { currentMode = Gdx.graphics.getDisplayMode();
 * Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height); call =
 * true; }
 * 
 * File selectedFile = FileHandler.winFileChooser();
 * 
 * 
 * if (selectedFile != null) { int xSize = defaultSize*Chunk.SIZE; int ySize =
 * ChunkRegion.LENGTH*Chunk.SIZE; int zSize = defaultSize*Chunk.SIZE;
 * 
 * //int xFix = xSize/2; //int zFix = zSize/2;
 * 
 * byte[] blocks = new byte[xSize*ySize*zSize]; int i = 0; for (int x = 0; x <
 * xSize; x++) { for (int y = 0; y < ySize; y++) { for (int z = 0; z < zSize;
 * z++) { blocks[i] = world.getBlock(x, y, z); i++; } } }
 * 
 * String path = selectedFile.getAbsolutePath();
 * FileHandler.writeAbsoluteBytes(path, blocks); if (call)
 * Gdx.graphics.setFullscreenMode(currentMode); return; }
 * 
 * System.out.println("No File selected.");
 * 
 * System.gc(); try { Thread.sleep(1000L); } catch (InterruptedException e) {
 * e.printStackTrace(); }
 * 
 * if (call) Gdx.graphics.setFullscreenMode(currentMode); }
 * 
 * public static World load() { File selectedFile =
 * FileHandler.winFileChooser();
 * 
 * byte[] blocks = null; if (selectedFile != null) { try { // blocks =
 * Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())); blocks =
 * FileHandler.readAbsoluteBytes(selectedFile.getAbsolutePath()); } catch
 * (Exception e) { e.printStackTrace(); Gdx.app.exit(); } // blocks =
 * Gdx.files.absolute(selectedFile.getAbsolutePath()).readBytes(); } else {
 * 
 * return new World(true); }
 * 
 * 
 * World world = new World(false); int xSize = defaultSize*Chunk.SIZE; int ySize
 * = ChunkRegion.HEIGHT; int zSize = defaultSize*Chunk.SIZE;
 * 
 * int i = 0; for (int x = 0; x < xSize; x++) { for (int y = 0; y < ySize; y++)
 * { for (int z = 0; z < zSize; z++) { //world.setBlock(x-xFix, y, z-zFix,
 * blocks[i++]); world.setBlock(x, y, z, blocks[i++]); } } }
 * 
 * 
 * 
 * return world; }
 * 
 * public static World load(String str) { try { if(!new File(str).exists()) {
 * System.out.println("invalid path: \t" + str); throw new Exception(); //string
 * passed into this method neednt be checked }
 * 
 * 
 * File selectedFile = new File(str); System.out.println("Proceeding to load\t"+
 * str); byte[] blocks = null; try { // blocks =
 * Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())); blocks =
 * FileHandler.readAbsoluteBytes(selectedFile.getAbsolutePath()); } catch
 * (Exception e) { e.printStackTrace(); Gdx.app.exit(); } // blocks =
 * Gdx.files.absolute(selectedFile.getAbsolutePath()).readBytes();
 * 
 * 
 * World world = new World(false); int xSize = defaultSize*Chunk.SIZE; int ySize
 * = ChunkRegion.HEIGHT; int zSize = defaultSize*Chunk.SIZE;
 * 
 * int i = 0; for (int x = 0; x < xSize; x++) { for (int y = 0; y < ySize; y++)
 * { for (int z = 0; z < zSize; z++) { //world.setBlock(x-xFix, y, z-zFix,
 * blocks[i++]); world.setBlock(x, y, z, blocks[i++]); } } }
 * 
 * 
 * return world;
 * 
 * 
 * }catch(Exception e) {
 * 
 * File selectedFile = FileHandler.winFileChooser();
 * 
 * byte[] blocks = null; if (selectedFile != null) { try { // blocks =
 * Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath()));
 * System.out.println("Trying to load\t"+ selectedFile);
 * 
 * blocks = FileHandler.readAbsoluteBytes(selectedFile.getAbsolutePath()); }
 * catch (Exception e1) { e1.printStackTrace(); Gdx.app.exit(); } // blocks =
 * Gdx.files.absolute(selectedFile.getAbsolutePath()).readBytes(); } else {
 * 
 * return new World(true); }
 * 
 * 
 * World world = new World(false); int xSize = defaultSize*Chunk.SIZE; int ySize
 * = ChunkRegion.HEIGHT; int zSize = defaultSize*Chunk.SIZE;
 * 
 * int i = 0; for (int x = 0; x < xSize; x++) { for (int y = 0; y < ySize; y++)
 * { for (int z = 0; z < zSize; z++) { //world.setBlock(x-xFix, y, z-zFix,
 * blocks[i++]); world.setBlock(x, y, z, blocks[i++]); } } }
 * 
 * 
 * return world;
 * 
 * }
 * 
 * 
 * }
 * 
 * }
 */
