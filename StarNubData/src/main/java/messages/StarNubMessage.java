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

public class StarNubMessage implements java.io.Serializable {

    public enum Direction {
        CENTRAL_SERVER,
        STARNUB_SERVER
    }

    public enum Type {
        LICENSE,
        SERVER_MESSAGE,
        STARNUB_BROADCAST,
        BAN
    }

    private final Direction DIRECTION;
    private final Type TYPE;

    public StarNubMessage(Direction DIRECTION, Type TYPE) {
        this.DIRECTION = DIRECTION;
        this.TYPE = TYPE;
    }

    public Direction getDIRECTION() {
        return DIRECTION;
    }

    public Type getTYPE() {
        return TYPE;
    }
}
