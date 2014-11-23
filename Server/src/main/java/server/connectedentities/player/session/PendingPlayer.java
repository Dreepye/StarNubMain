/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This utilities.file is part of org.starnub a Java Wrapper for Starbound.
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

package server.connectedentities.player.session;

import io.netty.channel.ChannelHandlerContext;
import server.connectedentities.Sender;

import java.net.InetAddress;

/**
 * StarNub's ConnectingPlayer is meant to store data about a player session attempt
 * <p>
 * The data is not permanent from this class, some pieces will be redistributed to
 * other classes if the player starbounddata.packets.connection is successful.
 * <p>
 * All data is based on a "Session Attempt" with StarNub.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class PendingPlayer extends Sender {

    /**
     * Represents the characters uuid for this session attempt
     */

    private final Player PLAYER_SESSION;

    /**
     * Represents the session ip
     */

    private final InetAddress SESSION_IP;

    /**
     * Represents the ChannelHandlerContext for the starbounddata.packets.starbounddata.packets.server side part of this session
     */

    private final ChannelHandlerContext SESSION_SERVER_CTX;

    /**
     * Represents the time the starbounddata.packets.connection attempt was started.
     */

    private final long CONNECTION_PENDING_TIME;

    /**
     * Represents a reason for rejecting a client (online, ban, tban, whitelist)
     */

    private final String REJECT_CLIENT;


    /**
     * Represents a explanation reason for a client rejection to be sent to the client that was rejected
     */

    private final String REJECTION_REASON;

    public Player getPLAYER_SESSION() {
        return PLAYER_SESSION;
    }

    public InetAddress getSESSION_IP() {
        return SESSION_IP;
    }

    public ChannelHandlerContext getSESSION_SERVER_CTX() {
        return SESSION_SERVER_CTX;
    }

    public long getCONNECTION_PENDING_TIME() {
        return CONNECTION_PENDING_TIME;
    }

    public String getREJECT_CLIENT() {
        return REJECT_CLIENT;
    }

    public String getREJECTION_REASON() {
        return REJECTION_REASON;
    }

    /**
     * Constructor used in setting the data for this class
     * @param SENDER_CTX ChannelHandlerContext representing the players of this session initialization
     * @param PLAYER_SESSION Player that represents the character trying to connect to the starbounddata.packets.starbounddata.packets.server
     * @param SESSION_SERVER_CTX ChannelHandlerContext representing the starbounddata.packets.starbounddata.packets.server side of this starbounddata.packets.connection
     * @param REJECT_CLIENT String representing a reason for rejecting a client, (online, ban, tban, whitelist)
     * @param REJECTION_REASON String representing an explanation reason for why the client cannot connect, to be sent to
     */
    public PendingPlayer(Player PLAYER_SESSION, ChannelHandlerContext SENDER_CTX,
                         InetAddress SESSION_IP, ChannelHandlerContext SESSION_SERVER_CTX,
                         String REJECT_CLIENT, String REJECTION_REASON) {
        super(SENDER_CTX);
        this.PLAYER_SESSION = PLAYER_SESSION;
        this.SESSION_IP = SESSION_IP;
        this.SESSION_SERVER_CTX = SESSION_SERVER_CTX;
        this.CONNECTION_PENDING_TIME = System.currentTimeMillis();
        this.REJECT_CLIENT = REJECT_CLIENT;
        this.REJECTION_REASON = REJECTION_REASON;
    }
}
