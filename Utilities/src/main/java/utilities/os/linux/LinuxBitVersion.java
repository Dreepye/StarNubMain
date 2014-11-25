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

package utilities.os.linux;

import utilities.os.BitVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Represents a LinuxKernelBitVersion this will set the bit version in this class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class LinuxBitVersion extends BitVersion {

    public LinuxBitVersion() {
        super();
        try {
            Process p = Runtime.getRuntime().exec("uname -m");
            String line;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                line = input.readLine();
            }
            if (line == null) {
                super.BIT_VERSION = 0;
            } else if (line.contains("x86_64")) {
                super.BIT_VERSION = 64;
            } else if (line.contains("i386")) {
                super.BIT_VERSION = 32;
            } else {
                super.BIT_VERSION = 32;
            }
        } catch (IOException e) {
            e.printStackTrace();
            super.BIT_VERSION = 32;
        }
    }

    public int getBIT_VERSION() {
        return BIT_VERSION;
    }
}
