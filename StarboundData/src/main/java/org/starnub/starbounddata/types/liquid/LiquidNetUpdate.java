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

package org.starnub.starbounddata.types.liquid;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.types.SbData;

public class LiquidNetUpdate extends SbData<LiquidNetUpdate> {

    private byte liquidId;
    private byte level;

    public LiquidNetUpdate() {
    }

    public LiquidNetUpdate(byte liquidId, byte level) {
        this.liquidId = liquidId;
        this.level = level;
    }

    public LiquidNetUpdate(ByteBuf in) {
        super(in);
    }

    public LiquidNetUpdate(LiquidNetUpdate liquidNetUpdate) {
        this.liquidId = liquidNetUpdate.getLiquidId();
        this.level =  liquidNetUpdate.getLevel();
    }

    public byte getLiquidId() {
        return liquidId;
    }

    public void setLiquidId(byte liquidId) {
        this.liquidId = liquidId;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    @Override
    public void read(ByteBuf in) {
        this.liquidId = (byte) in.readUnsignedByte();
        this.level = (byte) in.readUnsignedByte();
    }

    @Override
    public void write(ByteBuf out) {
        out.writeByte(this.liquidId);
        out.writeByte(this.level);
    }

    @Override
    public String toString() {
        return "LiquidNetUpdate{" +
                "liquidId=" + liquidId +
                ", level=" + level +
                "} " + super.toString();
    }

}
