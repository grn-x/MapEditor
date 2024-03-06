package de.grnx.mapeditor.helper;

import java.io.Serializable;

import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class BlockPos extends GridPoint3 implements Serializable
{
	private static final long serialVersionUID = 1L;

	public BlockPos() {
	}

	public BlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlockPos(int... ints) {
		this.x = ints[0];
		this.y = ints[1];
		this.z = ints[2];
	}

	public BlockPos(BlockPos pos) {
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
	}
	
	public BlockPos(Vector3 pos) {
		this.x = MathUtils.floor(x);
		this.y = MathUtils.floor(y);
		this.z = MathUtils.floor(z);
	}
	
	public BlockPos set(GridPoint3 pos) {
		super.set(pos);
		return this;
	}
	
	public BlockPos set(int x, int y, int z) {
		super.set(x, y, z);
		return this;
	}

	public BlockPos add(GridPoint3 other) {
		super.add(other);
		return this;
	}

	public BlockPos add(int x, int y, int z) {
		super.add(x, y, z);
		return this;
	}
	
	public BlockPos add(BlockPos b) {
		this.x= this.x+b.x;
		this.y= this.y+b.y;
		this.z= this.z+b.z;

		return this;
	}

	public BlockPos sub(GridPoint3 other) {
		super.sub(other);
		return this;
	}

	public BlockPos sub(int x, int y, int z) {
		super.sub(x, y, z);
		return this;
	}

	public BlockPos cpy() {
		return new BlockPos(this);
	}
	
	public BlockPos north() {
		this.z = this.z-1;
		return this;
	}
	
	public BlockPos east() {
		this.x = this.x+1;
		return this;
	}
	
	public BlockPos south() {
		this.z = this.z+1;
		return this;
	}
	
	public BlockPos west() {
		this.x = this.x-1;
		return this;
	}
	
	public BlockPos up() {
		this.y = this.y+1;
		return this;
	}
	public BlockPos down() {
		this.y = this.y-1;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof GridPoint3) {
			GridPoint3 p = (GridPoint3)obj;
			return p.x == x && p.y == y && p.z == z;
		}
		return false;
	}
	public String toString(){
		return this.x + ", " + this.y+ ", " + this.z; 
	}
}
