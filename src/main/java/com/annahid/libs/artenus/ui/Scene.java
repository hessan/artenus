package com.annahid.libs.artenus.ui;

import android.opengl.GLES10;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.entities.EntityCollection;
import com.annahid.libs.artenus.input.InputManager;
import com.annahid.libs.artenus.entities.physics.PhysicsSimulator;
import com.annahid.libs.artenus.input.Touchable;

import javax.microedition.khronos.opengles.GL10;

/**
 * This class represents a single screen in a game. If you view the whole game as a play,
 * the terms for {@link Stage} and {@code Scene} will make complete sense to you, as
 * they are intentionally named this way in the framework. A game can have several scenes
 * based on its nature. For example, a simple game consists of a menu scene, a game scene
 * and a game over scene where scores and rankings are shown. Scenes methods provide the
 * means to manipulate stage properties and navigate between scenes.
 *
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("UnusedDeclaration")
public class Scene implements Touchable {
	/**
	 * The parent stage for this scene. You MUST not modify the value of this field as it
	 * might cause inconsistency in the scene-stage relation.
	 */
	protected Stage stage;

	private final EntityCollection sprites = new EntityCollection(this);
	private PhysicsSimulator physics = null;
	private final Point2D trans = new Point2D(0.0f, 0.0f);
	private boolean loaded = false;
	private long haltStart = 0;
	RGB bgColor = new RGB(0, 0, 0);
	Dialog dialog = null;

	/**
	 * Constructs a new scene belonging to the given {@code Stage}.
	 *
	 * @param parentStage The parent stage
	 * @see Stage
	 */
	public Scene(Stage parentStage) {
		stage = parentStage;
	}

	/**
	 * Gets the default physics simulator for this {@code Scene}. The physics simulator
	 * is originally {@code null}, but it is allocated on the first access, including
	 * the invocation of this method.
	 *
	 * @return The physics simulator
	 * @see PhysicsSimulator
	 */
	public final PhysicsSimulator getPhysicsSimulator() {
		if (physics == null)
			physics = new PhysicsSimulator();
		return physics;
	}

	/**
	 * Gets the background color for this scene. The returned object is not a copy, and
	 * modifications will take effect in the scene.
	 *
	 * @return The background color
	 */
	public final RGB getBackColor() {
		return bgColor;
	}

	/**
	 * Sets the background color for this scene.
	 *
	 * @param color The background color
	 */
	public final void setBackColor(RGB color) {
		bgColor.r = color.r;
		bgColor.g = color.g;
		bgColor.b = color.b;
	}

	/**
	 * Sets the background color for this scene.
	 *
	 * @param r The red component of the background color
	 * @param g The green component of the background color
	 * @param b The blue component of the background color
	 */
	public final void setBackColor(float r, float g, float b) {
		bgColor.r = r;
		bgColor.g = g;
		bgColor.b = b;
	}

	/**
	 * Adds a sprite to the scene. Note that if you are using sprites as attachments
	 * in game objects, calling this method is not necessary and sprite addition and
	 * removal will be handled automatically. Use this method only if you are using
	 * sprites separately.
	 *
	 * @param entity The sprite to be added
	 * @see com.annahid.libs.artenus.entities.sprites.SpriteEntity
	 */
	public void add(Entity entity) {
		sprites.add(entity);
	}

	/**
	 * Removes a sprite from the scene. Note that if you are using sprites as
	 * attachments in game objects, calling this method is not necessary and sprite
	 * addition and removal will be handled automatically. Use this method only if
	 * you are using sprites separately.
	 *
	 * @param sprite The sprite to be removed
	 * @see com.annahid.libs.artenus.entities.sprites.SpriteEntity
	 */
	public final void remove(Entity sprite) {
		sprites.recursiveRemove(sprite);
	}

	/**
	 * Advances the animation for this scene. Subclasses should always call this
	 * method from their superclass, as it handles physics and sprite animations.
	 *
	 * @param elapsedTime The time elapsed since last frame
	 */
	public void advance(float elapsedTime) {
		if (!isHalted()) {
			sprites.advance(elapsedTime);

			if (physics != null) {
				physics.step(elapsedTime, 6, 4);
				physics.handleCollisions();
			}
		}

		if (dialog != null)
			dialog.advance(elapsedTime);
	}

	/**
	 * Gets the translation vector of this scene. The object returned by this
	 * method is not a copy, and modifying the coordinates will take effect
	 * in the scene immediately.
	 *
	 * @return The translation vector
	 */
	public final Point2D translationVector() {
		return trans;
	}

	/**
	 * This method provides a centering operation intended for platform-based
	 * games. Imagine you have a 2-dimensional map for your game that is
	 * bounded from all four corners (it is not an infinite map). Then this
	 * method manipulates the scene's translation vector so that the desired
	 * point on the map is brought into view and in the center if possible.
	 * If bringing the point to the center causes the dark regions of the
	 * map (regions outside the map) to be brought into view, the point will
	 * be brought to the nearest position to the center of the screen, where
	 * the dark regions of the map are not revealed.
	 *
	 * @param x   The x coordinate of the point to be brought to center
	 * @param y   The y coordinate of the point to be brought to center
	 * @param vx1 The x coordinate of the top-left corner of the map
	 * @param vy1 The y coordinate of the top-left corner of the map
	 * @param vx2 The x coordinate of the bottom-right corner of the map
	 * @param vy2 The y coordinate of the bottom-right corner of the map
	 * @param vpw Viewport width. This can simply be your stage width
	 * @param vph Viewport height. This can simply be your stage height
	 */
	public final void center(
			float x, float y, float vx1, float vy1, float vx2, float vy2, float vpw, float vph) {
		if (y + trans.y > vph / 2) {
			if (trans.y > -(vy2 - vph))
				trans.y -= y + trans.y - vph / 2;
		} else if (y + trans.y < vph / 2) {
			if (trans.y < vy1)
				trans.y -= y + trans.y - vph / 2;
		}
		if (x + trans.x > vpw / 2) {
			if (trans.x > -(vx2 - vpw))
				trans.x -= x + trans.x - vpw / 2;
		} else if (x + trans.x < vpw / 2) {
			if (trans.x < 0)
				trans.x -= x + trans.x - vpw / 2;
		}
	}

	/**
	 * Changes the translation vector to the given point.
	 *
	 * @param x The x coordination of the new translation vector
	 * @param y The y coordination of the new translation vector
	 */
	public final void moveTo(float x, float y) {
		trans.x = x;
		trans.y = y;
	}

	/**
	 * Halts the scene. All animation and physics will stop until the scene
	 * is unhalted. Note that although dialogs are also scenes, they cannot
	 * be halted.
	 *
	 * @see Dialog
	 */
	public final void halt() {
		if (!isDialog()) {
			haltStart = System.currentTimeMillis();
			onHalted();
		}
	}

	/**
	 * Checks whether the scene is halted.
	 *
	 * @return {@code true} if the scene is halted, and {@code false} otherwise
	 */
	public final boolean isHalted() {
		return haltStart > 0;
	}

	/**
	 * Un-halts a previously halted scene. All animation and physics will resume.
	 */
	public final void unhalt() {
		if (!isDialog()) {
			final float delay = (float) (System.currentTimeMillis() - haltStart) / 1000.0f;
			haltStart = 0;
			onUnhalted(delay);
		}
	}

	/**
	 * Gets the current dialog that is currently showing on the scene. Dialogs
	 * halt the scene until they are dismissed.
	 *
	 * @return The current dialog
	 * @see Dialog
	 */
	public final Dialog getDialog() {
		return dialog;
	}

	/**
	 * Gets the stage that this scene is displayed on.
	 *
	 * @return The parent stage
	 */
	public final Stage getStage() {
		return stage;
	}

	/**
	 * Indicates whether the resources for this scene are loaded.
	 *
	 * @return {@code true} if loaded, and {@code false} otherwise
	 */
	public final boolean isLoaded() {
		return loaded;
	}

	/**
	 * Handles the back button event. Subclasses can override this method to
	 * handle the back button. Calling the superclass method is not necessary.
	 *
	 * @return {@code true} if handled, {@code false} if passed
	 */
	public boolean onBackButton() {
		return true;
	}

	/**
	 * This method is called when all resources for the scene are safely in
	 * place. It is a good entry point for subclasses to create their objects
	 * and sprites. Remember to call the superclass method.
	 */
	public void onLoaded() {
		loaded = true;
	}

	/**
	 * If a scene has local resources, it should implement this method to load
	 * those resources. Inside this method you can call the {@code loadLocal}
	 * method from {@code TextureManager} to load your resources.
	 */
	public void onLocalLoad() {
	}

	/**
	 * Handles input for this scene.
	 *
	 * @param inputManager The input manager that triggered the event
	 */
	public void handleInput(InputManager inputManager) {
	}

	/**
	 * This method is called after this scene goes to a halted state. You can
	 * override this method for example to pause your game timers.
	 */
	protected void onHalted() {
	}

	/**
	 * This method is called after this scene goes out of a halted state. You
	 * can use this method to resume your game timers.
	 *
	 * @param delay The time passed, in seconds, in the halted state
	 */
	protected void onUnhalted(float delay) {
	}

	/**
	 * This method is called whenever a dialog is dismissed from the scene.
	 * Dialogs always invoke this method on their parent scene when they
	 * are dismissed.
	 *
	 * @param dialog The dismissed dialog
	 * @see Dialog
	 */
	protected void onDialogDismissed(Dialog dialog) {
	}

	/**
	 * Handles a touch event on this scene. Touch events are always passed
	 * to the scene currently being displayed on the stage, except if there
	 * is a dialog above the scene. In that case the dialog receives touch
	 * events.
	 *
	 * @param action The touch action. See {@link com.annahid.libs.artenus.input.InputManager}
	 *               for possible values.
	 * @param x      The x coordination of the touched point
	 * @param y      The y coordination of the touched point
	 * @see Dialog
	 */
	public boolean handleTouch(int action, float x, float y) {
		return sprites.handleTouch(action, x, y);
	}

	final void handleTouch(int pointerIndex, int action, float x, float y) {
		handleTouch(action, x, y);
	}

	/**
	 * Renders the scene on the given OpenGL context.
	 */
	final void render() {
		boolean skipRender = false;

		if (dialog != null)
			skipRender = dialog.isFull();

		if (!skipRender) {
			GLES10.glMatrixMode(GL10.GL_MODELVIEW);
			sprites.render(0);
		}

		if (dialog != null) {
			if (!dialog.isLoaded())
				dialog.onLoaded();

			dialog.render();
		}
	}

	/**
	 * Indicates whether this scene is a dialog.
	 *
	 * @return {@code true} if this scene is a dialog, {@code false} otherwise
	 */
	boolean isDialog() {
		return false;
	}
}
