package de.grnx.mapeditor.texture;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.grnx.mapeditor.buildableConf.Block;

public class AssetRefs {
	public static final String missingName = ManagerInit.prefixPath + "missing.png";
	public static final String crossairRef = ManagerInit.prefixPath + "crossair.png";
	//public static Texture crossair;
	public static AssetManager mgr;
	public static TextureStitcher textST;
	
	public static void setManager(AssetManager manager) {
		if(!manager.isFinished()) {
			System.out.println("AssetRefs.setManager: manager was not finished loading, before calling setManager. This may cause problems and is discouraged.");
			manager.finishLoading();
		}

		mgr=manager;

		textST = new TextureStitcher(manager);
		textST.stitchTextures();
		//TextureRegion textureRegion = textST.getTextureRegion(managerPath); //testCase

		//crossair = new Texture(ManagerInit.prefixPath +"crossair.png");
	}
	
	public static TextureRegion getText(String str){
		Texture text = mgr.get(str);//nesting doesnt work??? somethings ambiguous  ; need to cast ;/
		return new TextureRegion(text);
	}
	
	public static TextureRegion getMissing() {
		return new TextureRegion((Texture)mgr.get(ManagerInit.prefixPath+"missing.png"));
	}
	
	public static TextureRegion renderRelativeRef(String str) {
		return new TextureRegion((Texture)mgr.get(ManagerInit.prefixPath+str));
	}
	
	public static TextureRegion renderAbsoluteRef(String str) {
		return new TextureRegion((Texture)mgr.get(str));
	}
	
}
