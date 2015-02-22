package com.annahid.libs.artenus.entities.physics;

/**
 * An interface for classes that process physical collision events. An instance of a class
 * that implements this interface is assigned to a
 * {@link com.annahid.libs.artenus.entities.physics.PhysicsSimulator} using the
 * {@code setCollisionListener} method and on each collision, the {@code onCollision} method is
 * called on the listener with information relating to the collision.
 *
 * @author Hessan Feghhi
 *
 */
public interface CollisionListener {
	/**
	 * This method is called whenever a collision happens in the physical
	 * world.
	 *
	 * @param info Information about the collision.
	 */
	public void onCollision(CollisionInfo info);
}
