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

package org.starnub.starbounddata.types.dungeon;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.types.SbDataInterface;
import org.starnub.starbounddata.types.variants.VLQ;

import java.util.HashSet;

public class ProtectedDungeonIds extends HashSet<DungeonId> implements SbDataInterface<ProtectedDungeonIds> {

    public ProtectedDungeonIds() {
        super();
    }

    public ProtectedDungeonIds(ByteBuf in) {
        read(in);
    }

    public ProtectedDungeonIds(ProtectedDungeonIds protectedDungeonIds) {
        for (DungeonId dungeonId : protectedDungeonIds) {
            DungeonId dungeonIdCopy = dungeonId.copy();
            this.add(dungeonIdCopy);
        }
    }

    @Override
    public void read(ByteBuf in) {
        long arrayLength = VLQ.readUnsignedFromBufferNoObject(in);
        for (int i = 0; i < arrayLength; i++) {
            DungeonId dungeonId = new DungeonId(in);
            this.add(dungeonId);
        }
    }

    @Override
    public void write(ByteBuf out) {
        long size = (long) this.size();
        byte[] bytes = VLQ.writeUnsignedVLQNoObject(size);
        out.writeBytes(bytes);
        for (DungeonId dungeonId : this) {
            dungeonId.write(out);
        }
        this.clear();
    }

    @Override
    public ProtectedDungeonIds copy() {
        return new ProtectedDungeonIds(this);
    }

    @Override
    public String toString() {
        return "ProtectedDungeonIds{} " + super.toString();
    }
}