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

import org.jbox2d.dynamics.joints.Joint;

/**
 * The base class for all classes that describe joints in physical simulation. Joints are
 * constraints that relate two bodies and limit their freedom based on their mutual relation and on
 * the nature of the joint.
 *
 * @author Hessan Feghhi
 */
public abstract class JointDescriptor {
    /**
     * Holds joint type. Each joint defines its own type value, which is stored as the constant
     * {@code TYPE} within the corresponding class.
     */
    int jointType;

    /**
     * Holds the physical body at the first end of the joint.
     */
    PhysicalBody body1;

    /**
     * Holds the physical body at the second end of the joint.
     */
    PhysicalBody body2;

    /**
     * Holds the JBox2D joint object associated with this joint.
     */
    Joint jointObject = null;

    /**
     * Creates a joint with a given type. This constructor is called from all subclasses to
     * identify the type identifier of the joint.
     *
     * @param type type of the joint. Each joint defines its own type value, which is stored as the
     *             constant {@code TYPE} within the corresponding class.
     */
    protected JointDescriptor(int type) {
        jointType = type;
    }
}
