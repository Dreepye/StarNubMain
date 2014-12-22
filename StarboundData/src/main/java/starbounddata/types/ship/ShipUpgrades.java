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
import starbounddata.types.SbData;

import java.util.HashSet;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class ShipUpgrades extends SbData<ShipUpgrades> {

    private int shipLevel;
    private int fuelLevel;
    private Capabilities capabilities = new Capabilities();

    public ShipUpgrades() {
    }

    public ShipUpgrades(byte shipLevel, byte fuelLevel, HashSet<String> capabilities) {
        this.shipLevel = shipLevel;
        this.fuelLevel = fuelLevel;
        this.capabilities.addAll(capabilities);
    }

    public ShipUpgrades(ShipUpgrades shipUpgrades) {
        this.shipLevel = shipUpgrades.getShipLevel();
        this.fuelLevel = shipUpgrades.getFuelLevel();
        this.capabilities = shipUpgrades.getCapabilities().copy();
    }

    public ShipUpgrades(ByteBuf in) {
        super(in);
    }

    public int getShipLevel() {
        return shipLevel;
    }

    public void setShipLevel(byte shipLevel) {
        this.shipLevel = shipLevel;
    }

    public int getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(byte fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }

    @Override
    public void read(ByteBuf in) {
        this.shipLevel = in.readInt();
        this.fuelLevel = in.readInt();
        this.capabilities.read(in);
    }

    @Override
    public void write(ByteBuf out){
        out.writeInt(shipLevel);
        out.writeInt(fuelLevel);
        capabilities.write(out);
    }

    @Override
    public String toString() {
        return "ShipUpgrades{" +
                "shipLevel=" + shipLevel +
                ", fuelLevel=" + fuelLevel +
                ", capabilities=" + capabilities +
                '}';
    }
}
