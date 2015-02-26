package com.annahid.libs.artenus.entities.physics;

import com.annahid.libs.artenus.data.Point2D;

/**
 * This class is an implementation of {@link com.annahid.libs.artenus.entities.physics.JointDescriptor}
 * that describes a revolute joint. Revolute joints provide single-axis rotation function used in
 * many places such as door hinges, folding mechanisms, and other uni-axial rotation devices.
 * @author Hessan Feghhi
 *
 */
public class RevoluteJointDescriptor extends JointDescriptor {
	/**
	 * Constructs a {@code RevoluteJointDescriptor} between two bodies at the given
	 * anchor point. The bodies will be "pinned" together at that point.
	 *
	 * @param b1     The first body involved
	 * @param b2     The second body involved
	 * @param anchor The anchor point
	 */
	public RevoluteJointDescriptor(PhysicalBody b1, PhysicalBody b2, Point2D anchor) {
		super(JOINT_REVOLUTE);
		body1 = b1;
		body2 = b2;
		anchorPoint = anchor;
	}

	Point2D anchorPoint;
}
