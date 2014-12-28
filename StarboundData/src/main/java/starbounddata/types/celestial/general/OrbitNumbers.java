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

package starbounddata.types.celestial.general;

import io.netty.buffer.ByteBuf;
import starbounddata.types.SbData;

public class OrbitNumbers extends SbData<OrbitNumbers> {

    private int planetaryOrbitNumber;
    private int satelliteOrbitNumber;

    public OrbitNumbers() {
        super();
    }

    public OrbitNumbers(int planetaryOrbitNumber, int satelliteOrbitNumber) {
        this.planetaryOrbitNumber = planetaryOrbitNumber;
        this.satelliteOrbitNumber = satelliteOrbitNumber;
    }

    public OrbitNumbers(ByteBuf in) {
        super(in);
    }

    public OrbitNumbers(OrbitNumbers orbitalLevels) {
        this.planetaryOrbitNumber = orbitalLevels.getPlanetaryOrbitNumber();
        this.satelliteOrbitNumber = orbitalLevels.getSatelliteOrbitNumber();
    }

    public int getPlanetaryOrbitNumber() {
        return planetaryOrbitNumber;
    }

    public void setPlanetaryOrbitNumber(int planetaryOrbitNumber) {
        this.planetaryOrbitNumber = planetaryOrbitNumber;
    }

    public int getSatelliteOrbitNumber() {
        return satelliteOrbitNumber;
    }

    public void setSatelliteOrbitNumber(int satelliteOrbitNumber) {
        this.satelliteOrbitNumber = satelliteOrbitNumber;
    }

    @Override
    public void read(ByteBuf in) {
        this.planetaryOrbitNumber = in.readInt();
        this.satelliteOrbitNumber = in.readInt();
    }

    @Override
    public void write(ByteBuf out) {
        out.writeInt(planetaryOrbitNumber);
        out.writeInt(satelliteOrbitNumber);
    }

    @Override
    public String toString() {
        return "OrbitalLevels{" +
                "planetaryOrbitNumber=" + planetaryOrbitNumber +
                ", satelliteOrbitNumber=" + satelliteOrbitNumber +
                "} " + super.toString();
    }
}
