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

package starbounddata.types.celestial;

import io.netty.buffer.ByteBuf;
import starbounddata.types.SbDataInterface;
import starbounddata.types.variants.VLQ;
import starbounddata.types.variants.Variant;

import java.util.ArrayList;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class CelestialList extends ArrayList<Variant> implements SbDataInterface<CelestialList> {

    public CelestialList() {
        super();
    }

    public CelestialList(ByteBuf in) {
        read(in);
    }

    public CelestialList(CelestialList variantList) {
        for (Variant variant : variantList){
            Variant variantCopy = variant.copy();
            this.add(variantCopy);
        }
    }

    @Override
    public void read(ByteBuf in){
        long arrayLength = VLQ.readUnsignedFromBufferNoObject(in);
        for (int i = 0; i < arrayLength; i++) {
            Variant variant = new Variant(in);
            this.add(variant);
        }
    }

    @Override
    public void write(ByteBuf out) {
        long size = (long) this.size();
        byte[] bytes = VLQ.writeUnsignedVLQNoObject(size);
        out.writeBytes(bytes);
        for (Variant variant : this) {
            variant.write(out);
        }
        this.clear();
    }

    @Override
    public CelestialList copy(){
        return new CelestialList(this);
    }

    @Override
    public String toString() {
        return "EphemeralStatusEffects{} " + super.toString();
    }
}
