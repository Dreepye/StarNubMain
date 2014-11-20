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

package starbounddata.packets.chat;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.Packet;
import starbounddata.packets.Packets;

import static starbounddata.packets.StarboundBufferReader.*;
import static starbounddata.packets.StarboundBufferWriter.*;

/**
 * Represents the ChatReceivedPacket and methods to generate a packet data for StarNub and Plugins
 * <p/>
 * Notes: This packet can be edited freely. Please be cognisant of what values you change and how they will be interpreted by the client
 * <p/>
 * Packet Direction: Server -> Client
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ChatReceivePacket extends Packet {

    /**
     * <br>
     * 0 - Planet (String: ship_d07cdd7eb5bcba7a306edcf0fe610010 or Alpha Eta Car 0368 II a)
     * <br>
     * 1 - Universe
     * <br>
     * 2 - Whisper
     * <br>
     * 3 - Command
     */
    private ChatReceiveChannel channel;

    /**
     * ToDo: Insert expected string display
     */
    private String world;

    /**
     * ClientId of the sender which is assigned by the Starbound server
     */
    private int clientId;

    /**
     * Name of who sent the message
     */
    private String name;

    /**
     * Message sent from the server
     */
    private String message;

    public ChatReceiveChannel getChannel() {
        return channel;
    }

    public void setChannel(ChatReceiveChannel channel) {
        this.channel = channel;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param channel         String one of the following:
     *                        <br>
     *                        0 - Local Chat (Planet) (Object Sent: ship_d07cdd7eb5bcba7a306edcf0fe610010 or Alpha Eta Car 0368 II a)
     *                        <br>
     *                        1 - Entire Server (Universe)
     *                        <br>
     *                        2 - Whisper
     *                        <br>
     *                        3 - Command Results
     * @param DESTINATION_CTX ChannelHandlerContext representing the packet destination
     * @param world           String ToDo: Insert expected string display
     * @param clientId        String clientID of the sender. *STARNUB RESERVES 5000*
     * @param name            String name of the Sender
     * @param message         String the message
     */
    public ChatReceivePacket(ChannelHandlerContext DESTINATION_CTX, ChatReceiveChannel channel, String world, long clientId, String name, String message) {
        super(Packets.CHATRECEIVED.getPacketId(), null, DESTINATION_CTX);
        this.channel = channel;
        this.world = world;
        this.clientId = (int) clientId;
        this.name = name;
        this.message = message;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p/>
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This method will read in a {@link io.netty.buffer.ByteBuf} into this packets fields
     * <p/>
     *
     * @param in ByteBuf representing the reason to be read into the packet
     */
    @Override
    public void read(ByteBuf in) {
        this.channel = ChatReceiveChannel.values()[readUnsignedByte(in)];
        this.world = readStringVLQ(in);
        this.clientId = readUnsignedInt(in);
        this.name = readStringVLQ(in);
        this.message = readStringVLQ(in);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p/>
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This method will write to a {@link io.netty.buffer.ByteBuf} using this packets fields
     * <p/>
     *
     * @param out ByteBuf representing the space to write out the packet reason
     */
    @Override
    public void write(ByteBuf out) {
        writeByte(out, (byte) this.channel.ordinal());
        writeStringVLQ(out, this.world);
        writeInt(out, this.clientId);
        writeStringVLQ(out, this.name);
        writeStringVLQ(out, this.message);
    }

    public enum ChatReceiveChannel {
        PLANET,
        UNIVERSE,
        WHISPER,
        COMMAND
    }
}
