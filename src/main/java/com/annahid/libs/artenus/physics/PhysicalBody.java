package com.annahid.libs.artenus.physics;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.entities.FilteredEntity;
import com.annahid.libs.artenus.entities.behavior.Transformable;
import com.annahid.libs.artenus.core.Scene;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FilteredEntity} that represents a physical body in physics simulation. As the physical
 * body represented by this entity moves and rotates due to physical simulation, this entity will
 * modify the position and rotation of the underlying {@code Entity}, depending on connection
 * preferences.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public class PhysicalBody extends FilteredEntity {
    static final class Descriptor {
        Behavior type = Behavior.DYNAMIC;
        float angularVelocity = 0;
        float linearDamping = 0;
        Point2D linearVelocity = new Point2D(0, 0);
        boolean active = true;
        float angularDamping = 0;
        Point2D position = new Point2D(0, 0);
        float angle = 0;
        boolean bullet = false;
        boolean fixedRotation = false;
    }

    /**
     * Defines position connection. If this connection is set, the {@code PhysicalBody} will lock
     * the position of the underlying entity to that of itself. This connection is set by default
     * for every {@code PhysicalBody}.
     */
    public static final int POSITION = 1;

    /**
     * Defines rotation connection. If this connection is set, the {@code PhysicalBody} will lock
     * the rotational angle of the underlying entity to that of itself. This connection is set by
     * default for every {@code PhysicalBody}.
     */
    public static final int ROTATION = 2;

    /**
     * The Behavior specifies how a physical body is simulated. The default is DYNAMIC.
     */
    public enum Behavior {
        /**
         * Defines a dynamic body. Dynamic bodies are normally simulated.
         */
        DYNAMIC,

        /**
         * Defines a static body. Static bodies do not move and are fixed at
         * their place. Collisions dot alter their positions.
         */
        STATIC,

        /**
         * Defines a kinematic body. Kinematic bodies can be manually moved or
         * rotated. But they cannot be moved by collisions or external forces.
         */
        KINEMATIC
    }

    public PhysicalBody(Entity entity) {
        this(entity, null);
    }

    /**
     * Constructs a {@code PhysicalBody} with a given shape.
     *
     * @param target    The entity to attach to this physical body
     * @param shapeDesc The initial {@code Shape} specification of the body
     */
    public PhysicalBody(Entity target, Shape shapeDesc) {
        super(target);

        if (!(this.target instanceof Transformable))
            throw new IllegalArgumentException(
                    "A physical body can only accept a transformable entity as target.");

        desc = new Descriptor();
        shape = shapeDesc;
        body = null;
    }

    /**
     * Gets the mass of the {@code PhysicalBody} in kilograms.
     *
     * @return The mass of the body
     */
    public final float getMass() {
        if (body != null)
            return body.m_mass;
        else return 0;
    }

    /**
     * Applies a 2-dimensional force to the {@code PhysicalBody}.
     *
     * @param force The force given in the SI unit
     */
    public final void applyForce(Point2D force) {
        if (body != null)
            body.applyForce(new Vec2(force.x, force.y), body.getWorldCenter());
    }

    /**
     * Applies a 2-dimensional force to the {@code PhysicalBody}.
     *
     * @param force  The force given in the SI unit
     * @param center The effective point of the force on the {@code PhysicalBody}
     */
    public final void applyForce(Point2D force, Point2D center) {
        if (body != null)
            body.applyForce(new Vec2(force.x, force.y), new Vec2(center.x, center.y));
    }

    /**
     * Applies a 2-dimensional impulse to the {@code PhysicalBody}.
     *
     * @param impulse The impulse to be applied in SI unit
     */
    public final void applyLinearImpulse(Point2D impulse) {
        if (body != null)
            body.applyLinearImpulse(new Vec2(impulse.x, impulse.y), body.getWorldCenter());
    }

    /**
     * Applies a 2-dimensional impulse to the {@code PhysicalBody}.
     *
     * @param impulse The impulse to be applied in SI unit
     * @param center  The effective point of the impulse on the {@code PhysicalBody}
     */
    public void applyLinearImpulse(Point2D impulse, Point2D center) {
        if (body != null)
            body.applyLinearImpulse(new Vec2(impulse.x, impulse.y), new Vec2(center.x, center.y));
    }

    /**
     * Adds a joint descriptor to this {@code PhysicalBody}. For joints to be effective,
     * they should be added to both bodies involved.
     *
     * @param joint The joint to be added
     */
    public final void add(JointDescriptor joint) {
        joints.add(joint);
    }

    /**
     * Sets the {@code Shape} of this body. If the body is already attached to a scene,
     * The shape will change will take effect on the next frame.
     *
     * @param s The new shape for the body
     * @see com.annahid.libs.artenus.physics.Shape
     */
    public final void setShape(Shape s) {
        shape = s;

        if (body != null)
            createFixture();
    }

    /**
     * Gets the simulation type of this {@code PhysicalBody}. This type can be one
     * of {@link Behavior#DYNAMIC}, {@link Behavior#STATIC}, or {@link Behavior#KINEMATIC}.
     *
     * @return The body type
     * @see Behavior
     */
    public final Behavior getType() {
        return desc.type;
    }

    /**
     * Sets the simulation type of the physical body. It can be one of the values
     * {@link Behavior#STATIC}, {@link Behavior#DYNAMIC}, or {@link Behavior#KINEMATIC}.
     *
     * @param type The desired simulation type
     * @see Behavior
     */
    public final void setType(Behavior type) {
        if (body != null) {
            switch (type) {
                case DYNAMIC:
                    body.m_type = BodyType.DYNAMIC;
                    break;
                case STATIC:
                    body.m_type = BodyType.STATIC;
                    break;
                default:
                    body.m_type = BodyType.KINEMATIC;
                    break;
            }
        } else desc.type = type;
    }

    /**
     * Sets the angular velocity of the body.
     *
     * @return The current angular velocity
     */
    public final float getAngularVelocity() {
        if (body != null)
            return body.m_angularVelocity;
        else return desc.angularVelocity;
    }

    /**
     * Sets the angular velocity of the body. If the object is active in simulation, its
     * current angular velocity will be replaced with the new value.
     *
     * @param velocity The new angular velocity
     */
    public final void setAngularVelocity(float velocity) {
        if (body != null)
            body.setAngularVelocity(velocity);
        else desc.angularVelocity = velocity;
    }

    /**
     * Gets the linear velocity of the body.
     *
     * @return The current linear velocity
     */
    public final Point2D getLinearVelocity() {
        if (body != null) {
            final Vec2 v = body.m_linearVelocity;
            return new Point2D(v.x, v.y);
        } else return desc.linearVelocity;
    }

    /**
     * Sets the current linear velocity of the object. If the object is active in simulation,
     * its current linear velocity is replaced with the new value.
     *
     * @param velocity The new linear velocity
     */
    public final void setLinearVelocity(Point2D velocity) {
        setLinearVelocity(velocity.x, velocity.y);
    }

    /**
     * Sets the current linear velocity of the object. If the object is active in simulation,
     * its current linear velocity is replaced with the new value.
     *
     * @param vx The x component of the new linear velocity
     * @param vy The y component of the new linear velocity
     */
    public final void setLinearVelocity(float vx, float vy) {
        if (body != null)
            body.setLinearVelocity(new Vec2(vx, vy));
        else {
            desc.linearVelocity.x = vx;
            desc.linearVelocity.y = vy;
        }
    }

    /**
     * Gets linear damping for the body.
     *
     * @return The body's linear damping
     */
    public float getLinearDamping() {
        if (body != null)
            return body.m_linearDamping;
        else return desc.linearDamping;
    }

    /**
     * Sets linear damping for the body.
     *
     * @param damping The new linear damping
     */
    public final void setLinearDamping(float damping) {
        if (body != null)
            body.setLinearDamping(damping);
        else desc.linearDamping = damping;
    }

    /**
     * Indicates whether the body is active in simulation and is considered in collision
     * detection.
     *
     * @return A value indicating whether the body is active
     */
    public final boolean isActive() {
        if (body != null)
            return body.isActive();
        else return desc.active;
    }

    /**
     * Modifies the active state of the body kin simulation. If the body is not active,
     * it is not accounted for in collision detection and physics simulation.
     *
     * @param active A value indicating whether the body should be active
     */
    public final void setActive(boolean active) {
        if (body != null)
            body.setActive(active);
        else desc.active = active;
    }

    /**
     * Gets angular damping for the body.
     *
     * @return The body's angular damping
     */
    public float getAngularDamping() {
        if (body != null)
            return body.m_angularDamping;
        else return desc.angularDamping;
    }

    /**
     * Sets angular for the body.
     *
     * @param damping The new angular damping
     */
    public final void setAngularDamping(float damping) {
        if (body != null)
            body.setAngularDamping(damping);
        else desc.angularDamping = damping;
    }

    /**
     * This method indicated whether the body is a bullet. A bullet is a fast-moving physical body.
     *
     * @return A value indicating whether the body is a bullet
     */
    public boolean isBullet() {
        if (body != null)
            return body.isBullet();
        else return desc.bullet;
    }

    /**
     * Sets whether the body is a bullet. A bullet is a fast-moving physical body. If you have an object
     * that has this characteristic in your game, you should set this flag for better collision processing.
     *
     * @param bullet A new value indicating whether the body is a bullet
     */
    public void setBullet(boolean bullet) {
        if (body != null)
            body.setBullet(bullet);
        else desc.bullet = bullet;
    }

    /**
     * Gets whether the rotational angle of the body is fixed in simulation.
     *
     * @return A value indicating whether the body's rotation angle should remain fixed
     */
    public boolean isFixedRotation() {
        if (body != null)
            return body.isFixedRotation();
        else return desc.fixedRotation;
    }

    /**
     * Sets whether the rotational angle of the body is fixed in simulation.
     *
     * @param flag A value indicating whether the body's rotation angle should remain fixed
     */
    public void setFixedRotation(boolean flag) {
        if (body != null)
            body.setFixedRotation(flag);
        else desc.fixedRotation = flag;
    }

    /**
     * Gets the currently assigned density of the body.
     *
     * @return Density of the body
     */
    public float getDensity() {
        return density;
    }

    /**
     * Sets the density of the body. Density is accounted for whenever you assign a shape to the body.
     * Changing this value has no effect if the parent game object is already added to the scene. But
     * if you change the shape, the value is applied to the new shape.
     *
     * @param value The new value for density
     */
    public void setDensity(float value) {
        density = value;
    }


    /**
     * Gets the currently assigned friction of the body.
     *
     * @return Friction of the body
     */
    public float getFriction() {
        return friction;
    }

    /**
     * Sets the friction of the body. Friction is accounted for whenever you assign a shape to the body.
     * Changing this value has no effect if the parent game object is already added to the scene. But
     * if you change the shape, the value is applied to the new shape.
     *
     * @param value The new value for friction
     */
    public void setFriction(float value) {
        friction = value;
    }

    /**
     * Gets the currently assigned restitution of the body. Restitution determines how much energy the
     * object loses when bouncing.
     *
     * @return Restitution of the body
     */
    public float getRestitution() {
        return restitution;
    }

    /**
     * Sets the restitution of the body. Friction is accounted for whenever you assign a shape to
     * the body. Changing this value has no effect if the parent game object is already added to the
     * scene. But if you change the shape, the value is applied to the new shape.
     *
     * @param value The new value for restitution. Setting this value to 1 ensures that the object
     *              will not lose any energy when bouncing.
     */
    public void setRestitution(float value) {
        restitution = value;
    }

    /**
     * Gets the current position of the body. This position changes with simulation. If the parent game object
     * is added to the scene, this method runs slower than it would when isolated.
     *
     * @return The current position of the body in pixels. This value is a copy of the original metric value
     * and cannot be used to modify the position. Use {@link #setPosition(float, float)} to modify position.
     */
    @Override
    public final Point2D getPosition() {
        if (body != null) {
            final Vec2 pos = body.getPosition();
            return new Point2D(pos.x * PhysicsSimulator.pixelsPerMeter, pos.y * PhysicsSimulator.pixelsPerMeter);
        } else return desc.position;
    }

    /**
     * Modifies the position of the body. The values are given in pixels, but internally stored in metric format.
     *
     * @param x The x component of the new position in pixels
     * @param y The y component of the new position in pixels
     */
    @Override
    public void setPosition(float x, float y) {
        if (body != null)
            body.setTransform(new Vec2(x / PhysicsSimulator.pixelsPerMeter, y / PhysicsSimulator.pixelsPerMeter), body.getAngle());
        else {
            desc.position.x = x;
            desc.position.y = y;
        }

        if ((connections & POSITION) != 0)
            ((Transformable) target).setPosition(x, y);
    }

    /**
     * Gets the rotational angle of the body.
     *
     * @return The current rotational angle of the body in degrees
     */
    @Override
    public final float getRotation() {
        if (body != null)
            return body.getAngle() * 57.29579143f;
        else return desc.angle * 57.29579143f;
    }

    /**
     * Sets the rotation angle of the body. This value is internally stored in Radians, but the method
     * takes it in degrees to be compatible with game object attachments.
     *
     * @param rotation The new rotational angle of the body in degrees
     */
    @Override
    public final void setRotation(float rotation) {
        if (body != null)
            body.setTransform(body.getPosition(), rotation * 0.0174532889f);
        else desc.angle = rotation * 0.0174532889f;

        if ((connections & ROTATION) != 0)
            ((Transformable) target).setRotation(rotation);
    }

    @Override
    public void onAttach(Scene scene) {
        super.onAttach(scene);
        target.onAttach(scene);
        scene.getPhysicsSimulator().attach(this);
    }

    @Override
    public void onDetach(Scene scene) {
        super.onDetach(scene);
        target.onDetach(scene);
        scene.getPhysicsSimulator().detach(this);
    }

    /**
     * Updates this physical body to its new state provided by the physics simulator. The underlying
     * entity may also be modified, depending on selected connections.
     *
     * @param elapsedTime Elapsed time in seconds since the last frame
     * @see com.annahid.libs.artenus.physics.PhysicalBody#setConnections(int)
     */
    @Override
    public void advance(float elapsedTime) {
        super.advance(elapsedTime);
        final Vec2 pos = body.getPosition();
        final Transformable trans = ((Transformable) target);

        if ((connections & POSITION) != 0) {
            trans.setPosition(
                    pos.x * PhysicsSimulator.pixelsPerMeter,
                    pos.y * PhysicsSimulator.pixelsPerMeter);
        }

        if ((connections & ROTATION) != 0)
            trans.setRotation(getRotation());

    }

    /**
     * Sets connections for this physical body. Valid values are {@link #POSITION} and
     * {@link #ROTATION}.
     *
     * @param conn the bit-masked list of connections
     */
    public void setConnections(int conn) {
        connections = conn;
    }

    /**
     * Gets the current connections associated with this physical body.
     *
     * @return the bit-masked list of connections
     */
    public int getConnections() {
        return connections;
    }

    final void createFixture() {
        final FixtureDef def = new FixtureDef();
        def.density = density;
        def.friction = friction;
        def.restitution = restitution;
        def.shape = (org.jbox2d.collision.shapes.Shape) shape.createInternal();
        body.createFixture(def);
    }

    Body body;
    Descriptor desc;
    List<JointDescriptor> joints = new ArrayList<>();

    private Shape shape;
    private float density, friction, restitution;
    private int connections = POSITION | ROTATION;
}
