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
 * Provides a scripting console interface. You can type in commands and request execution. The
 * request will be passed to the {@code ScriptHost} object for processing. To use this interface,
 * you must declare {@code ConsoleActivity} as an activity in {@code AndroidManifest.xml}.
 *
 * @author Hessan Feghhi
 */
public class ConsoleActivity extends Activity {
    private static ConsoleActivity instance = null;

    /**
     * Holds the script host used to run scripts on this console.
     */
    private static ScriptHost host;

    /**
     * Holds the current code.
     */
    private static String code = "";

    /**
     * Holds the text box control that contains the code.
     */
    private EditText txtCommand;

    /**
     * Holds the execute button.
     */
    private Button btnExecute;

    /**
     * Opens a console window with the given script host.
     *
     * @param context Application context
     * @param host    The script host interpreting scripts
     *
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
     * Gets the script currently showing on the editor.
     *
     * @return The script text
     */
    public static String getScript() {
        return code;
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
     * Closes the console window.
     */
    public static void closeConsole() {
        if (instance != null) {
            instance.finish();
            instance = null;
        }
    }

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
