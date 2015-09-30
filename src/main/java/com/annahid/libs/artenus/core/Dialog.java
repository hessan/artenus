package com.annahid.libs.artenus.core;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.entities.EntityCollection;
import com.annahid.libs.artenus.graphics.sprites.LineSprite;

/**
 * The superclass for all dialogs. A dialog is a user interface component that pauses the normal
 * operation of a scene to do a short task, and resumes the scene upon the completion of the task.
 * Examples of dialogs are pop-up messages, waiting circles, and friend choosers. When a dialog is
 * displayed on a scene, the content of the scene will be still visible behind the dialog, but
 * dimmer than the content of the dialog. Input is also captured only by the dialog.
 *
 * @author Hessan Feghhi
 */

public abstract class Dialog extends Scene {
    /**
     * Dialog result indicating the dialog is still active.
     */
    public static final int RESULT_NONE = 0;

    /**
     * Dialog result indicating a positive user response to the dialog.
     */
    public static final int RESULT_YES = 1;

    /**
     * Dialog result indicating a negative user response to the dialog.
     */
    public static final int RESULT_NO = 2;

    /**
     * Dialog result indicating that the dialog is dismissed.
     */
    public static final int RESULT_OK = 3;

    /**
     * Dialog result indicating that the dialog is cancelled.
     */
    public static final int RESULT_CANCEL = 4;

    /**
     * The result of the dialog. Initially this field is {@code RESULT_NONE}.
     * Setting a different value for this field will cause the dialog to be
     * dismissed. The scene can then check the provided result and take an
     * action accordingly.
     */
    protected int result;

    /**
     * The collection of entities that lie above the dark shade and make up te dialog.
     */
    private final EntityCollection col;

    /**
     * The black shade that is used to dim the screen behind the dialog.
     */
    private final LineSprite shade;

    /**
     * The parent scene.
     */
    private final Scene scene;

    /**
     * A value indicating whether the dialog is currently displayed.
     */
    private boolean showing = false;

    /**
     * Creates a dialog that will be displayed above a given scene.
     *
     * @param parentScene The underlying scene
     */
    public Dialog(Scene parentScene) {
        super(parentScene.getStage());
        scene = parentScene;

        final float x = scene.getStage().getLogicalWidth() / 2;
        final float y = scene.getStage().getLogicalHeight() / 2;

        shade = new LineSprite(new Point2D(x, 0), new Point2D(x, y * 2), x * 2);
        shade.setColorFilter(0, 0, 0);
        shade.setAlpha(0);
        super.add(shade);
        col = new EntityCollection();
        super.add(col);
        result = RESULT_NONE;
    }

    /**
     * Halts the underlying scene and displays the dialog on top of it.
     */
    public final void show() {
        scene.halt();
        scene.dialog = this;
    }

    /**
     * Adds an entity to the dialog. Dialog entities only belong to their
     * parent dialog, and are separate from the underlying scene.
     *
     * @param entity The sprite to be added
     */
    @Override
    public void add(Entity entity) {
        col.add(entity);
    }

    /**
     * Gets the result of the dialog. If this method is called before the dialog
     * is dismissed, it returns {@link Dialog#RESULT_NONE}.
     *
     * @return Dialog result code
     */
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
     * Handles the back button for this dialog. The default implementation cancels the dialog by
     * calling the {@code cancel()} method.
     */
    @Override
    public boolean onBackButton() {
        cancel();
        return true;
    }

    /**
     * Advances animation for this dialog. The {@code advance} method for {@code Scene} is
     * overridden and finalized by this class, and this method is a replacement. This method is only
     * called if the dialog is not fading in or out.
     *
     * @param elapsedTime The time passed since the last frame
     */
    protected abstract void advanceDialog(float elapsedTime);

    /**
     * Advances animation for this dialog when it is fading in.
     * The end of the fade-in process is determined by the return value.
     *
     * @param elapsedTime The time passed since the last frame
     * @return {@code true} if the dialog has completed fading in, {@code false} otherwise
     */
    protected abstract boolean fadeIn(float elapsedTime);

    /**
     * Advances animation for this dialog when it is fading out. The end
     * of the fade-out process is determined by the return value.
     *
     * @param elapsedTime The time passed since the last frame
     * @return {@code true} if the dialog has completed fading out, {@code false} otherwise
     */
    protected abstract boolean fadeOut(float elapsedTime);

    /**
     * Indicates whether the underlying scene is fully covered by this dialog.
     * In this case the underlying scene is not drawn for better performance.
     *
     * @return {@code true} if the dialog fully covers the parent scene, {@code false} otherwise
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
