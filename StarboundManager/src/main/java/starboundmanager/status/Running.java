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

package starboundmanager.status;


import starboundmanager.StarboundManagement;

/**
 * Represents StarNubs InitializingConnection Status
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Running implements StarboundStatus {

    StarboundManagement starboundManagement;

    public Running(StarboundManagement starboundManagement) {
        this.starboundManagement = starboundManagement;
    }

    @Override
    public String start() {
        String message = "";
        StarNub.getLogger().cInfoPrint("StarNub", message);
        return message;
    }

    @Override
    public String isAlive() {

        return message;
    }

    @Override
    public String isResponsive() {

        return message;
    }

    @Override
    public String stop() {

        return message;
    }
}
