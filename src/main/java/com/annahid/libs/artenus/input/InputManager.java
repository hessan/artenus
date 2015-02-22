package com.annahid.libs.artenus.input;

import android.content.Context;
import android.view.MotionEvent;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.ui.Stage;

/**
 * The base class for all input managers. An input manager maps inputs from a specific
 * source to the unified Artenus game controller pattern. A virtual controller in this  
 * framework has a direction knob and four action keys. The direction knob can indicate
 * any possible direction in a circle centered at the knob.
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class InputManager {
	/**
	 * Key identifier for the first action key.
	 */
	public static final int KEY_ACTION1 = 16;
	
	/**
	 * Key identifier for the second action key. 
	 */
	public static final int KEY_ACTION2 = 32;
	
	/**
	 * Key identifier for the third action key.
	 */
	public static final int KEY_ACTION3 = 64;
	
	/**
	 * Key identifier for the fourth action key.
	 */
	public static final int KEY_ACTION4 = 128;
    /**
     * Indicates that a pressed gesture has just started.
     */
    public static final int EVENT_DOWN = 0;
    /**
	 * Indicates that a pressed gesture has finished.
	 */
	public static final int EVENT_UP = 1;
    /**
	 * Indicates that a change has happened during a press gesture (between
	 * {@link com.annahid.libs.artenus.input.InputManager#EVENT_UP} and {@link com.annahid.libs.artenus.input.InputManager#EVENT_DOWN}).
	 */
	public static final int EVENT_MOVE = 2;

    private Point2D savedDirection;
	private int keyMap, savedKeyMap;
	private InputListener l = null; 
	
	/**
	 * The current direction of the knob. Subclasses are allowed to adjust the
	 * coordinates of this field according to user input. However, this variable
	 * should never be set to {@code null} or it will cause application crash.
	 * To keep up with the standard operation of the framework's input system
	 * subclasses should normalize the direction (so the length of the direction
	 * vector is always 1 or zero).
	 */
	protected Point2D direction;
	
	/**
	 * Constructs an {@code InputManager} with all buttons unpressed and the
	 * directional knob at its rest (0).
	 */
	protected InputManager() {
		keyMap = 0;
		direction = new Point2D(0, 0);
		savedDirection = new Point2D(0, 0);
	}

	/**
	 * Appoints an {@link InputListener} to respond to input status changes of this
	 * {@code InputManager}. Each time the key map or the direction changes, the
	 * listener is signaled to process the event.
	 * @param listener	The new listener to be appointed. Providing {@code null} for
	 * this parameter removes the listener.
	 * @see InputListener
	 */
	public final void setListener(InputListener listener) {
		l = listener;
	}

	/**
	 * This method holds the key map to process new input. Subclasses must call this
	 * method before processing input. Holding the key map helps determine whether
	 * the associated {@link InputListener} should be called back. This method
	 * should be followed by {@link #releaseKeyMap()} in order to complete the
	 * processing of input.
	 * @see InputListener
	 */
	protected void holdKeyMap() {
		savedKeyMap = keyMap;
		savedDirection.x = direction.x;
		savedDirection.y = direction.y;
	}

	/**
	 * Releases the previously held key map. Once this method is called, the new
	 * key map and knob direction are compared to their corresponding values before
	 * holding. In case of any change, the associated {@link InputListener} will be
	 * called back to respond to the change.
	 */
	protected void releaseKeyMap() {
		if(l != null && (keyMap != savedKeyMap || direction.x != savedDirection.x || direction.y != savedDirection.y))
			l.inputStatusChanged(this);
	}
	
	/**
	 * Changes the status of a key or a combination of keys to pressed. Subclasses
	 * should use this method to alter the key map. This method must be called
	 * after a {@link #holdKeyMap()} call and before a {@link #releaseKeyMap()}
	 * call. Otherwise the changes will not be reported.
	 * @param keyCode	The key identifier(s) to be pressed.
	 */
	protected void pressKeys(int keyCode) {
		keyMap |= keyCode;
	}
	
	/**
	 * Changes the status of a key or a combination of keys to released. Subclasses
	 * should use this method to alter the key map. This method must be called
	 * after a {@link #holdKeyMap()} call and before a {@link #releaseKeyMap()}
	 * call. Otherwise the changes will not be reported.
	 * @param keyCode	The key identifier(s) to be released.
	 */
	protected void releaseKeys(int keyCode) {
		keyMap &= ~keyCode;
	}
	
	/**
	 * Determines whether the key (or any of the multiple keys) specified are
	 * currently in the pressed state.
	 * @param keyCode	The key identifier(s) to check.
	 * @return	{@code true} if the key (or any of the keys} are pressed or
	 * {@code false} otherwise.
	 */
	public boolean isKeyPressed(int keyCode) {
		return (keyMap & keyCode) != 0;
	}
	
	/**
	 * Gets the complete key map. The key map is a bit-set containing information
	 * about the pressed state of the keys. If you {@code and} the return value with
	 * any of the key identifiers, the result will be zero if the corresponding key
	 * is not pressed, and non-zero otherwise. 
	 * @return	The key map bitwise integer.
	 */
	public int getKeyMap() {
		return keyMap;
	}

	/**
	 * Gets the status of the direction knob.
	 * @return The direction vector. The length of the vector is normally either
	 * 1 or zero. 
	 */
	public final Point2D getDirection() {
		return direction;
	}

	/**
	 * Handles a touch event. All input managers in this framework are informed of
	 * touch events, regardless of the source of their input. Subclasses of
	 * {@code InputManager} that need to handle touch events can override this method.
	 * Calling the superclass method in this case is NOT necessary.
	 * @param stage	The {@link com.annahid.libs.artenus.ui.Stage} this event originates from.
	 * @param event	The {@code MotionEvent} associated to this event. See Android
	 * documentation for information about  {@code MotionEvent}.
	 * @see com.annahid.libs.artenus.ui.Stage
	 */
	public void onTouchEvent(Stage stage, MotionEvent event) {}
	
	/**
	 * Registers this {@code InputManager} with the given context. Some input
	 * managers might need some initialization that needs context. This method is
	 * where they should do those procedures. All input managers must implement
	 * this method. This method is called internally and you must not invoke it
	 * manually.
	 * @param context	The application context for this input manager.
	 */
	public abstract void register(Context context);
	
	/**
	 * Unregisters this {@code InputManager} and releases resources associated to
	 * it. This method is called internally and you must not invoke it manually.
	 */
	public abstract void unregister();
}
