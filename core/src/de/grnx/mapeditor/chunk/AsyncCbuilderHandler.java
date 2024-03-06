package de.grnx.mapeditor.chunk;

import de.grnx.mapeditor.canvas.World;
import de.grnx.mapeditor.chunk.AsyncCbuilder.VolatileMeshPacket;
import de.grnx.mapeditor.helper.AsyncThreaded;
import de.grnx.mapeditor.helper.AtomicPointedCollection;

import com.badlogic.gdx.utils.async.AsyncResult;

public class AsyncCbuilderHandler extends AsyncThreaded<AtomicPointedCollection<VolatileMeshPacket>>
{
	public final AtomicPointedCollection<Chunk> chunks;
	private final AtomicPointedCollection<VolatileMeshPacket> packets;
	private AsyncResult<AtomicPointedCollection<VolatileMeshPacket>> result;
	public final int length;

	private final AsyncCbuilder build;

	public AsyncCbuilderHandler(World world, int length) {
		super("Chunk Loader");
		build = new AsyncCbuilder(world);
		chunks = new AtomicPointedCollection<Chunk>(length);
		packets = new AtomicPointedCollection<VolatileMeshPacket>(length);
		for (int i = 0; i < length; i++) {
			packets.set(i, new VolatileMeshPacket());
		}
		this.length = length;
	}
	
	public void start() {
		result = exe.submit(this);
	}

	@Override
	public AtomicPointedCollection<VolatileMeshPacket> call() {
		//final long a = System.currentTimeMillis();
		final int size = chunks.size;
		for (int i = 0; i < size; i++) {
			final Chunk chunk = chunks.get(i);
			build.all(chunk, packets.get(i));
			chunk.isChunkSafe = true;
		}
		packets.size = size;
		//System.out.println(System.currentTimeMillis() - a);
		return packets;
	}

	@Override
	public AtomicPointedCollection<VolatileMeshPacket> get() {
		if (result == null) return null;
		final AtomicPointedCollection<VolatileMeshPacket> packet = result.get();
		result = null;
		return packet;
	}

	@Override
	public boolean isDone() {
		return result == null ? true : result.isDone();
	}

	@Override
	public void clear() {
		if (result != null && result.get() != null) {
			final int size = packets.size;
			for (int i = 0; i < size; i++) {
				packets.get(i).dispose();
			}
		}
		result = null;
	}
}
