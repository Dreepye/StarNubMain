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

package org.starnub.starbounddata.types.items;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.types.variants.Variant;
import org.starnub.starbounddata.types.SbData;
import org.starnub.starbounddata.types.variants.VLQ;

import java.util.ArrayList;
import java.util.List;

import static org.starnub.starbounddata.packets.Packet.readVLQString;
import static org.starnub.starbounddata.packets.Packet.writeStringVLQ;
import static org.starnub.starbounddata.types.variants.VLQ.writeUnsignedVLQNoObject;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class ItemDescriptor extends SbData<ItemDescriptor>{

    private String name;
    private long  count;
    private Variant parameters = new Variant();
//    private byte[] parametersSha256;
    private List<ItemDescriptor> itemDescriptors = new ArrayList<>();

    public ItemDescriptor(){
    }

    public ItemDescriptor(String name, long count, Variant parameters, List<ItemDescriptor> itemDescriptors) {
        this.name = name;
        this.count = count;
        this.parameters = parameters;
//        this.parametersSha256 = parametersSha256;
    }

    public ItemDescriptor(ByteBuf in){
        super(in);
    }

    public ItemDescriptor(ItemDescriptor itemDescriptor) {
        this.name = itemDescriptor.getName();
        this.count = itemDescriptor.getCount();
        this.parameters = itemDescriptor.getParameters();
//        this.parametersSha256 = itemDescriptor.getParametersSha256().clone();
        itemDescriptors = new ArrayList<>();
        for (ItemDescriptor itemDescriptorItem : itemDescriptors){
            ItemDescriptor itemDescriptorCopy = itemDescriptorItem.copy();
            itemDescriptors.add(itemDescriptorCopy);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Variant getParameters() {
        return parameters;
    }

    public void setParameters(Variant parameters) {
        this.parameters = parameters;
    }
//
//    public byte[] getParametersSha256() {
//        return parametersSha256;
//    }
//
//    public void setParametersSha256(byte[] parametersSha256) {
//        this.parametersSha256 = parametersSha256;
//    }

    @Override
    public void read(ByteBuf in) {
        this.name = readVLQString(in);
        this.count = VLQ.readUnsignedFromBufferNoObject(in);
        this.parameters.read(in);
        while (in.readableBytes() > 0) {
            ItemDescriptor itemDescriptor = new ItemDescriptor(in);
            itemDescriptors.add(itemDescriptor);
        }
    }

    @Override
    public void write(ByteBuf out){
        writeStringVLQ(out, this.name);
        out.writeBytes(writeUnsignedVLQNoObject(this.count));
        this.parameters.write(out);
//        boolean hasParametersShae256 =  parametersSha256 != null;
        if(itemDescriptors.size() > 0){
            for(ItemDescriptor itemDescriptor : itemDescriptors){
                itemDescriptor.write(out);
            }
        }
    }

    @Override
    public String toString() {
        return "ItemDescriptor{" +
                "name='" + name + '\'' +
                ", count=" + count +
                ", parameters=" + parameters +
                ", itemDescriptors=" + itemDescriptors +
                "} " + super.toString();
    }
}
