package de.grnx.mapeditor.controllable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.IntArray;

import de.grnx.mapeditor.Options;

public final class Inputs extends InputAdapter
{
	private static Inputs input;
	
	private final IntArray keysPressed = new IntArray(false, 16);
	private final IntArray keysJustPre = new IntArray(false, 16);
	
	private final GridPoint2 lastPos = new GridPoint2(), movePos = new GridPoint2(), tmp = new GridPoint2();
	
	public Inputs() {
		input = this;
	}
	
	public boolean keyDown (int keycode) {
		if (!keysPressed.contains(keycode)) {
			keysPressed.add(keycode);
		}
		if (!keysJustPre.contains(keycode)) {
			keysJustPre.add(keycode);
		}
		return false;
	}

	public boolean keyUp (int keycode) {
		keysPressed.removeValue(keycode);
		return false;
	}
	
	/** Must be called when window has been resized. */
	public void clear() {
		keysPressed.clear();
		keysJustPre.clear();
	}
	
	/** Must be called at the end of <code>render()</code> to reset for next cycle*/
	public void clearJustPressed() {
		keysJustPre.clear();
	}
	
	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		move(screenX, screenY);
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		move(screenX, screenY);
		return false;
	}
	
	private void move(int screenX, int screenY) {
		movePos.add(lastPos.x - screenX, lastPos.y - screenY);
		lastPos.set(screenX, screenY);
	}
	
	public static GridPoint2 getMouseDelta() {
		if(Options.mouseCaught) {
		input.lastPos.set(0, 0);
		input.tmp.set(input.movePos);
		input.movePos.set(0, 0);
		if(Options.mouseCaught)Gdx.input.setCursorPosition(0, 0);
		return input.tmp;
		}else {
			input.lastPos.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			input.tmp.set(input.movePos);
			input.movePos.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			if(Options.mouseCaught)Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			return input.tmp;
		}
		
		
	}
	
	/** Resets the mouse's position to (0, 0). */
	public static void resetMouse() {
		input.lastPos.set(0, 0);
		input.movePos.set(0, 0);
		if(Options.mouseCaught)Gdx.input.setCursorPosition(0, 0);
	}
	
	/** Returns whether the key has just been pressed.
	 *  If pressed, then remove the pressed key (will return false on next call with same key).
	 * 
	 * @param key The key code as found in {@link Input.Keys}.
	 * @return true if key just pressed, else false. */
	public static boolean isKeyJustPressed(int key) {
		final int index = findAndGetIndex(input.keysJustPre, key);
		if (index == -1) return false;
		input.keysJustPre.removeIndex(index);
		return true;
	}
	
	/** Returns whether the key is pressed.
	 * 
	 * @param key The key code as found in {@link Input.Keys}.
	 * @return true or false. */
	public static boolean isKeyPressed(int key) {
		return input.keysPressed.contains(key);
	}
	
	/** @return -1 if wasn't found. */
	private static int findAndGetIndex(IntArray array, int key) {
		final int size = array.size;
		final int[] ints = array.items;
		for (int i = 0; i < size; ++i) {
			if (ints[i] == key)	return i;
		}
		return -1;
	}
}
