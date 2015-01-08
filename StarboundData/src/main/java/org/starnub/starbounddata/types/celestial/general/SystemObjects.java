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

package org.starnub.starbounddata.types.celestial.general;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.types.vectors.Vec3I;
import org.starnub.starbounddata.types.SbDataInterface;
import org.starnub.starbounddata.types.variants.VLQ;

import java.util.HashMap;

public class SystemObjects extends HashMap<Vec3I, Planets> implements SbDataInterface<SystemObjects> {

    public SystemObjects() {
        super();
    }

    public SystemObjects(ByteBuf in) {
        read(in);
    }

    public SystemObjects(SystemObjects systemObjects) {
        for (Entry<Vec3I, Planets> entry : systemObjects.entrySet()){
            Vec3I key = entry.getKey();
            Vec3I keyCopy = key.copy();
            Planets planets = entry.getValue();
            Planets planetsCopy = planets.copy();
            this.put(keyCopy, planetsCopy);
        }
    }

    @Override
    public void read(ByteBuf in) {
        long mapLength = VLQ.readUnsignedFromBufferNoObject(in);
        for (int i = 0; i < mapLength; i++) {
            Vec3I key = new Vec3I(in);
            Planets planets = new Planets();
            planets.read(in);
            this.put(key, planets);
        }
    }

    @Override
    public void write(ByteBuf out) {
        long size = (long) this.size();
        byte[] bytes = VLQ.writeUnsignedVLQNoObject(size);
        out.writeBytes(bytes);
        for (Entry<Vec3I, Planets> entry : this.entrySet()){
            Vec3I key = entry.getKey();
            key.write(out);
            Planets planets = entry.getValue();
            planets.write(out);
        }
        this.clear();
    }

    @Override
    public SystemObjects copy() {
        return new SystemObjects(this);
    }

    @Override
    public String toString() {
        return "SystemObjects{} " + super.toString();
    }
}
