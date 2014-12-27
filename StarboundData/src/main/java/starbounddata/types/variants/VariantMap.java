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

package starbounddata.types.variants;

import io.netty.buffer.ByteBuf;
import starbounddata.types.CollectionsInterface;

import java.util.HashMap;

import static starbounddata.packets.Packet.readVLQString;
import static starbounddata.packets.Packet.writeStringVLQ;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class VariantMap extends HashMap<String, Variant> implements CollectionsInterface<VariantMap>{

    public VariantMap() {
        super();
    }

    public VariantMap(ByteBuf in) {
        read(in);
    }

    public VariantMap(VariantMap variantMap) {
        for (Entry<String, Variant> entry : variantMap.entrySet()){
            String key = entry.getKey();
            Variant variant = entry.getValue();
            Variant variantCopy = variant.copy();
            this.put(key, variantCopy);
        }
    }

    @Override
    public void read(ByteBuf in) {
        long mapLength = VLQ.readUnsignedFromBufferNoObject(in);
        for (int i = 0; i < mapLength; i++) {
            String key = readVLQString(in);
            Variant variant = new Variant(in);
            this.put(key, variant);
        }
    }

    @Override
    public void write(ByteBuf out) {
        long size = (long) this.size();
        byte[] bytes = VLQ.writeVLQNoObject(size);
        out.writeBytes(bytes);
        for (Entry<String, Variant> entry : this.entrySet()){
            String key = entry.getKey();
            writeStringVLQ(out, key);
            Variant variant = entry.getValue();
            variant.write(out);
        }
        this.clear();
    }

    @Override
    public VariantMap copy() {
        return new VariantMap(this);
    }

    @Override
    public String toString() {
        return "VariantMap{} " + super.toString();
    }
}
