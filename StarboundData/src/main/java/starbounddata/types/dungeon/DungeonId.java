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

package starbounddata.types.dungeon;

import io.netty.buffer.ByteBuf;
import starbounddata.types.SbData;
import starbounddata.types.variants.VLQ;

public class DungeonId extends SbData<DungeonId> {

    private short dungeonId;

    public DungeonId() {
    }

    public DungeonId(short dungeonId) {
        this.dungeonId = dungeonId;
    }

    public DungeonId(ByteBuf in) {
        super(in);
    }

    public DungeonId(DungeonId dungeonId) {
        this.dungeonId = dungeonId.getDungeonId();
    }

    public short getDungeonId() {
        return dungeonId;
    }

    public void setDungeonId(short dungeonId) {
        this.dungeonId = dungeonId;
    }

    @Override
    public void read(ByteBuf in) {
        this.dungeonId = (short) VLQ.readUnsignedFromBufferNoObject(in);//DEBUG
    }

    @Override
    public void write(ByteBuf out){
        byte[] bytes = VLQ.writeUnsignedVLQNoObject(dungeonId);
        out.writeBytes(bytes);
    }

    @Override
    public String toString() {
        return "DungeonId{" +
                "dungeonId=" + dungeonId +
                "} " + super.toString();
    }
}
