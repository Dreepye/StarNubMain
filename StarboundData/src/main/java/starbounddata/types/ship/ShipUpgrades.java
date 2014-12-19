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

package starbounddata.types.ship;

import io.netty.buffer.ByteBuf;

import java.util.HashSet;

public class ShipUpgrades {

    private byte shipLevel;
    private byte fuelLevel;
    private Capabilities CAPABILITIES = new Capabilities();

    public ShipUpgrades() {

    }

    public ShipUpgrades(ShipUpgrades shipUpgrades) {
        this.shipLevel = shipUpgrades.getShipLevel();
        this.fuelLevel = shipUpgrades.getFuelLevel();
        this.CAPABILITIES = shipUpgrades.getCAPABILITIES().copy();
    }

    public ShipUpgrades(byte shipLevel, byte fuelLevel, HashSet<String> capabilities) {
        this.shipLevel = shipLevel;
        this.fuelLevel = fuelLevel;
        this.CAPABILITIES.addAll(capabilities);
    }

    public ShipUpgrades(ByteBuf in) {
        readShipUpgrades(in);
    }

    public byte getShipLevel() {
        return shipLevel;
    }

    public void setShipLevel(byte shipLevel) {
        this.shipLevel = shipLevel;
    }

    public byte getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(byte fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public Capabilities getCAPABILITIES() {
        return CAPABILITIES;
    }

    public void readShipUpgrades(ByteBuf in) {
        if (in.readableBytes() > 0) {
            this.shipLevel = (byte) in.readUnsignedByte();
        }
        if (in.readableBytes() > 0) {
            this.fuelLevel = (byte) in.readUnsignedByte();
        }
        if (in.readableBytes() != 0) {
            this.CAPABILITIES.readCapabilitiees(in);
        }
    }

    public void writeShipUpgrades(ByteBuf out){
        if (shipLevel != 0) {
            out.writeInt(shipLevel);
        }
        if (shipLevel != 0) {
            out.writeInt(fuelLevel);
        }
        if (CAPABILITIES.size() != 0) {
            CAPABILITIES.writeCapabilities(out);
        }
    }

    public ShipUpgrades copy(){
        return new ShipUpgrades(this);
    }

    @Override
    public String toString() {
        return "ShipUpgrades{" +
                "shipLevel=" + shipLevel +
                ", fuelLevel=" + fuelLevel +
                ", CAPABILITIES=" + CAPABILITIES +
                '}';
    }
}
