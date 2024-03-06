package de.grnx.mapeditor.helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

import de.grnx.mapeditor.Options;

import java.awt.*;

/** A useful utilities for this game and LibGDX. */
public final class Util 
{	
	/** A cached screen size. */
	public static final Dimension screen = new Dimension();
	/** A cached world screen size. */
	public static final Dimension world  = new Dimension();
	
	/** Fast floor for double. */
	public static int floor(final double x) {
		final int xi = (int)x;
		return x < xi ? xi - 1 : xi;
	}
	
	/** Null-safe dispose method for disposable object. */
	public static void disposes(Disposable dis) {
		if (dis != null) dis.dispose();
	}
	
	/** Null-safe disposes method for disposable objects. */
	public static void disposes(Disposable... dis) {
		final int size = dis.length;
		for (int i = 0; i < size; ++i) {
			final Disposable d = dis[i];
			if (d != null) d.dispose();
		}
	}
	
	/** Resets the mouse position to the center of the screen. */
	public static void resetMouse() {
		//redundant method because in render mouse is reset to 0 0
		if(Options.mouseCaught)Gdx.input.setCursorPosition(screen.width/2, screen.height/2);
		//TODO!!! RESET MOUSE COMMENT
		//TODO RESET MOUSE CURSOR COMMENTED
	}
	
	/** Seconds to 60 tick update. */
	public static final int seconds(int second) {
		return second * 60;
	}
	
	/** Milliseconds to 60 tick update. */
	public static final int milseconds(int mils) {
		return MathUtils.roundPositive((mils / 1000f) * 60f);
	}
	
	/** Utility log. */
	public static void log(Object tag, Object obj) {
		if (tag instanceof Class) {
			Gdx.app.log(((Class<?>)tag).getSimpleName(), obj.toString());
		} else {
			Gdx.app.log(tag.toString(), obj.toString());
		}
	}
	/**takes everything as input**/
	public static String stringSlut(Object ... objs) {
		StringBuilder sb = new StringBuilder();
		for(Object o:objs) {
			sb.append(o+";\t");
		}
		return sb.toString();
	}

}
