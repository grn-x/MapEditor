package de.grnx.mapeditor.texture;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;

public class TextureStitcher {
    private AssetManager assetManager;
    private Texture combinedTexture;
    private HashMap<String, TextureRegion> textureRegions;

    public TextureStitcher(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.textureRegions = new HashMap<>();
    }

    public void stitchTextures() {
        int totalWidth = 0;
        int totalHeight = 0;

        // Calculate total width and height
        for (String asset : assetManager.getAssetNames()) {
            Texture texture = assetManager.get(asset, Texture.class);
            totalWidth += texture.getWidth();
            totalHeight = Math.max(totalHeight, texture.getHeight());
        }

        // Create a Pixmap with the total width and height
        Pixmap pixmap = new Pixmap(totalWidth, totalHeight, Pixmap.Format.RGBA8888);

        int currentWidth = 0;

        // Draw each texture onto the Pixmap
        for (String asset : assetManager.getAssetNames()) {
            Texture texture = assetManager.get(asset, Texture.class);
            texture.getTextureData().prepare();
            Pixmap texturePixmap = texture.getTextureData().consumePixmap();
            pixmap.drawPixmap(texturePixmap, currentWidth, 0);
            currentWidth += texture.getWidth();
            texturePixmap.dispose();
        }

        // Create a new Texture from the Pixmap
        combinedTexture = new Texture(pixmap);

        // Create TextureRegions after combinedTexture has been created
        currentWidth = 0;
        for (String asset : assetManager.getAssetNames()) {
            Texture texture = assetManager.get(asset, Texture.class);
            textureRegions.put(asset, new TextureRegion(combinedTexture, currentWidth, 0, texture.getWidth(), texture.getHeight()));
            currentWidth += texture.getWidth();
        }

        pixmap.dispose();
    }

    public TextureRegion getTextureRegion(String managerPath) {
        return textureRegions.get(managerPath);
    }

    public Texture getCombinedTexture() {
        return combinedTexture;
    }
}