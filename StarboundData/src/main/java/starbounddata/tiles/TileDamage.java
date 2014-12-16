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

package starbounddata.tiles;

import io.netty.buffer.ByteBuf;

/**
 * Represents a TileDamage which contains the class is a Enum for Damage Type, Amount, Harvest Level
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class TileDamage {

    private TileDamageType tileDamageType;
    private float amount;
    private int harvestLevel;

    public TileDamage(TileDamageType tileDamageType, float amount, int harvestLevel) {
        this.tileDamageType = tileDamageType;
        this.amount = amount;
        this.harvestLevel = harvestLevel;
    }

    public TileDamage(ByteBuf in) {
        this.tileDamageType = TileDamageType.values()[in.readUnsignedByte()];
        this.amount = in.readFloat();
        this.harvestLevel = 1;
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

    public TileDamage copy(){
        return new TileDamage(this);
    }

    /**
     * @param out ByteBuf out representing a {@link io.netty.buffer.ByteBuf} to write this TileDamage to
     */
    public void writeTileDamage(ByteBuf out) {
        out.writeByte(tileDamageType.ordinal());
        out.writeFloat(amount);
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

