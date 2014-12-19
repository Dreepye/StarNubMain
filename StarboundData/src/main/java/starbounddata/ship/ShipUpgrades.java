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

package starbounddata.ship;

import io.netty.buffer.ByteBuf;

public class ShipUpgrades {

    private int shipLevel;
    private int fuelLevel;
    private final Capabilities CAPABILITIES = new Capabilities();

    public ShipUpgrades() {
    }//TODO CLONE

    public ShipUpgrades(int shipLevel, int fuelLevel) {
        this.shipLevel = shipLevel;
        this.fuelLevel = fuelLevel;
    }

    public ShipUpgrades(ByteBuf in) {
        this.shipLevel = (int) in.readUnsignedInt();
        this.fuelLevel = (int) in.readUnsignedInt();
    }

    public int getShipLevel() {
        return shipLevel;
    }

    public void setShipLevel(int shipLevel) {
        this.shipLevel = shipLevel;
    }

    public int getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(int fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public Capabilities getCAPABILITIES() {
        return CAPABILITIES;
    }

    public void readShipUpgrades(ByteBuf in) {
        this.shipLevel = (int) in.readUnsignedInt();
        this.fuelLevel = (int) in.readUnsignedInt();
    }

    public void writeShipUpgrades(ByteBuf out){
        out.writeInt(shipLevel);
        out.writeInt(fuelLevel);
        CAPABILITIES.writeCapabilities(out);
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
