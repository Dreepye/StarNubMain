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

package starbounddata.types.planet;

import io.netty.buffer.ByteBuf;
import starbounddata.types.SbData;

public class OrbitalLevels extends SbData<OrbitalLevels> {

    private int planetOrbitalLevels;
    private int satelliteOrbitalLevels;

    public OrbitalLevels() {
        super();
    }

    public OrbitalLevels(int planetOrbitalLevels, int satelliteOrbitalLevels) {
        this.planetOrbitalLevels = planetOrbitalLevels;
        this.satelliteOrbitalLevels = satelliteOrbitalLevels;
    }

    public OrbitalLevels(ByteBuf in) {
        super(in);
    }

    public OrbitalLevels(OrbitalLevels orbitalLevels) {
        this.planetOrbitalLevels = orbitalLevels.getPlanetOrbitalLevels();
        this.satelliteOrbitalLevels = orbitalLevels.getSatelliteOrbitalLevels();
    }

    public int getPlanetOrbitalLevels() {
        return planetOrbitalLevels;
    }

    public void setPlanetOrbitalLevels(int planetOrbitalLevels) {
        this.planetOrbitalLevels = planetOrbitalLevels;
    }

    public int getSatelliteOrbitalLevels() {
        return satelliteOrbitalLevels;
    }

    public void setSatelliteOrbitalLevels(int satelliteOrbitalLevels) {
        this.satelliteOrbitalLevels = satelliteOrbitalLevels;
    }

    @Override
    public void read(ByteBuf in) {
        this.planetOrbitalLevels = in.readInt();
        this.satelliteOrbitalLevels = in.readInt();
    }

    @Override
    public void write(ByteBuf out) {
        out.writeInt(planetOrbitalLevels);
        out.writeInt(satelliteOrbitalLevels);
    }

    @Override
    public String toString() {
        return "OrbitalLevels{" +
                "planetOrbitalLevels=" + planetOrbitalLevels +
                ", satelliteOrbitalLevels=" + satelliteOrbitalLevels +
                "} " + super.toString();
    }
}
