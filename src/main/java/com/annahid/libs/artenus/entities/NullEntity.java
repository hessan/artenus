package com.annahid.libs.artenus.entities;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.graphics.Effect;
import com.annahid.libs.artenus.ui.Scene;

class NullEntity implements Entity {
	private static final Entity instance = new NullEntity();

	public static Entity getInstance() {
		return instance;
	}

	private Point2D dummyPoint = new Point2D(1, 1);
	private RGB dummyRGB = new RGB(0, 0, 0);

	private NullEntity() {
	}

	@Override
	public void setAnimation(AnimationHandler animation) {
	}

	@Override
	public AnimationHandler getAnimation() {
		return null;
	}

	@Override
	public Point2D getPosition() {
		return null;
	}

	@Override
	public void setPosition(Point2D position) {
	}

	@Override
	public void setPosition(float x, float y) {
	}

	@Override
	public void move(float amountX, float amountY) {
	}

	@Override
	public float getRotation() {
		return 0;
	}

	@Override
	public void setRotation(float angle) {
	}

	@Override
	public void rotate(float angle) {
	}

	@Override
	public Point2D getScale() {
		return dummyPoint;
	}

	@Override
	public void setScale(float scaleValue) {
	}

	@Override
	public void setScale(float scaleX, float scaleY) {
	}

	@Override
	public void setColorFilter(float r, float g, float b) {
	}

	@Override
	public void setColorFilter(RGB rgb) {
	}

	@Override
	public RGB getColorFilter() {
		return dummyRGB;
	}

	@Override
	public void onAttach(Scene scene) {
	}

	@Override
	public void onDetach(Scene scene) {
	}

	@Override
	public Effect getEffect() {
		return null;
	}

	@Override
	public void setEffect(Effect effect) {
	}

	@Override
	public void advance(float elapsedTime) {
	}

	@Override
	public void render(int flags) {
	}
}
