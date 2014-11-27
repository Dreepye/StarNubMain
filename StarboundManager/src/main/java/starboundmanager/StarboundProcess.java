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

package starboundmanager;

import utilities.events.types.StringEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Represents StarboundProcess this will build and start a process as well as a Runnable to manage the stream.
 * It will submit events or print to screen or non depending on the settings when constructed
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarboundProcess implements Runnable {

    protected final Process PROCESS;
    private final StarboundManagement STARBOUND_MANAGEMENT;

    protected boolean STREAM_EVENT_MESSAGE;
    protected boolean STREAM_CONSOLE_PRINT;

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will build a new Starbound Process
     *
     * @param STARBOUND_MANAGEMENT StarboundManagement representing the reference so that we can use the event router
     * @param STREAM_EVENT_MESSAGE boolean representing if we print event messages to the event handler from the Starbound_Server.exe output stream
     * @param STREAM_CONSOLE_PRINT boolean representing if we should print console messages from the Starbound_Server.exe output stream
     * @throws IOException an exception if we cannot build the process
     */
    StarboundProcess(StarboundManagement STARBOUND_MANAGEMENT, boolean STREAM_EVENT_MESSAGE, boolean STREAM_CONSOLE_PRINT) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(STARBOUND_MANAGEMENT.getFilePath());
        processBuilder.redirectErrorStream(true);
        this.STREAM_EVENT_MESSAGE = STREAM_EVENT_MESSAGE && STARBOUND_MANAGEMENT.EVENT_ROUTER !=null;
        this.STREAM_CONSOLE_PRINT = STREAM_CONSOLE_PRINT;
        this.STARBOUND_MANAGEMENT = STARBOUND_MANAGEMENT;
        this.PROCESS = processBuilder.start();
        new Thread(this, "StarNub - Starbound - Standard Out_Standard Error Stream").start();
    }

    protected Process getProcess() {
        return PROCESS;
    }

    public boolean isSTREAM_EVENT_MESSAGE() {
        return STREAM_EVENT_MESSAGE;
    }

    public void setSTREAM_EVENT_MESSAGE(boolean STREAM_EVENT_MESSAGE) {
        this.STREAM_EVENT_MESSAGE = STREAM_EVENT_MESSAGE;
    }

    public boolean isSTREAM_CONSOLE_PRINT() {
        return STREAM_CONSOLE_PRINT;
    }

    public void setSTREAM_CONSOLE_PRINT(boolean STREAM_CONSOLE_PRINT) {
        this.STREAM_CONSOLE_PRINT = STREAM_CONSOLE_PRINT;
    }

    /**
     * Depending on the internal settings the stream will be discarded or used
     * to print console messages and provide basic player management functions.
     */
    @SuppressWarnings("unchecked")
    public void run() {
        InputStreamReader inputStreamReader = new InputStreamReader(PROCESS.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                /* For debugging */
                if (STREAM_EVENT_MESSAGE) {
                    STARBOUND_MANAGEMENT.EVENT_ROUTER.eventNotify(new StringEvent("Starbound_Output_Stream", line));
                }
                if (STREAM_CONSOLE_PRINT) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            STARBOUND_MANAGEMENT.printOrEvent("StarNub_Log_Error", "Error printing Starbound Input Stream.");
        } finally {
            try {
                inputStreamReader.close();
                bufferedReader.close();
            } catch (IOException e) {
                STARBOUND_MANAGEMENT.printOrEvent("StarNub_Log_Error", "Error closing Starbound Input Stream.");
                e.printStackTrace();
            }
        }
    }
}
