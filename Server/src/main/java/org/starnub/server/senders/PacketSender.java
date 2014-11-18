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

package org.starnub.server.senders;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import org.starnub.server.connectedentities.Sender;
import org.starnub.server.connectedentities.player.session.Player;
import org.starnub.server.server.packets.Packet;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static org.codehome.utilities.files.compression.Zlib.compress;
import static org.starnub.server.server.packets.StarboundBufferWriter.writeByte;
import static org.starnub.server.server.packets.StarboundBufferWriter.writeByteArray;
import static org.starnub.server.server.packets.StarboundBufferWriter.writeSignedVLQNoObject;

/**
 * StarNub's Packet Sender
 * <p>
 * Packets must be sent by using a specific channel.
 * <p>
 * Channels are basically the link between the StarNub
 * and the player and StarNub to the server.
 * <p>
 * EXAMPLE:<br>
 *            |Client Channel |      StarNub      | Server Channel|<br>
 * Player <-> |PORT-SOCKET    | StarNub Internals |    PORT-SOCKET| <-> Starbound<br>
 *            |- SENDER_CTX                     SESSION_SERVER_CTX| <br>
 *            |- SESSION_CLIENT_CTX <br><br>
 *
 * When sending any packet consider what direction you are going.
 * <p>
 * SENDER_CTX: well send it to any destination that initiated the connection
 * to StarNub which would a player, management or any other client interacting with StarNub.
 * <p>
 * SESSION_CLIENT_CTX will send it to the client side of a player connection only. (Sent to Player)
 * <p>
 * SESSION_SERVER_CTX will send it to the server side of a player connection only. (Sent to Server)
 * <p>
 * Sending a message goes as such: <br>
 * sender.getSENDER_CTX.writeAndFlush(msg);
 * sender.getSESSION_CLIENT_CTX.writeAndFlush(msg);
 * sender.getSESSION_SERVER_CTX.writeAndFlush(msg);
 * <p>
 * You cannot forget that the msg must be the packet encoded.
 * <p>
 * sender.getSENDER_CTX.writeAndFlush(PacketEncoder.packetToMessageEncoder(packet);
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public enum PacketSender {
    INSTANCE;

    /**
     *
     * @param sender Sender who initiated this packet send
     * @param packet Packet that needs to be sent
     */
    public void clientPacketSender(Sender sender, Packet packet) {
        sender.getSENDER_CTX().writeAndFlush(packetToMessageEncoder(packet), sender.getSENDER_CTX().voidPromise());
    }

    /**
     *
     * @param sender Sender who initiated this packet send
     * @param packet Packet that needs to be sent
     */
    public void playerPacketSender(Player sender, Packet packet) {
        sender.getClientCtx().writeAndFlush(packetToMessageEncoder(packet), sender.getClientCtx().voidPromise());
    }

    /**
     *
     * @param sender Sender who initiated this packet send
     * @param packet Packet that needs to be sent
     */
    public void serverPacketSender(Player sender, Packet packet) {
        sender.getServerCtx().writeAndFlush(packetToMessageEncoder(packet), sender.getServerCtx().voidPromise());
    }

    /**
     *
     * @param serverCTX ChannelHandlerContext who initiated this packet send
     * @param packet Packet that needs to be sent
     */
    public void serverPacketSender(ChannelHandlerContext serverCTX, Packet packet) {
        serverCTX.writeAndFlush(packetToMessageEncoder(packet), serverCTX.voidPromise());
    }

    /**
     * This method is used internally to route packets
     * @param packet Packet the data to be routed
     */
    public void destinationPacketRouter(Packet packet) {
        packet.getDestinationCTX().writeAndFlush(packetToMessageEncoder(packet), packet.getDestinationCTX().voidPromise());
    }

    /**
     * This method is used internally to route packets
     * @param packet Packet the data to be routed
     */
    public void destinationPacketRouter(ChannelHandlerContext ctx, Packet packet) {
        ctx.writeAndFlush(packetToMessageEncoder(packet), ctx.voidPromise());
    }

    /**
     * This method is used route packets by Channel.
     *
     * @param channel Channel to send the packet to
     * @param packet Packet that needs to be sent
     */
    public void channelPacketRouter(Channel channel, Packet packet) {
        channel.writeAndFlush(packetToMessageEncoder(packet), channel.voidPromise());
    }

    /**
     * This method is used to route packets by ChannelHandlerContext
     *
     * @param ctx ChannelHandlerContext to send the packet to
     * @param packet Packet that needs to be sent
     */
    public void channelHandlerContextPacketSender(ChannelHandlerContext ctx, Packet packet) {
        ctx.writeAndFlush(packetToMessageEncoder(packet), ctx.voidPromise());
    }

    /**
     * This method is used to route packets by ChannelGroup
     *
     * @param channelGroup ChannelGroup to send the packet to
     * @param packet Packet that needs to be sent
     */
    public void channelGroupPacketSender(ChannelGroup channelGroup, Packet packet) {
//        channelGroup.writeAndFlush();
        for (Channel channel : channelGroup){
            ByteBuf byteBuf = packetToMessageEncoder(packet);
            channel.writeAndFlush(byteBuf, channel.voidPromise());
        }
    }

    /**
     * This method is used to route packets by ChannelGroup
     *
     * @param channelGroup ChannelGroup to send the packet to
     * @param packet Packet that needs to be sent
     */
    public void channelGroupPacketSenderIgnoreList(ChannelGroup channelGroup, ArrayList<Channel> ignoredChannels, Packet packet) {
        if (!ignoredChannels.isEmpty()) {
            channelGroup.stream().filter(channel -> !ignoredChannels.contains(channel)).forEach(channel -> {
                channel.writeAndFlush(packetToMessageEncoder(packet), channel.voidPromise());
            });
        } else {
//            channelGroup.writeAndFlush(packetToMessageEncoder(packet));
            for (Channel channel : channelGroup){
                ByteBuf byteBuf = packetToMessageEncoder(packet);
                channel.writeAndFlush(byteBuf, channel.voidPromise());
            }
        }
    }

    /**
     * This method is used to broadcast cast packets to the server on behalf of all the online connected clients
     * @param onlinePlayers ConcurrentHashMap of the online players
     * @param packet Packet that needs to be sent
     * @param ignoredPlayerSessions ArrayList of Player(s) to ignored
     */
    public void serverPacketBroadcast(ConcurrentHashMap<ChannelHandlerContext, Player> onlinePlayers, Packet packet, ArrayList<Player> ignoredPlayerSessions) {
        packetBroadcaster(onlinePlayers, packet, ignoredPlayerSessions, true);
    }

    /**
     * This method is used to broadcast cast packets to all online clients
     * @param onlinePlayers ConcurrentHashMap of the online players
     * @param packet Packet that needs to be sent
     * @param ignoredPlayerSessions ArrayList of Player(s) to ignored
     */
    public void playerPacketBroadcast(ConcurrentHashMap<ChannelHandlerContext, Player> onlinePlayers, Packet packet, ArrayList<Player> ignoredPlayerSessions) {
        packetBroadcaster(onlinePlayers, packet, ignoredPlayerSessions, false);
    }


    /**
     * Private internal method to create less user input into the API
     * @param onlinePlayers ConcurrentHashMap of the online players
     * @param packet Packet that needs to be sent
     * @param ignoredPlayerSessions ArrayList of Player(s) to ignored
     * @param server boolean if the message is to the server or not
     */
    private void packetBroadcaster(ConcurrentHashMap<ChannelHandlerContext, Player> onlinePlayers, Packet packet, ArrayList<Player> ignoredPlayerSessions, boolean server) {
        if (ignoredPlayerSessions != null) {
            onlinePlayers.values().stream().filter(playerSession -> !ignoredPlayerSessions.contains(playerSession)).forEach(playerSession -> {
                if (server) {
                    serverPacketSender(playerSession, packet);
                } else {
                    playerPacketSender(playerSession, packet);
                }
            });
        } else {
            for (Player playerSession : onlinePlayers.values()) {
                if (server) {
                    serverPacketSender(playerSession, packet);
                } else {
                    playerPacketSender(playerSession, packet);
                }
            }
        }
    }

    private static ByteBuf packetToMessageEncoder(Packet packet) {
        ByteBuf msgOut = PooledByteBufAllocator.DEFAULT.directBuffer();
        packet.write(msgOut);
        int payloadLengthOut = msgOut.readableBytes();
        byte[] dataOut;
        if (payloadLengthOut > 100) {
            dataOut = compress(msgOut.readBytes(payloadLengthOut).array());
            payloadLengthOut = -dataOut.length;
        } else {
            dataOut = msgOut.readBytes(payloadLengthOut).array();
        }
        msgOut.clear();
        writeByte(msgOut, packet.getPacketId());
        writeSignedVLQNoObject(msgOut, payloadLengthOut);
        writeByteArray(msgOut, dataOut);
        return msgOut;
    }
}
