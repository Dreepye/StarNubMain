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

package messages;

public abstract class StarNubBan extends StarNubMessage {

    public enum BanIdentifier{
        IP,
        UUID
    }

    public enum BanRequest{
        ADD,
        REMOVE
    }

    private final BanIdentifier BAN_IDENTIFIER;
    private final BanRequest BAN_REQUEST;

    public StarNubBan(Direction DIRECTION, Type TYPE, BanIdentifier BAN_IDENTIFIER, BanRequest BAN_REQUEST) {
        super(DIRECTION, TYPE);
        this.BAN_IDENTIFIER = BAN_IDENTIFIER;
        this.BAN_REQUEST = BAN_REQUEST;
    }

    public BanIdentifier getBAN_IDENTIFIER() {
        return BAN_IDENTIFIER;
    }

    public BanRequest getBAN_REQUEST() {
        return BAN_REQUEST;
    }

}
