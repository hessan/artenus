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

package com.annahid.libs.artenus.data;

/**
 * <p>Represents an integer value which cannot be discovered or altered using game hacking software.
 * It is slower than int but more secure. It is recommended to use this class instead of the
 * primitive type int for sensitive information.</p>
 * <p>Android applications are programmed in java, which inherently enables malicious users with
 * reflection-based attacks. The variables used in the application are stored in memory and can be
 * easily read using some tools. What game "hackers" do is to use software to find variables with a
 * specific value among all currently used variables in the application, and modify the values they
 * want to their benefit. As an example, if a hacker wants to have a million coins in your game and
 * they currently have 1200, they look for variables that currently have the value of 1200. If they
 * are lucky, there will be only one variable. If not, they can earn or lose a few coins and track
 * changes in the variables. Finally they find the exact variable that is responsible for the coins
 * and they simply set it to one million in runtime.</p>
 * <p>The attack described here is a prominent attack and is used by many. The software used for
 * this attack is widely available and does not require any special programming knowledge to
 * operate. For this reason you have to be careful how you keep your sensitive information in the
 * application. Encryption is a good idea for storage. For runtime attacks, this class provides a
 * secure way to store your variables such as scores and coins. The values queried with hacking
 * tools do not reveal the real values stored in this class. This class stores a randomly modified
 * version of the original value to keep it undetected by such software.</p>
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("unused")
public class RogueInt extends Number {
    /**
     * Holds the modified value.
     */
    private int value;

    /**
     * Holds the seed used to modify the value (or to get it back).
     */
    private int seed;

    /**
     * Creates a {@code RougeInt} with the given initial value.
     *
     * @param initialValue The initial value to be stored
     */
    public RogueInt(int initialValue) {
        seed = (int) (100 * Math.random()) + 10;
        set(initialValue);
    }

    /**
     * Stores a new value in this {@code RougeInt} instance.
     *
     * @param intValue The new value to be stored
     */
    public void set(int intValue) {
        value = intValue * (seed > 60 ? 3 : 2) + seed;
    }

    /**
     * Returns the value represented by this {@code RogueInt} instance.
     *
     * @return Stored value
     */
    @Override
    public int intValue() {
        return (value - seed) / (seed > 60 ? 3 : 2);
    }

    /**
     * Returns the double representation of the value represented by this {@code RogueInt} instance.
     *
     * @return Stored value converted to double
     */
    @Override
    public double doubleValue() {
        return (double) intValue();
    }

    /**
     * Returns the float representation of the value represented by this {@code RogueInt} instance.
     *
     * @return Stored value converted to float
     */
    @Override
    public float floatValue() {
        return (float) intValue();
    }

    /**
     * Returns the long representation of the value represented by this {@code RogueInt} instance.
     *
     * @return Stored value converted to long
     */
    @Override
    public long longValue() {
        return intValue();
    }

    /**
     * Returns the string representation of the value represented by this {@code RougeInt} instance.
     *
     * @return String representation of the {@code int} value this {@code RogueInt} represents
     */
    @Override
    public String toString() {
        return String.valueOf(intValue());
    }
}
