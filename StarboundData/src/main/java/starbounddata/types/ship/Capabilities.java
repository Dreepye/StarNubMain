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

package starbounddata.types.ship;

import io.netty.buffer.ByteBuf;
import starbounddata.packets.Packet;
import starbounddata.types.CollectInterface;
import starbounddata.types.variants.VLQ;

import java.util.HashSet;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class Capabilities extends HashSet<String> implements CollectInterface<Capabilities> {

    public Capabilities() {
    }

    public Capabilities(Capabilities capabilities){
        capabilities.forEach(this::add);
    }

    public Capabilities(ByteBuf in){
        read(in);
    }

    public Capabilities(HashSet<String> capabilities){
        this.addAll(capabilities);
    }

    @Override
    public void read(ByteBuf in){
        long  hastSetLength = VLQ.readUnsignedFromBufferNoObject(in);
        for (int i = 0; i < hastSetLength; i++) {
            String string = Packet.readVLQString(in);
            this.add(string);
        }
    }

    @Override
    public  void write(ByteBuf out){
        out.writeBytes(VLQ.writeVLQNoObject((long) this.size()));
        for (String string : this) {
            Packet.writeStringVLQ(out, string);
        }
        this.clear();
    }

    @Override
    public Capabilities copy(){
        return new Capabilities(this);
    }

    @Override
    public String toString() {
        return "Capabilities{} " + super.toString();
    }
}
