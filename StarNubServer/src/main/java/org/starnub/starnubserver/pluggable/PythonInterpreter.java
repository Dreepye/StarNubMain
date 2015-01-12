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
import org.starnub.utilities.concurrent.thread.ThreadSleep;

import java.io.File;

public class PythonInterpreter {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final PythonInterpreter instance = new PythonInterpreter();

    private static org.python.util.PythonInterpreter interpreter = new org.python.util.PythonInterpreter();
    private static String currentFile;
    private static boolean inUse;

    /**
     * This constructor is private - Singleton Pattern
     */
    private PythonInterpreter() {}

    /**
     * This returns this Singleton - Singleton Pattern
     */
    public static PythonInterpreter getInstance() {
        return instance;
    }

    public org.python.util.PythonInterpreter getInterpreter() {
        return interpreter;
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse() {
        PythonInterpreter.inUse = true;
    }

    public void setNotInUse() {
        PythonInterpreter.inUse = false;
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
        PyObject pyObject = interpreter.eval(objectName);
        if(!keepUsing){
            inUse = false;
        }
        return pyObject;
    }
}


