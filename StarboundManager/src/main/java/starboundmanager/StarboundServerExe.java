/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This utilities.file is part of org.starnub a Java Wrapper for Starbound.
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

import utilities.os.OperatingSystem;

/**
 * Represents GetFilePath this will get a file path for which starbound_server.exe
 * to be using, based on the operating system.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarboundServerExe extends OperatingSystem {

    private String filePath;

    public StarboundServerExe() {
        super();
        String starting = "Starting the Starbound Server. ";
        if (OPERATING_SYSTEM.contains("Windows")) {
            filePath = "win32/starbound_server.exe";
        } else {
            switch (super.BIT_VERSION.getBIT_VERSION()) {
                case 32: {
                    filePath = "./linux32/starbound_server";
                    break;
                }
                case 64: {
                    filePath = "./linux64/starbound_server";
                    break;
                }
            }
        }
    }

    /**
     * Recommended: For connections use.
     * <p>
     * Uses: This method will return a file path based on the OS Version and Bit
     * Version.
     *
     * @return A integer that represents the bit version.
     * <p>
     * Windows = win32/starbound_server.exe
     * <p>
     * Linux 32 = ./linux32/starbound_server
     * <p>
     * Linux 64 = ./linux64/starbound_server
     */
    public String getFilePath() {
        return filePath;
    }
}
