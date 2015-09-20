package com.annahid.libs.artenus.entities;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.entities.behavior.Transformable;
import com.annahid.libs.artenus.ui.Scene;

class NullEntity implements Entity, Transformable {
	private static final Entity instance = new NullEntity();
	private static final Point2D dummyPoint = new Point2D(1, 1);

	public static Entity getInstance() {
		return instance;
	}

	private NullEntity() {
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
	public void onAttach(Scene scene) {
	}

	@Override
	public void onDetach(Scene scene) {
	}
}
