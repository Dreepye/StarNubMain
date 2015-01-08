/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This utilities.file is part of org.starnub a Java Wrapper for Starbound.
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

package org.starnub.starbounddata.types.tile;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.types.SbData;

/**
 * Represents a TileDamage which contains the class is a Enum for Damage Type, Amount, Harvest Level
 * <p>
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class TileDamage extends SbData<TileDamage> {

    private TileDamageType tileDamageType;
    private float amount;
    private int harvestLevel;

    public TileDamage() {
    }

    public TileDamage(TileDamageType tileDamageType, float amount, int harvestLevel) {
        this.tileDamageType = tileDamageType;
        this.amount = amount;
        this.harvestLevel = harvestLevel;
    }

    public TileDamage(ByteBuf in) {
        super(in);
    }

    public TileDamage(TileDamage tileDamage) {
        this.tileDamageType = tileDamage.getTileDamageType();
        this.amount = tileDamage.getAmount();
        this.harvestLevel = tileDamage.getHarvestLevel();
    }

    public TileDamageType getTileDamageType() {
        return tileDamageType;
    }

    public void setTileDamageType(TileDamageType tileDamageType) {
        this.tileDamageType = tileDamageType;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getHarvestLevel() {
        return harvestLevel;
    }

    public void setHarvestLevel(int harvestLevel) {
        this.harvestLevel = harvestLevel;
    }

    @Override
    public void read(ByteBuf in){
        this.tileDamageType = TileDamageType.values()[in.readUnsignedByte()];
        this.amount = in.readFloat();
        this.harvestLevel = in.readInt();
    }

    @Override
    public void write(ByteBuf out) {
        out.writeByte(tileDamageType.ordinal());
        out.writeFloat(amount);
        out.writeInt(harvestLevel);
    }

    @Override
    public String toString() {
        return "TileDamage{" +
                "tileDamageType=" + tileDamageType +
                ", amount=" + amount +
                ", harvestLevel=" + harvestLevel +
                '}';
    }
}

