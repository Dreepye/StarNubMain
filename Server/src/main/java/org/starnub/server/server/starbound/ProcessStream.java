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

package org.starnub.server.server.starbound;

import org.starnub.server.StarNub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class must be in its own thread. It will process
 * the Starbound output stream. We discard it. We do not
 * want console spam.
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
class ProcessStream implements Runnable {

    /**
     * This runnable just discards the Starbound Process Stream, since we have
     * created our own eventsrouter we do not need to see all the debug messages.
     */
    public synchronized void run() {
        BufferedReader input = new BufferedReader(new InputStreamReader(
                ProcessManagement.getSbProcess().getInputStream()));
        String line;
        try {
            while ((line = input.readLine()) != null) {
                    /* For debugging */
//					System.out.println(line);
            }
        } catch (IOException e) {
            StarNub.getLogger().cErrPrint("StarNub", "Error printing Starbound Input Stream.");
        }
    }
}
