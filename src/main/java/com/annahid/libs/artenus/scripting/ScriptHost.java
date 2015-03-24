package com.annahid.libs.artenus.scripting;

/**
 * The interface for classes that interpret and execute scripts. If your
 * game supports scripting, you can implement this interface to take
 * advantage of the script console user interface. 
 * @author Hessan Feghhi
 * @see ConsoleActivity
 */
public interface ScriptHost {
	/**
	 * Executes a script code
	 *
	 * @param command The command or piece of code
	 * @return The result or {@code null} if there is not result
	 */
	Object execute(String command);

	/**
	 * This method is called when the script console is closing. It
	 * should free all resources allocated to the script host.
	 */
	void onExit();
}
