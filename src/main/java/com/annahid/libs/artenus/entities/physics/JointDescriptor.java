package com.annahid.libs.artenus.entities.physics;

import org.jbox2d.dynamics.joints.Joint;

/**
 * The base class for all classes that describe joints in physical simulation.
 * Joints are constraints that relate two bodies and limit their freedom based
 * on their mutual relation and on the nature of the joint.
 *
 * @author Hessan Feghhi
 *
 */
public abstract class JointDescriptor {
	/**
	 * Type identifier for a revolute joint.
	 *
	 * @see RevoluteJointDescriptor
	 */
	public static final int JOINT_REVOLUTE = 0;

	/**
	 * Type of the joint. The only valid value is {@link JointDescriptor#JOINT_REVOLUTE} at the
	 * moment.
	 */
	int jointType;

	PhysicalBody body1, body2;
	Joint jointObject = null;

	/**
	 * Constructs a joint with a given type. This constructor is called from
	 * all subclasses to identify the type identifier of the joint.
	 *
	 * @param type type of the joint, such as {@link JointDescriptor#JOINT_REVOLUTE}
	 */
	protected JointDescriptor(int type) {
		jointType = type;
	}
}
