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

import lombok.Getter;
import org.codehome.utilities.operatingsystem.LinuxKernelBitVersion;
import server.StarNub;

/**
 * This class will determine where Starbound Server is located at within the
 * Starbound directory and based on the system OS.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
final class GetFilePath {

    @Getter
    private String filePath = setFilePath();

    /**
     * This method will return a file path based on the OS Version and Bit
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

    /**
     * This method will find what OS the user is using
     * and return a String file path for where Starbound
     * should be located.
     *
     * @return String that represents the file path
     */
    private String setFilePath() {
        String starting = "Starting the Starbound Server. ";
        String systemOS = System.getProperty("os.name");
        Boolean osWindows = systemOS.startsWith("Windows");

        if (osWindows) {
            StarNub.getLogger().cInfoPrint("StarNub", starting+"Using Win32 Starbound_Server.exe.");
            return "win32/starbound_server.exe";
        } else {
            String linuxKernel = new LinuxKernelBitVersion().getKernelVersion();
            int bitVersion = 0;
            if (linuxKernel.contains("i386")) {
                bitVersion = 32;
            } else if (linuxKernel.contains("x86_64")) {
                bitVersion = 64;
            }
            switch (bitVersion) {
                case 64: {
                    StarNub.getLogger().cInfoPrint("StarNub", starting+"Using Linux64 Starbound_Server.");
                    return "./linux64/starbound_server";
                }
                case 32: {
                    StarNub.getLogger().cInfoPrint("StarNub", starting+"Using Linux32 Starbound_Server.");
                    return "./linux32/starbound_server";
                }
                default: {
                    StarNub.getLogger().cErrPrint("StarNub", starting+"Error detecting Linux Kernel Bit Version");
                    return "./linux32/starbound_server";
                }
            }
        }
    }
}
