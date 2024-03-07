package de.grnx.mapeditor.texture;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import de.grnx.mapeditor.buildableConf.Blocks;
import de.grnx.mapeditor.helper.filehandler.FileHandler;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import java.io.File;
import java.io.FilenameFilter;

import static de.grnx.mapeditor.Options.jarExport;;

public class ManagerInit {
	public static String prefixPath;
	
	
	@SuppressWarnings("unused")
	public static AssetManager initialize() {

		if(jarExport==false){
			//AssetManager mgr = new AssetManager(new AbsoluteFileHandleResolver());
			AssetManager mgr = new AssetManager(new InternalFileHandleResolver());


//		mgr = new AssetManager();
		//mgr.load("internalBlocks\\stone.png", Texture.class);
		
		//System.out.println(Gdx.files.internal("internalBlocks\\").file().getAbsolutePath());
		File blocksDirectory = FileHandler.getBlock("").file();
    	System.out.println("\nblocks dir test: " + blocksDirectory);

    	System.out.println("\nblocks dir is dir?: " + blocksDirectory.isDirectory());
    	
    	 for(String str:Blocks.extTextsMGR){
         	if(new File(str).exists()) {
         		mgr.load(str, Texture.class);
         	}else {
         		System.out.println("Manager Init: ExternalFail! " + str); //TODO 
         	}}
    	 
    	 
    	if (jarExport == false && !blocksDirectory.isDirectory())return mgr;// should throw error internal file fuck, this would work if i were to not export a jar
    	
        	File[] blockFiles = blocksDirectory.listFiles(new FilenameFilter() {
        	    public boolean accept(File dir, String name) {
        	        return name.toLowerCase().endsWith(".png");
        	    }
        	});
        	
        	System.out.println("block files to string" + Arrays.toString(blockFiles));
            if (blockFiles != null) {
            	prefixPath=blocksDirectory.toString().replace("\\", "/") +"/";
            	System.out.println("\nprefix path test: " + prefixPath);
                for (File blockFile : blockFiles) {
                    String blockName = blockFile.getName().replace(".png", ""); //if getname does not return the file with extension; more bulletproof
                    String blockPath = blocksDirectory + "/" + blockName + ".png";
                    mgr.load(blockPath, Texture.class);
                    
                }
            }
        
        
       
        
		mgr.finishLoading(); //necessary! asset manager initializes stitcher TODO: implement and use assetmanager async potential
        AssetRefs.setManager(mgr);
        return mgr;
		
		}else {
			AssetManager mgr = new AssetManager(new InternalFileHandleResolver());

			
			File blocksDirectory = FileHandler.getBlock("").file();
	    	System.out.println("\nblocks dir test: " + blocksDirectory);
	    	String resListRead = new String(FileHandler.getBlock("res.txt").readBytes(),Charset.defaultCharset());
	    	String[] strs =resListRead.split("\n");
	    	
	    	
	    		prefixPath=blocksDirectory.toString().replace("\\", "/") +"/";
            	System.out.println("\nprefix path test: " + prefixPath);

	    	for(String str:strs) {
                /*
                 * instead of 
                 * 	    blocks_internal[STONE].tex(mgr, ManagerInit.prefixPath + "stone.png");
                 * 	you can now write 
                 * 		blocks_internal[STONE].tex(mgr, ManagerInit.prefixPath + "nestedDir/stone.png");
                 * 
                 * this can help organize textures
                 */
                    //String blockName = str.substring(str.lastIndexOf("\\")+1).replace(".png", ""); //gradle task fixed; accounted for filename in nested directories
                    String blockName = str.replace(".png", ""); //
                    String blockPath = blocksDirectory + "/" + blockName + ".png";//both lines to make sure the fill always ends in .png
                    mgr.load(blockPath, Texture.class);
                    
                
            
	    	}
	    	
	    	
	    	
	    	 for(String str:Blocks.extTextsMGR){
	         	if(new File(str).exists()) {
	         		mgr.load(str, Texture.class);
	         	}else {
	         		System.out.println("Manager Init: ExternalFail! " + str); //TODO 
	         	}}
	    	 
	    	 
	    	
	        	File[] blockFiles = blocksDirectory.listFiles(new FilenameFilter() {
	        	    public boolean accept(File dir, String name) {
	        	        return name.toLowerCase().endsWith(".png");
	        	    }
	        	});
	        	
	        	
	            	
	            
	            mgr.finishLoading();//necessary! asset manager initializes stitcher
	            AssetRefs.setManager(mgr);
	            return mgr;
			}
		}
	}








	 class AbsoluteFileHandleResolver implements FileHandleResolver {
		@Override
		public FileHandle resolve (String fileName) {
			//System.out.println("FileHandle: " + fileName + " exists? : " + new FileHandle(fileName).exists());
			return Gdx.files.absolute(fileName);
		}
	}

