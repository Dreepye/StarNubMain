/*
 * Copyright (C) 2014 www.StarNub.org - Underbalanced
 *
 * This file is part of org.starnub a Java Wrapper for Starbound.
 *
 * This above mentioned StarNub software is free software:
 * you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free
 * Software Foundation, either version  3 of the License, or
 * any later version. This above mentioned CodeHome software
 * is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU General Public License for more details. You should
 * have received a copy of the GNU General Public License in
 * this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.starnub.starnubserver.pluggable;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.starnub.utilities.concurrent.thread.ThreadSleep;

import java.io.File;

public class PluggablePythonInterpreter {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final PluggablePythonInterpreter instance = new PluggablePythonInterpreter();

    private static PythonInterpreter interpreter;
    private static String currentFile = "";
    private static boolean inUse;

    /**
     * This constructor is private - Singleton Pattern
     */
    private PluggablePythonInterpreter() {
        interpreter = new PythonInterpreter();
    }

    /**
     * This returns this Singleton - Singleton Pattern
     */
    public static PluggablePythonInterpreter getInstance() {
        return instance;
    }

    public PythonInterpreter getInterpreter() {
        return interpreter;
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse() {
        PluggablePythonInterpreter.inUse = true;
    }

    public void setNotInUse() {
        PluggablePythonInterpreter.inUse = false;
    }

    public void loadPythonScript(String file){
        if (!currentFile.equals(file)) {
            while (isInUse()) {
                ThreadSleep.timerSeconds(1);
            }
            inUse = true;
            interpreter.execfile(file);
        }
    }

    public void loadPythonScript(File file){
        String filePath = file.getAbsolutePath();
        loadPythonScript(filePath);
    }

    public PyObject getPyObject(String objectName, boolean keepUsing){
        PyObject b = interpreter.getLocals().__finditem__(objectName);
        if(!keepUsing){
            inUse = false;
        }
        return b;
    }
}


