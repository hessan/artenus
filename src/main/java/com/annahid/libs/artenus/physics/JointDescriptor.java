package com.annahid.libs.artenus.physics;

import org.jbox2d.dynamics.joints.Joint;

/**
 * The base class for all classes that describe joints in physical simulation. Joints are
 * constraints that relate two bodies and limit their freedom based on their mutual relation and on
 * the nature of the joint.
 *
 * @author Hessan Feghhi
 *
 */
public abstract class JointDescriptor {
	/**
	 * Constructs a joint with a given type. This constructor is called from all subclasses to
	 * identify the type identifier of the joint.
	 *
	 * @param type type of the joint. Each joint defines its own type value, which is stored as the
	 *             constant {@code TYPE} within the corresponding class.
	 */
	protected JointDescriptor(int type) {
		jointType = type;
	}

	/**
	 * Type of the joint. Each joint defines its own type value, which is stored as the constant
	 * {@code TYPE} within the corresponding class.
	 */
	int jointType;

	PhysicalBody body1, body2;
	Joint jointObject = null;
}
