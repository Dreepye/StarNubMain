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

package starbounddata.ship;

import io.netty.buffer.ByteBuf;
import starbounddata.packets.Packet;
import starbounddata.variants.VLQ;

import java.util.HashSet;

public class Capabilities extends HashSet<String> {

    private final static int HASHSET_SIZE_LIMIT = 50;

    public Capabilities(){
    }//TODO CLONE

    public Capabilities(ByteBuf in){
        int hastSetLength = VLQ.readUnsignedFromBufferNoObject(in);
        if (hastSetLength > HASHSET_SIZE_LIMIT) {
            throw new ArrayIndexOutOfBoundsException();
        }
        for (int i = 0; i < hastSetLength; i++) {
            String string = Packet.readStringVLQ(in);
            this.add(string);
        }
    }

    public Capabilities(HashSet<String> capabilities){
        this.addAll(capabilities);
    }

    public static int getHashsetSizeLimit() {
        return HASHSET_SIZE_LIMIT;
    }

    public void readCapabilitiees(ByteBuf in){
        int hastSetLength = VLQ.readUnsignedFromBufferNoObject(in);
        if (hastSetLength > HASHSET_SIZE_LIMIT) {
            throw new ArrayIndexOutOfBoundsException();
        }
        for (int i = 0; i < hastSetLength; i++) {
            String string = Packet.readStringVLQ(in);
            this.add(string);
        }
    }

    public  void writeCapabilities(ByteBuf out){
        out.writeBytes(VLQ.createVLQNoObject((long) this.size()));
        for (String string : this) {
            Packet.writeStringVLQ(out, string);
        }
        this.clear();
    }

    @Override
    public String toString() {
        return "Capabilities{} " + super.toString();
    }
}
