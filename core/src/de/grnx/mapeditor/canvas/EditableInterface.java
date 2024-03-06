package de.grnx.mapeditor.canvas;

import de.grnx.mapeditor.buildableConf.Block;
import de.grnx.mapeditor.chunk.Chunk;
import de.grnx.mapeditor.helper.BlockPos;

public final class EditableInterface 
{
	private final World world;

	public EditableInterface(final World world) {
		this.world = world;
	}

	public void breakBlock(final BlockPos in,  final Block block) {
		final Chunk chunk = world.getChunkAt(in.x, in.y, in.z);
		if (chunk != null) {
			chunk.editBlock(in.x, in.y, in.z, block);
			world.forceDirty();
		}
	}

	public void placeBlock(final BlockPos out, final Block block) {
		final Chunk chunk = world.getChunkAt(out.x, out.y, out.z);
		if (chunk != null) {
			chunk.editBlock(out.x, out.y, out.z, block);
			world.forceDirty();
		}
	}
}
