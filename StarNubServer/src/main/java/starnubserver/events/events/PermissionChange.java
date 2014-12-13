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

package starnubserver.events.events;

import starnubserver.connections.player.session.PlayerSession;
import utilities.events.Modification;

public class PermissionChange {

    private final PlayerSession PLAYER_SESSION;
    private final Modification MODIFICATION;
    private final String PERMISSION;

    public PermissionChange(PlayerSession PLAYER_SESSION, Modification MODIFICATION, String PERMISSION) {
        this.PLAYER_SESSION = PLAYER_SESSION;
        this.MODIFICATION = MODIFICATION;
        this.PERMISSION = PERMISSION;
    }

    public PlayerSession getPLAYER_SESSION() {
        return PLAYER_SESSION;
    }

    public Modification getMODIFICATION() {
        return MODIFICATION;
    }

    public String getPERMISSION() {
        return PERMISSION;
    }
}
