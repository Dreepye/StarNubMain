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

package org.starnub.starbounddata.types.chat;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.packets.Packet;
import org.starnub.starbounddata.types.SbData;

public class MessageContext extends SbData<MessageContext> {

    private Mode MODE;
    private String CHANNEL_NAME;

    public MessageContext() {
    }

    public MessageContext(Mode MODE, String CHANNEL_NAME) {
        this.MODE = MODE;
        this.CHANNEL_NAME = CHANNEL_NAME;
    }

    public MessageContext(ByteBuf in) {
        super(in);
    }

    public MessageContext(MessageContext messageContext) {
        this.MODE = messageContext.getMODE();
        this.CHANNEL_NAME = messageContext.getCHANNEL_NAME();
    }

    public Mode getMODE() {
        return MODE;
    }

    public void setMODE(Mode MODE) {
        this.MODE = MODE;
    }

    public String getCHANNEL_NAME() {
        return CHANNEL_NAME;
    }

    public void setCHANNEL_NAME(String CHANNEL_NAME) {
        this.CHANNEL_NAME = CHANNEL_NAME;
    }

    @Override
    public void read(ByteBuf in) {
        this.MODE = Mode.values()[in.readUnsignedByte()];
        this.CHANNEL_NAME = Packet.readVLQString(in);
    }

    @Override
    public void write(ByteBuf out) {
        out.writeByte(MODE.ordinal());
        Packet.writeStringVLQ(out, CHANNEL_NAME);
    }

    @Override
    public String toString() {
        return "MessageContext{" +
                "MODE=" + MODE +
                ", CHANNEL_NAME='" + CHANNEL_NAME + '\'' +
                '}';
    }


}
