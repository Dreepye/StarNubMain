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

package server.server.starbound;

import server.StarNub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StarboundProcess implements Runnable {

    protected Process process;

    public StarboundProcess(String filePath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(filePath);
        processBuilder.redirectErrorStream(true);
        process = processBuilder.start();
        new Thread(this, "StarNub - Starbound - Standard Out_Error Stream").start();
    }

    public Process getProcess() {
        return process;
    }

    /**
     * Depending on the internal settings the stream will be discarded or used
     * to print console messages and provide basic player management functions.
     */
    public void run() {
        boolean printMessage = !(boolean) StarNub.getConfiguration().getNestedValue("packet_decode", "starnub settings");
        InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                /* For debugging */
                if (printMessage) {
//					System.out.println(line);
                    //TODO Management System - Player List, Banning, Kicking
                }
            }
        } catch (IOException e) {
            StarNub.getLogger().cErrPrint("StarNub", "Error printing Starbound Input Stream.");
        } finally {
            try {
                inputStreamReader.close();
                bufferedReader.close();
            } catch (IOException e) {
                StarNub.getLogger().cErrPrint("StarNub", "Error closing Starbound Input Stream.");
                e.printStackTrace();
            }

        }
    }
}
