package starnub;/*
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

import starnub.resources.BannedIPs;
import starnub.resources.ResourceManager;

public class Connections {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final Connections instance = new Connections();

    private static final BannedIPs internallyBannedIps = new BannedIPs(ResourceManager.getStarnubResources());






    /**
     * This constructor is private - Singleton Pattern
     */
    private Connections(){}

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will set and get Connections Singleton instance
     ** @return Connections Singleton Instance
     */
    @SuppressWarnings("unchecked")
    public static Connections getInstance() {
        return instance;
    }

}


