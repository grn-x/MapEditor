package de.grnx.mapeditor.gles30;

import com.badlogic.gdx.utils.Disposable;

public interface Vertex extends Disposable 
{
	public void bind();
	public void unbind();
}
