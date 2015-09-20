package com.annahid.libs.artenus.physics;

import com.annahid.libs.artenus.data.Point2D;

/**
 * An implementation of {@link com.annahid.libs.artenus.physics.JointDescriptor}
 * that describes a revolute joint. Revolute joints provide single-axis rotation function used in
 * many places such as door hinges, folding mechanisms, and other uni-axial rotation devices.
 * @author Hessan Feghhi
 *
 */
public class RevoluteJointDescriptor extends JointDescriptor {	/**
	/* Type identifier for a revolute joint.
	 *
	 * @see RevoluteJointDescriptor
	 */
	public static final int TYPE = 0;

	/**
	 * Constructs a {@code RevoluteJointDescriptor} between two bodies at the given
	 * anchor point. The bodies will be "pinned" together at that point.
	 *
	 * @param b1     The first body involved
	 * @param b2     The second body involved
	 * @param anchor The anchor point
	 */
	public RevoluteJointDescriptor(PhysicalBody b1, PhysicalBody b2, Point2D anchor) {
		super(TYPE);
		body1 = b1;
		body2 = b2;
		anchorPoint = anchor;
	}

	Point2D anchorPoint;
}
