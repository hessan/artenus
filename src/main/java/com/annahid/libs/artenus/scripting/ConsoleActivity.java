package com.annahid.libs.artenus.scripting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

import com.annahid.libs.artenus.R;

/**
 * This class provides a scripting console interface. You can type in commands
 * and request execution. The request will be passed to the {@code ScriptHost}
 * object for processing. To use this interface, you must declare
 * {@code ConsoleActivity} as an activity in {@code AndroidManifest.xml}.
 * author Hessan Feghhi
 *
 */
public class ConsoleActivity extends Activity {
	private static ConsoleActivity instance = null;
	private static ScriptHost host;
	private static String code = "";

	/**
	 * Opens a console window with the given script host.
	 *
	 * @param context The application context
	 * @param host    The script host interpreting scripts
	 * @see ScriptHost
	 */
	public static void openConsole(Context context, ScriptHost host) {
		if (instance == null)
			closeConsole();

		final Intent consoleIntent = new Intent(context, ConsoleActivity.class);
		ConsoleActivity.host = host;
		context.startActivity(consoleIntent);
	}

	/**
	 * Sets the script showing on the editor.
	 *
	 * @param script The script code
	 */
	public static void setScript(String script) {
		code = script;
	}

	/**
	 * Retrieves the scripts currently showing on the editor.
	 *
	 * @return The script text
	 */
	public static String getScript() {
		return code;
	}

	/**
	 * Closes the console window.
	 */
	public static void closeConsole() {
		if (instance != null) {
			instance.finish();
			instance = null;
		}
	}

	private EditText txtCommand;
	private Button btnExecute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.console);
		txtCommand = (EditText) findViewById(R.id.txtCommand);
		btnExecute = (Button) findViewById(R.id.btnExecute);
		btnExecute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == btnExecute) {
					final String text = txtCommand.getText().toString();

					if (text.length() > 0)
						host.execute(text);
				}
			}
		});
		txtCommand.setText(code);
		instance = this;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		code = txtCommand.getText().toString();
		host.onExit();
	}
}
