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
import starbounddata.types.SbDataInterface;

import java.util.ArrayList;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class VariantList extends ArrayList<Variant> implements SbDataInterface<VariantList> {

    public VariantList() {
        super();
    }

    public VariantList(ByteBuf in) {
        read(in);
    }

    public VariantList(VariantList variantList) {
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
        byte[] bytes = VLQ.writeVLQNoObject(size);
        out.writeBytes(bytes);
        for (Variant variant : this) {
            variant.write(out);
        }
        this.clear();
    }

    @Override
    public VariantList copy(){
        return new VariantList(this);
    }

    @Override
    public String toString() {
        return "VariantList{} " + super.toString();
    }
}
