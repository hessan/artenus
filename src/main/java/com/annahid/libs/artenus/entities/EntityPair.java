package com.annahid.libs.artenus.entities;

import android.opengl.GLES10;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.entities.behavior.Animatable;
import com.annahid.libs.artenus.entities.behavior.Transformable;
import com.annahid.libs.artenus.graphics.Effect;
import com.annahid.libs.artenus.entities.behavior.Touchable;
import com.annahid.libs.artenus.entities.behavior.Renderable;
import com.annahid.libs.artenus.ui.Scene;

/**
 * <p>Represents a pair of connected entities. The visual difference between this class and an
 * {@link EntityCollection} with two items is that this class applies effects to underlying
 * entities, whereas an entity collection applies it to itself. The figure below makes the
 * difference clearer. Notice how the shadow looks for each implementation.</p>
 * <p>
 * <img alt="EntityPair vs. EntityCollection" src="data:image/png;base64,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">
 * </p>
 * <p>
 * Performance-wise, this class is recommended whenever you want to encapsulate only two entities.
 * </p>
 */
public class EntityPair
		implements Entity, Animatable, Touchable, Transformable, Renderable {

	/**
	 * Constructs a new entity pair, with given entities.
	 *
	 * @param first The first entity in the pair
	 * @param second The second entity in the pair
	 */
	public EntityPair(Entity first, Entity second) {
		pos = new Point2D(0, 0);
		scale = new Point2D(1, 1);

		this.first = first == null ? NullEntity.getInstance() : first;
		this.second = second == null ? NullEntity.getInstance() : second;
	}

	/**
	 * Gets the first entity in this pair.
	 *
	 * @return The first entity
	 */
	public final Entity getFirst() {
		return first;
	}

	/**
	 * Gets the second entity in this pair.
	 *
	 * @return The second entity
	 */
	public final Entity getSecond() {
		return second;
	}

	@Override
	public void setAnimation(AnimationHandler animation) {
		anim = animation;
	}

	@Override
	public AnimationHandler getAnimation() {
		return anim;
	}

	@Override
	public Point2D getPosition() {
		return pos;
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public Point2D getScale() {
		return scale;
	}

	@Override
	public void setRotation(float angle) {
		rotation = angle;
	}

	@Override
	public void setScale(float scaleValue) {
		scale.x = scale.y = scaleValue;
	}

	@Override
	public void setScale(float scaleX, float scaleY) {
		scale.x = scaleX;
		scale.y = scaleY;
	}

	@Override
	public void rotate(float angle) {
		rotation += angle;
	}

	@Override
	public void move(float amountX, float amountY) {
		pos.x += amountX;
		pos.y += amountY;
	}

	@Override
	public void setPosition(Point2D position) {
		setPosition(position.x, position.y);
	}

	@Override
	public void setPosition(float x, float y) {
		pos.x = x;
		pos.y = y;
	}

	@Override
	public void setColorFilter(float r, float g, float b) {
		if(first instanceof Renderable)
			((Renderable)first).setColorFilter(r, g, b);
		if(second instanceof Renderable)
			((Renderable)second).setColorFilter(r, g, b);
	}

	@Override
	public void setColorFilter(RGB rgb) {
		setColorFilter(rgb.r, rgb.g, rgb.b);
	}

	@Override
	public RGB getColorFilter() {
		if(first instanceof Renderable)
			return ((Renderable)first).getColorFilter();
		else if(second instanceof Renderable)
			return ((Renderable)second).getColorFilter();
		else return new RGB(0, 0, 0);
	}

	@Override
	public void onAttach(Scene scene) {
		first.onAttach(scene);
		second.onAttach(scene);
	}

	@Override
	public void onDetach(Scene scene) {
		first.onDetach(scene);
		second.onDetach(scene);
	}

	/**
	 * Renders both entities in the pair.
	 *
	 * @param flags Rendering flags
	 */
	@Override
	public void render(int flags) {
		GLES10.glPushMatrix();
		GLES10.glTranslatef(pos.x, pos.y, 0);
		GLES10.glRotatef(rotation, 0, 0, 1);
		GLES10.glScalef(scale.x, scale.y, 0);

		if(first instanceof Renderable)
			((Renderable)first).render(flags);
		if(second instanceof Renderable)
			((Renderable)second).render(flags);

		GLES10.glPopMatrix();
	}

	@Override
	public void advance(float elapsedTime) {
		if (anim != null)
			anim.advance(this, elapsedTime);

		if(first instanceof Animatable)
			((Animatable)first).advance(elapsedTime);
		if(second instanceof Animatable)
			((Animatable)second).advance(elapsedTime);
	}

	@Override
	public boolean handleTouch(int action, int pointerIndex, float x, float y) {
		return (
						first instanceof Touchable
								&& ((Touchable) first).handleTouch(action, pointerIndex, x, y)
				) || (
						second instanceof Touchable
								&& ((Touchable) second).handleTouch(action, pointerIndex, x, y)
				);
	}

	@Override
	public Effect getEffect() {
		if(first instanceof Renderable)
			return ((Renderable)first).getEffect();
		else if(second instanceof Renderable)
			return ((Renderable)second).getEffect();
		else return null;
	}

	@Override
	public void setEffect(Effect effect) {
		if(first instanceof Renderable)
			((Renderable)first).setEffect(effect);
		if(second instanceof Renderable)
			((Renderable)second).setEffect(effect);
	}

	private Entity first, second;
	private final Point2D pos, scale;
	private float rotation;

	protected AnimationHandler anim;
}
