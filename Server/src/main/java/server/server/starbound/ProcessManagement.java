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

import org.apache.commons.lang3.exception.ExceptionUtils;
import server.StarNub;

/**
 * This class will create and manage the Starbound Server Process.
 * The methods are protected and can only be accessed via Event wrapper
 * classes in the package {@link org.starnub.server.starbound}.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
class ProcessManagement {

    /**
     * Starbound Sub Process inside of the JVM
     */
    protected volatile static Process sbProcess;

    /**
     * This method will return a file path based on the OS Version and Bit
     * Version.
     *
     * @return A Process that represents the Starbound Server Sub process of this
     * Java VM.
     */
    protected static Process getSbProcess() {
        return sbProcess;
    }

    /**
     * This method will build and start the Starbound Process and Starbound
     * Stream Managers.
     */
    protected static void sb_ProcessStart() {
        try {
            ProcessBuilder sbProcessBuild = new ProcessBuilder(new GetFilePath().getFilePath());
            sbProcessBuild.redirectErrorStream(true);
            sbProcess = sbProcessBuild.start();
            Runnable sb_StreamInput = new ProcessStream();
            new Thread(sb_StreamInput,"StarNub - Starbound - Standard Out_Error Stream").start();
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
    }

    /**
     * This method will kill the running Starbound Server Sub process within this
     * Java VM.
     */
    protected static void sb_ProcessKill() {
        try {
            sbProcess.destroy();
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
    }

    /**
     * This method will return a boolean whether the Starbound Server Sub process
     * is still alive.
     *
     * @return <p>
     * True - Alive
     * <p>
     * False - No process
     */
    protected static boolean sb_ProcessStatus() {
        try {
            return sbProcess.isAlive();
        } catch (Exception e) {
            return false;
        }
    }
}
