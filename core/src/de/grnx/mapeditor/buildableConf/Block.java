package de.grnx.mapeditor.buildableConf;

import de.grnx.mapeditor.texture.AssetRefs;

import java.io.Serializable;

import com.badlogic.gdx.assets.AssetManager;

public final class Block implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public final byte id;
	public final String name;
	public final boolean isSolid;
	public final boolean isTrans;
	public final boolean collision;
	public final BlockType type;
	public String top;
	public String side;
	public String bottom;
	
	public String invokeLaterTop;
	public String invokeLaterSide;
	public String invokeLaterBottom;
	
	public Block(byte id, String name, boolean isSoildNcollision, BlockType type) {
		this(id, name, isSoildNcollision, isSoildNcollision, type);
	}
	
	
	
	public Block(byte id, String name, boolean isSoild, boolean collision, BlockType type) {
		this(id, name, isSoild, !isSoild, collision, type);
	}
	
	public Block(byte id, String name, boolean isSoild, boolean isTrans, boolean collision, BlockType type) {
		this.id = id;
		this.name = name;
		this.isSolid = isSoild;
		this.isTrans = isTrans;
		this.collision = collision;
		this.type = type;
	}
	
	/** only for external block loading and asyc(? actually not) texture region assignation*/
	public Block(byte id, String name, boolean isSoild, boolean isTrans, boolean collision, BlockType type, String... invokeLater) {
		this.id = id;
		this.name = name;
		this.isSolid = isSoild;
		this.isTrans = isTrans;
		this.collision = collision;
		this.type = type;
		
		
		int j = 0; 
		for (int i = 0; i < 3; i++) {
			switch (i) {
            case 0:
                invokeLaterSide = invokeLater[j];
                break;
            case 1:
                invokeLaterTop = invokeLater[j];
                break;
            case 2:
                invokeLaterBottom = invokeLater[j];
                break;
        }
		    if (j < invokeLater.length-1)j++;
		    
		}

	}
	
	/** for non reference cloning*/
	public Block(Block b) {
		this.id = b.id;
		this.name = b.name;
		this.isSolid = b.isSolid;
		this.isTrans = b.isTrans;
		this.collision = b.collision;
		this.type = b.type; //enums essentially immutable like primitives?
	}
	
	/*public Block tex(TextureRegion all) {
		textures = new BlockTex(all, all, all);
		return this;
	}
	
	public Block tex(TextureRegion topBottom, TextureRegion side) {
		textures = new BlockTex(topBottom, side, topBottom);
		return this;
	}
	
	public Block tex(TextureRegion top, TextureRegion side, TextureRegion topDown) {
		textures = new BlockTex(top, side, topDown);
		return this;
	}*/
	
	public Block tex(AssetManager mgr, String all) {
		try {all = (mgr.get(all) != null) ? all : AssetRefs.missingName;} catch (Exception e) {all = AssetRefs.missingName;}
		this.top = this.bottom = this.side = all;
		return this;
	}
	
	public Block tex(AssetManager mgr, String side, String topBottom) {
		try {topBottom = (mgr.get(topBottom) != null) ? topBottom : AssetRefs.missingName;} catch (Exception e) {topBottom = AssetRefs.missingName;}
		try {side = (mgr.get(side) != null) ? side : AssetRefs.missingName;} catch (Exception e) {side = AssetRefs.missingName;}

		this.top = this.bottom = topBottom;
		this.side = side;
		return this;
	}
	
	public Block tex(AssetManager mgr, String side, String top, String bottom) {
		try {top = (mgr.get(top) != null) ? top : AssetRefs.missingName;} catch (Exception e) {top = AssetRefs.missingName;}
		try {side = (mgr.get(side) != null) ? side : AssetRefs.missingName;} catch (Exception e) {side = AssetRefs.missingName;}
		try {bottom = (mgr.get(bottom) != null) ? bottom : AssetRefs.missingName;} catch (Exception e) {bottom = AssetRefs.missingName;}

		this.top = top;
		this.side=side;
		this.bottom = bottom;
		
		return this;
	}
	
	public void texLoaded(AssetManager mgr) {
		
		try {invokeLaterTop = (mgr.get(invokeLaterTop) != null) ? invokeLaterTop : AssetRefs.missingName;} catch (Exception e) {invokeLaterTop = AssetRefs.missingName;}
		try {invokeLaterSide = (mgr.get(invokeLaterSide) != null) ? invokeLaterSide : AssetRefs.missingName;} catch (Exception e) {invokeLaterSide = AssetRefs.missingName;}
		try {invokeLaterBottom = (mgr.get(invokeLaterBottom) != null) ? invokeLaterBottom : AssetRefs.missingName;} catch (Exception e) {invokeLaterBottom = AssetRefs.missingName;}

		this.top = invokeLaterTop;
		this.side=invokeLaterSide;
		this.bottom = invokeLaterBottom;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (obj.getClass() == Block.class) {
			return ((Block)obj).id == id;
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder build = new StringBuilder(32);
		build.append("id: ").append(id&0xFF).append('\n');
		build.append("name: ").append(name).append('\n');
		build.append("isSoild: ").append(isSolid).append('\n');
		build.append("collision: ").append(collision).append('\n');
		return build.toString();
	}
	
//	public static final class BlockTex implements Serializable {
//		private static final long serialVersionUID = 1L;
//		public final TextureRegion top, side, bottom;
//		
//		public BlockTex(TextureRegion top, TextureRegion side, TextureRegion bottom){
//			this.top = top == null ? TexLib.missing :top;
//			this.side = side == null ? TexLib.missing : side;
//			this.bottom = bottom == null ? TexLib.missing : bottom;
//			
//			/*this.top = top == null ? new TextureRegionSer(): (TextureRegionSer)top;
//			this.side = side == null ? (TextureRegionSer)TexLib.missing : (TextureRegionSer)side;
//			this.bottom = bottom == null ? (TextureRegionSer)TexLib.missing : (TextureRegionSer)bottom;*/
//		}
//	}
}
