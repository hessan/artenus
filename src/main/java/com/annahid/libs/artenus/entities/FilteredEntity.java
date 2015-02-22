package com.annahid.libs.artenus.entities;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.ui.Scene;

public abstract class FilteredEntity implements Entity {

	@Override
	public final void setAnimation(AnimationHandler animation) {
		target.setAnimation(animation);
	}

	@Override
	public final AnimationHandler getAnimation() {
		return target.getAnimation();
	}

	@Override
	public Point2D getPosition() {
		return target.getPosition();
	}

	@Override
	public final void setPosition(Point2D position) {
		setPosition(position.x, position.y);
	}

	@Override
	public void setPosition(float x, float y) {
		target.setPosition(x, y);
	}

	@Override
	public final void move(float amountX, float amountY) {
		final Point2D pos = getPosition();
		setPosition(pos.x + amountX, pos.y + amountY);
	}

	@Override
	public float getRotation() {
		return target.getRotation();
	}

	@Override
	public void setRotation(float angle) {
		target.setRotation(angle);
	}

	@Override
	public final void rotate(float angle) {
		setRotation(getRotation() + angle);
	}

	@Override
	public Point2D getScale() {
		return target.getScale();
	}

	@Override
	public final void setScale(float scaleValue) {
		setScale(scaleValue, scaleValue);
	}

	@Override
	public void setScale(float scaleX, float scaleY) {
		target.setScale(scaleX, scaleY);
	}

	@Override
	public void setColorFilter(float r, float g, float b) {
		target.setColorFilter(r, g, b);
	}

	@Override
	public final void setColorFilter(RGB rgb) {
		setColorFilter(rgb.r, rgb.g, rgb.b);
	}

	@Override
	public RGB getColorFilter() {
		return target.getColorFilter();
	}

	@Override
	public void onAttach(Scene scene) {
		target.onAttach(scene);
	}

	@Override
	public void onDetach(Scene scene) {
		target.onDetach(scene);
	}

	@Override
	public void advance(float elapsedTime) {
		target.advance(elapsedTime);
	}

	public final Entity getUnderlyingEntity() {
		return target;
	}

	protected FilteredEntity(Entity e) {
		target = e == null ? NullEntity.getInstance() : e;
	}

	protected Entity target;
}
