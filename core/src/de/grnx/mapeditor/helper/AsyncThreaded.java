package de.grnx.mapeditor.helper;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;

/** A light-weight single-threaded abstract class */
public abstract class AsyncThreaded<Packet> implements AsyncTask<Packet>, Disposable 
{
	/** The asynchronous executor. */
	protected final AsyncExecutor exe;
	
	protected AsyncResult<Packet> result;
	
	public AsyncThreaded(final String name) {
		this.exe = new AsyncExecutor(1, name);
	}
	
	/** The method that thread can execute */
	public abstract Packet call() throws Exception;
	
	/** Get the packet after the thread is finished executing. */
	public abstract Packet get();
	
	/** Check if the thread is finished executing. */
	public abstract boolean isDone();
	
	/** Clear anything regardless of the thread is done. */
	public void clear() {
		
	}
	
	@Override
	/** Clear anything then dispose the thread. */
	public void dispose() {
		clear();
		exe.dispose();
	}
}
