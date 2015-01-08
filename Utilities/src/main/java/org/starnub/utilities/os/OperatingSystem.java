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

import org.starnub.utilities.os.linux.LinuxBitVersion;

/**
 * Represents a OperatingSystem this will represent some OS
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class OperatingSystem {

    protected final String OPERATING_SYSTEM;
    protected final BitVersion BIT_VERSION;

    public OperatingSystem() {
        String systemOS = System.getProperty("os.name");
        if (systemOS.startsWith("Windows")){
            this.OPERATING_SYSTEM = "Windows";
            BIT_VERSION = null;
        } else {
            this.OPERATING_SYSTEM = "Linux";
            BIT_VERSION = new LinuxBitVersion();
        }
    }

    public String getOPERATING_SYSTEM() {
        return OPERATING_SYSTEM;
    }

    public BitVersion getBIT_VERSION() {
        return BIT_VERSION;
    }
}
