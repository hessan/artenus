package com.annahid.libs.artenus.entities.physics;

/**
 * Contains information about a collision. An instance of this class
 * is passed to the {@code onCollision} method of the {@link CollisionListener}
 * interface per each pair of colliding bodies.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public final class CollisionInfo {
	CollisionInfo() { }

	/**
	 * The first body involved in the collision.
	 */
	public PhysicalBody body1;

	/**
	 * The second body involved in the collision.
	 */
	public PhysicalBody body2;

	/**
	 * An array of normal impulses for the collision.
	 */
	public float[] normalImpulses;

	/**
	 * An array of tangent impulses for the collision.
	 */
	public float[] tangentImpulses;

	/**
	 * The number of impulses included in this collision.
	 */
	public int impulseCount;

	/**
	 * The tangent relative speed of colliding bodies.
	 */
	public float tangentSpeed;
}
