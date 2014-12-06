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

package starnubserver.cache.objects;

import starnubserver.connections.player.session.PlayerSession;
import utilities.cache.objects.TimeCache;

/**
 * Represents a RejectionReason Cache. This can be used in any Cache Wrapper
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class RejectionCache extends TimeCache {

    public enum Reason {
        RESTARTING,
        WHITELIST,
        TEMPORARY_BANNED,
        BANNED,
        ALREADY_LOGGED_IN,
        SERVER_FULL,
        SERVER_FULL_NO_VIP,
        SERVER_FULL_NO_VIP_KICK
    }

    private final boolean REJECTED;
    private final Reason REJECTION_REASON;
    private final String PACKET_MESSAGE;
    private final PlayerSession PLAYERSession;

    public RejectionCache(boolean REJECTED, PlayerSession PLAYERSession) {
        this.REJECTED = REJECTED;
        this.REJECTION_REASON = null;
        this.PACKET_MESSAGE = null;
        this.PLAYERSession = PLAYERSession;
    }

    public RejectionCache(boolean REJECTED, Reason REJECTION_REASON, String PACKET_MESSAGE, PlayerSession PLAYERSession) {
        this.REJECTED = REJECTED;
        this.REJECTION_REASON = REJECTION_REASON;
        this.PACKET_MESSAGE = PACKET_MESSAGE;
        this.PLAYERSession = PLAYERSession;
    }

    public boolean isREJECTED() {
        return REJECTED;
    }

    public Reason getREJECTION_REASON() {
        return REJECTION_REASON;
    }

    public String getPACKET_MESSAGE() {
        return PACKET_MESSAGE;
    }

    public PlayerSession getPLAYERSession() {
        return PLAYERSession;
    }
}
