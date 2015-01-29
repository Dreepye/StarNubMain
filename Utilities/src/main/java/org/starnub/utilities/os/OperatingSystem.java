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

package org.starnub.utilities.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Represents a OperatingSystem this will represent some OS
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class OperatingSystem {

    protected final OperatingSystemType OPERATING_SYSTEM;
    protected int BIT_VERSION;

    public OperatingSystem() {
        String systemOS = System.getProperty("os.name");
        ProcessBuilder processBuilder;
        if (systemOS.startsWith("Windows")){
            this.OPERATING_SYSTEM = OperatingSystemType.WINDOWS;
            processBuilder = getProcessBuilder("cmd.exe", "/c", "wmic OS get OSArchitecture");
        } else {
             this.OPERATING_SYSTEM = OperatingSystemType.LINUX;
            processBuilder = getProcessBuilder("uname -m");
        }
        BIT_VERSION = setBitVersion(processBuilder);
    }

    public OperatingSystemType getOPERATING_SYSTEM() {
        return OPERATING_SYSTEM;
    }

    public int getBIT_VERSION() {
        return BIT_VERSION;
    }

    private ProcessBuilder getProcessBuilder(String command){
        return new ProcessBuilder(command);
    }

    private ProcessBuilder getProcessBuilder(String... commands){
        return new ProcessBuilder(commands);
    }

    private static int setBitVersion(ProcessBuilder pb) {
        InputStreamReader isr;
        try {
            pb.redirectErrorStream(true);
            Process p = pb.start();
            InputStream is = p.getInputStream();
            isr = new InputStreamReader(is);
            String line;
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(isr)) {
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                line = sb.toString();
                if ((line.contains("x86_64") || line.contains("64"))) {
                    return 64;
                }
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        return 32;
    }
}
