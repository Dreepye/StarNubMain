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

package org.starnub.starnubserver.events.events;

import org.starnub.starnubdata.generic.DisconnectReason;
import org.starnub.starnubserver.connections.player.session.PlayerSession;

public class DisconnectData {

    private final PlayerSession PLAYER_SESSION;
    private final DisconnectReason DISCONNECT_REASON;

    public DisconnectData(PlayerSession PLAYER_SESSION, DisconnectReason DISCONNECT_REASON) {
        this.PLAYER_SESSION = PLAYER_SESSION;
        this.DISCONNECT_REASON = DISCONNECT_REASON;
    }

    public PlayerSession getPLAYER_SESSION() {
        return PLAYER_SESSION;
    }

    public DisconnectReason getDISCONNECT_REASON() {
        return DISCONNECT_REASON;
    }

    @Override
    public String toString() {
        return "DisconnectData{" +
                "PLAYER_SESSION=" + PLAYER_SESSION +
                ", DISCONNECT_REASON=" + DISCONNECT_REASON +
                '}';
    }
}
