package com.annahid.libs.artenus.data;

/**
 * <p>This class represents an integer value which cannot be discovered or altered
 * using game hacking software. It is slower than int but more secure. It is recommended
 * to use this class instead of the primitive type int in your sensitive information.</p>
 * <p>Android applications are programmed in java, which inherently enables
 * malicious users with reflection-based attacks. The variables used in the application
 * are stored in memory and can be easily read using some tools. What game "hackers" do
 * is to use software to find variables with a specific value among all currently used
 * variables in the application, and modify the values they want to their benefit. As an
 * example, if a hacker wants to have a million coins in your game and they currently have
 * 1200, they look for variables that currently have the value of 1200. If they are lucky,
 * there will be only one variable. If not, they can earn or lose a few coins and track
 * changes in the variables. Finally they find the exact variable that is responsible
 * for the coins and they simply set it to one million in runtime.</p>
 * <p>The attack described here is a prominent attack and is used by many. The software
 * used for this attack is widely available and does not require any special programming
 * knowledge to operate. For this reason you have to be careful how you keep your sensitive
 * information in the application. Encryption is a good idea for storage. For runtime
 * attacks, this class provides a secure way to store your variables such as scores and
 * coins. The values queries with hacking tools do not reveal the real values stored in
 * this class. This class stores a randomly modified version of the original value to
 * keep it undetected by this software.</p>
 * 
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("UnusedDeclaration")
public class RogueInt {
	private int value, seed;

	/**
	 * Constructs a {@code RougeInt} with the given initial value.
	 *
	 * @param initialValue The initial value to be stored
	 */
	public RogueInt(int initialValue) {
		seed = (int) (100 * Math.random()) + 10;
		setValue(initialValue);
	}

	/**
	 * Stores a new value in this {@code RougeInt} instance.
	 *
	 * @param newValue The new value to be stored
	 */
	public void setValue(int newValue) {
		value = newValue * (seed > 60 ? 3 : 2) + seed;
	}

	/**
	 * Gets the value represented by this {@code RougeInt} instance.
	 *
	 * @return The stored value
	 */
	public int getValue() {
		return (value - seed) / (seed > 60 ? 3 : 2);
	}

	/**
	 * Returns the string representation of the value represented by this {@code RougeInt} instance.
	 * @return The string representation of the {@code int} value this {@code RogueInt} represents
	 */
	@Override
	public String toString() {
		return String.valueOf(getValue());
	}
}
