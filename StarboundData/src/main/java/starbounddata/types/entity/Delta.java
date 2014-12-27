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

package starbounddata.types.entity;

import io.netty.buffer.ByteBuf;
import starbounddata.types.SbData;
import starbounddata.types.variants.VLQ;

import static starbounddata.packets.Packet.readVLQString;
import static starbounddata.packets.Packet.writeStringVLQ;

public class Delta extends SbData<Delta> {

    private long lengthTemp;
    private String unknown;
    private float unknownFloat1;

    private byte[] temp;

    private String unknown2;
    private float unkownFloat2;

    public Delta() {
    }

    @Override
    public void read(ByteBuf in) {
        lengthTemp = VLQ.readUnsignedFromBufferNoObject(in);
        unknown = readVLQString(in);
        unknownFloat1 = in.readFloat();
        temp = in.readBytes(in.readableBytes()).array();
    }

    @Override
    public void write(ByteBuf out) {
        byte[] bytes = VLQ.writeVLQNoObject(lengthTemp);
        out.writeBytes(bytes);
        writeStringVLQ(out, unknown);
        out.writeFloat(unknownFloat1);
        out.writeBytes(temp);
    }

    @Override
    public String toString() {
        return "Delta{" +
                "unknown='" + unknown + '\'' +
                ", unknownFloat1=" + unknownFloat1 +
                "} " + super.toString();
    }
}
