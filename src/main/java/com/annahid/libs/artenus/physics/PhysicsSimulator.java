/*
 *  This file is part of the Artenus 2D Framework.
 *  Copyright (C) 2015  Hessan Feghhi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Foobar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.annahid.libs.artenus.physics;

import com.annahid.libs.artenus.data.Point2D;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs physical simulation in the framework. It handles everything related to
 * physical bodies, joints and physical animation. It is highly recommended that you do not directly
 * instantiate this class. Each {@link com.annahid.libs.artenus.core.Scene} internally contains a
 * {@code PhysicsSimulator} instance and it is recommended that you take advantage of that through
 * entities such as {@link PhysicalBody}.
 *
 * @author Hessan Feghhi
 * @see com.annahid.libs.artenus.physics.PhysicalBody
 */
@SuppressWarnings("UnusedDeclaration")
public final class PhysicsSimulator {
    /**
     * Pixels per meter. This framework is based on pixel measures. However,
     * everything other than distances are calculated by their SI units in physics
     * simulations. To conform distances to the SI unit, this value indicates
     * how many pixels define a meter. Note that setting this value to zero causes
     * division by zero and results in application crash.
     */
    public static float pixelsPerMeter = 60;

    /**
     * Use this field to scale the physical time. The value of this parameter is
     * originally 1, which means that the time in the physical world is as fast
     * as the time in scene animations. Setting this value enables you to speed
     * up or slow down physics.
     */
    public float timeScale = 1;

    private World world;

    private List<CollisionInfo> collisions = new ArrayList<>();

    private CollisionListener collisionListener = null;

    private boolean paused = false;

    /**
     * Constructs a {@code PhysicsSimulator}.
     */
    public PhysicsSimulator() {
        world = new World(new Vec2(0, 0));

        world.setContactListener(new ContactListener() {
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
                final Body body1 = contact.m_fixtureA.m_body;
                final Body body2 = contact.m_fixtureB.m_body;
                final CollisionInfo info = new CollisionInfo();
                info.body1 = (PhysicalBody) body1.m_userData;
                info.body2 = (PhysicalBody) body2.m_userData;
                info.impulseCount = impulse.count;
                info.normalImpulses = impulse.normalImpulses;
                info.tangentImpulses = impulse.tangentImpulses;
                collisions.add(info);
            }

            @Override
            public void beginContact(Contact arg0) {
            }

            @Override
            public void endContact(Contact arg0) {
            }

            @Override
            public void preSolve(Contact arg0, Manifold arg1) {
            }
        });
    }

    /**
     * Sets the 2-dimensional gravity of the physical world.
     * (meters per seconds squared).
     *
     * @return The gravity of the world
     */
    public Point2D getGravity() {
        final Vec2 gravity = world.getGravity();
        return new Point2D(gravity.x, gravity.y);
    }

    /**
     * Sets the 2-dimensional gravity of the physical world.
     *
     * @param xFactor The x factor of gravity
     * @param yFactor The y factor of gravity
     */
    public void setGravity(float xFactor, float yFactor) {
        world.setGravity(new Vec2(xFactor, yFactor));
    }

    /**
     * Gets the collision listener currently appointed for handling collisions.
     *
     * @return The collision listener or {@code null} if none assigned
     *
     * @see CollisionListener
     */
    public CollisionListener getCollisionListener() {
        return collisionListener;
    }

    /**
     * Appoints a collision listener to this physics simulator to handle collisions.
     *
     * @param listener The collision listener. Setting this value to {@code null}
     *                 removes the listener
     *
     * @see CollisionListener
     */
    public void setCollisionListener(CollisionListener listener) {
        collisionListener = listener;
    }

    /**
     * Advances the world by the given time. This method is called internally by the
     * scene and you do not need to directly invoke it.
     *
     * @param elapsedTime        The time passed since last frame. This value will be scaled
     *                           using {@link #timeScale}
     * @param velocityIterations The number of velocity points to interpolate between
     * @param positionIterations The number of position points to interpolate between
     */
    public void step(float elapsedTime, int velocityIterations, int positionIterations) {
        if (!paused)
            world.step(elapsedTime * timeScale, velocityIterations, positionIterations);
    }

    /**
     * Signals the simulator that it is a good time to handle collisions. Normally
     * all collisions are queued in the simulator when they happen. This is to avoid
     * concurrent modification of sensitive variables. This method is then called when
     * it is safe, and it handles all collisions in the queue until the queue is empty.
     * You do not need to call this method manually if you are using physics simulation
     * through a scene.
     */
    public void handleCollisions() {
        if (collisionListener != null)
            for (CollisionInfo info : collisions)
                collisionListener.onCollision(info);
        collisions.clear();
    }

    /**
     * Gets whether the simulator is paused.
     *
     * @return {@code true} if paused or {@code false} otherwise
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Changes the paused state of the simulator. Physical bodies in a paused simulator
     * remain halted until the simulator is resumed.
     *
     * @param p {@code true} to pause the simulator or {@code false} to un-pause it
     */
    public void setPaused(boolean p) {
        paused = p;
    }

    /**
     * Attaches a physical body to the simulator. You do not need to call this method
     * directly if you are using physics simulation through a scene.
     *
     * @param body The new physical body
     *
     * @see com.annahid.libs.artenus.physics.PhysicalBody
     */
    public void attach(PhysicalBody body) {
        if (body.body == null) {
            final PhysicalBody.Descriptor bodyDesc = body.desc;
            final BodyDef def = new BodyDef();
            def.allowSleep = true;
            def.angularDamping = bodyDesc.angularDamping;
            def.angularVelocity = bodyDesc.angularVelocity;
            def.bullet = bodyDesc.bullet;
            def.fixedRotation = bodyDesc.fixedRotation;
            def.linearDamping = bodyDesc.linearDamping;

            switch (bodyDesc.type) {
                case DYNAMIC:
                    def.type = BodyType.DYNAMIC;
                    break;
                case STATIC:
                    def.type = BodyType.STATIC;
                    break;
                default:
                    def.type = BodyType.KINEMATIC;
                    break;
            }

            body.body = world.createBody(def);
            body.body.m_userData = body;
            body.createFixture();
            body.setPosition(bodyDesc.position.x, bodyDesc.position.y);
            body.setRotation(bodyDesc.angle);
            body.body.setLinearVelocity(new Vec2(bodyDesc.linearVelocity.x, bodyDesc.linearVelocity.y));
            body.body.setActive(def.active);
            body.desc = null;

            for (JointDescriptor joint : body.joints) {
                if (joint.jointObject == null && joint.body1.body != null && joint.body2.body != null) {
                    switch (joint.jointType) {
                        case RevoluteJointDescriptor.TYPE:
                            final RevoluteJointDef jointDef = new RevoluteJointDef();
                            final Point2D anchor = ((RevoluteJointDescriptor) joint).anchorPoint;
                            jointDef.initialize(joint.body1.body, joint.body2.body, new Vec2(anchor.x / PhysicsSimulator.pixelsPerMeter, anchor.y / PhysicsSimulator.pixelsPerMeter));
                            joint.jointObject = world.createJoint(jointDef);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * Detaches a physical body from the simulator. You do not need to call this method
     * directly if you are using physics simulation through the scene. Once a body is
     * detached from the simulator, it must be disposed. Physical objects removed from
     * the scene are NOT reusable.
     *
     * @param body The physical body to remove
     *
     * @see com.annahid.libs.artenus.physics.PhysicalBody
     */
    public void detach(PhysicalBody body) {
        if (body.body != null) {
            body.body.m_userData = null;

            for (JointDescriptor joint : body.joints)
                if (joint.jointObject != null) {
                    world.destroyJoint(joint.jointObject);
                    joint.jointObject = null;
                }

            world.destroyBody(body.body);
        }
    }
}
