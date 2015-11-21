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

/**
 * An implementation of {@link com.annahid.libs.artenus.physics.JointDescriptor}
 * that describes a revolute joint. Revolute joints provide single-axis rotation function used in
 * many places such as door hinges, folding mechanisms, and other uni-axial rotation devices.
 *
 * @author Hessan Feghhi
 */
public class RevoluteJointDescriptor extends JointDescriptor {
    /**
     * Type identifier for a revolute joint.
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
