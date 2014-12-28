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
import starbounddata.types.vectors.Vec3I;

public class CelestialSystemObjects extends SbData<CelestialSystemObjects> implements Celestial<CelestialSystemObjects>{

    private Vec3I systemLocation = new Vec3I();
    private Planets planets = new Planets();

    public CelestialSystemObjects() {
    }

    public CelestialSystemObjects(Vec3I systemLocation, Planets planets) {
        this.systemLocation = systemLocation;
        this.planets = planets;
    }

    public CelestialSystemObjects(ByteBuf in) {
        super(in);
    }

    public CelestialSystemObjects(CelestialSystemObjects celestialSystemObjects) {
        this.systemLocation = celestialSystemObjects.getSystemLocation().copy();
        this.planets = celestialSystemObjects.getPlanets().copy();
    }

    public Vec3I getSystemLocation() {
        return systemLocation;
    }

    public void setSystemLocation(Vec3I systemLocation) {
        this.systemLocation = systemLocation;
    }

    public Planets getPlanets() {
        return planets;
    }

    public void setPlanets(Planets planets) {
        this.planets = planets;
    }

    @Override
    public void read(ByteBuf in) {
        this.systemLocation.read(in);
        this.planets.read(in);
    }

    @Override
    public void write(ByteBuf out) {
        this.systemLocation.write(out);
        this.planets.write(out);
    }

    @Override
    public String toString() {
        return "CelestialSystemObjects{" +
                "systemLocation=" + systemLocation +
                ", planets=" + planets +
                "} " + super.toString();
    }
}
