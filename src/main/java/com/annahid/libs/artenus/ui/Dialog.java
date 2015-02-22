package com.annahid.libs.artenus.ui;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.entities.EntityCollection;
import com.annahid.libs.artenus.entities.sprites.LineSprite;

/**
 * This class is the superclass for all dialogs. A dialog is an interface
 * that pauses the normal operation of a scene to do a short task, and
 * resumes the scene upon the completion of the task. Examples of dialogs
 * are pop-up messages, waiting circles, and friend choosers. When a dialog
 * is displayed on a scene, the content of the scene will be still visible
 * behind the dialog, but dimmer than the content of the dialog. 
 * 
 * @author Hessan Feghhi
 *
 */
public abstract class Dialog extends Scene {
	public static final int RESULT_NONE = 0;
	public static final int RESULT_YES = 1;
	public static final int RESULT_NO = 2;
	public static final int RESULT_OK = 3;
	public static final int RESULT_CANCEL = 4;

	private final EntityCollection col;
	private final LineSprite shade;
	private final Scene scene;
	private boolean showing = false;

	/**
	 * The result of the dialog. Initially this field is {@code RESULT_NONE}.
	 * Setting a different value for this field will cause the dialog to be
	 * dismissed. The scene can then check the provided result and take an
	 * action accordingly.
	 */
	protected int result;

	/**
	 * Constructs a {@code Dialog} that will be popped up above a given scene.
	 *
	 * @param parentScene The underlying scene.
	 */
	public Dialog(Scene parentScene) {
		super(parentScene.getStage());

		scene = parentScene;

		final float x = scene.getStage().getGLWidth() / 2, y = scene.getStage().getGLHeight() / 2;

		shade = new LineSprite(new Point2D(x, 0), new Point2D(x, y * 2), x * 2);
		shade.setColorFilter(0, 0, 0);
		shade.setAlpha(0);
		super.add(shade);

		col = new EntityCollection(this);
		super.add(col);

		result = RESULT_NONE;
	}

	public final void show() {
		scene.halt();
		scene.dialog = this;
	}

	@Override
	public void add(Entity entity) {
		col.add(entity);
	}

	public final int getResult() {
		return result;
	}

	/**
	 * Cancels the dialog. The default implementation of this method sets the
	 * {@code result} to {@code RESULT_CANCEL}, and this is all this method
	 * does. But it can be overridden to change this behavior. Pressing the
	 * back button invokes this method by default.
	 */
	public void cancel() {
		result = RESULT_CANCEL;
	}

	@Override
	public final void onLocalLoad() {
	}

	@Override
	public final void advance(float elapsedTime) {
		super.advance(elapsedTime);

		if (result > RESULT_NONE) {
			if (showing) {
				final float alpha = Math.max(0, shade.getAlpha() - elapsedTime * 2);
				shade.setAlpha(alpha);
				showing = alpha > 0.05f || !fadeOut(elapsedTime);
			} else {
				scene.dialog = null;
				scene.unhalt();
				scene.onDialogDismissed(this);
			}
		} else {
			float alpha = shade.getAlpha();

			if (!showing || alpha < 0.25f) {
				alpha = Math.min(0.25f, shade.getAlpha() + elapsedTime * 2);
				shade.setAlpha(alpha);

				if (!showing)
					showing = alpha > 0.2f && fadeIn(elapsedTime);
			} else advanceDialog(elapsedTime);
		}
	}

	/**
	 * Handles the back button for this dialog. The default implementation
	 * cancels the dialog by calling the {@code cancel()} method.
	 */
	@Override
	public boolean onBackButton() {
		cancel();
		return true;
	}

	/**
	 * Advances animation for this dialog. The {@code advance}
	 * method for {@code Scene} is overridden and finalized by this class,
	 * and this method is a replacement. This method is only called if the
	 * dialog is not fading in or out.
	 *
	 * @param elapsedTime The time passed since the last frame.
	 */
	protected abstract void advanceDialog(float elapsedTime);

	/**
	 * Advances animation for this dialog when it is fading in.
	 * The end of the fade-in process is determined by the return value.
	 *
	 * @param elapsedTime The time passed since the last frame.
	 * @return    {@code true} if the dialog has completed fading in, or
	 * {@code false} otherwise.
	 */
	protected abstract boolean fadeIn(float elapsedTime);

	/**
	 * Advances animation for this dialog when it is fading out. The end
	 * of the fade-out process is determined by the return value.
	 *
	 * @param elapsedTime The time passed since the last frame.
	 * @return    {@code true} if the dialog has completed fading out, or
	 * {@code false} otherwise.
	 */
	protected abstract boolean fadeOut(float elapsedTime);

	/**
	 * Indicates whether the underlying scene is fully covered by this dialog.
	 * In this case the underlying scene is not drawn for better performance.
	 *
	 * @return {@code true} if the dialog fully covers the parent scene, and
	 * {@code false} otherwise.
	 */
	public boolean isFull() {
		return false;
	}

	/**
	 * This method always returns {@code true}.
	 */
	final boolean isDialog() {
		return true;
	}
}
