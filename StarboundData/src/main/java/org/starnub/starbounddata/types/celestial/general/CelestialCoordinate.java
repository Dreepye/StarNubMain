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

package org.starnub.starbounddata.types.celestial.general;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.types.SbData;
import org.starnub.starbounddata.types.vectors.Vec3I;

public class CelestialCoordinate extends SbData<CelestialCoordinate>{

    private Vec3I location = new Vec3I();
    private OrbitNumbers orbitNumbers = new OrbitNumbers();

    public CelestialCoordinate() {
    }

    public CelestialCoordinate(Vec3I location, OrbitNumbers orbitNumbers) {
        this.location = location;
        this.orbitNumbers = orbitNumbers;
    }

    public CelestialCoordinate(ByteBuf in) {
        super(in);
    }

    public CelestialCoordinate(CelestialCoordinate celestialCoordinate) {
        this.location = celestialCoordinate.getLocation().copy();
        this.orbitNumbers = celestialCoordinate.getOrbitNumbers().copy();
    }

    public Vec3I getLocation() {
        return location;
    }

    public void setLocation(Vec3I location) {
        this.location = location;
    }

    public OrbitNumbers getOrbitNumbers() {
        return orbitNumbers;
    }

    public void setOrbitNumbers(OrbitNumbers orbitNumbers) {
        this.orbitNumbers = orbitNumbers;
    }

    @Override
    public void read(ByteBuf in) {
        this.location.read(in);
        this.orbitNumbers.read(in);
    }

    @Override
    public void write(ByteBuf out) {
        this.location.write(out);
        this.orbitNumbers.write(out);

    }

    @Override
    public String toString() {
        return "CelestialCoordinate{" +
                "location=" + location +
                ", orbitNumbers=" + orbitNumbers +
                "} " + super.toString();
    }
}
