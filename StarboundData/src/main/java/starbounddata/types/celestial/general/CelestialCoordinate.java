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
import starbounddata.types.planet.OrbitalLevels;
import starbounddata.types.vectors.Vec3I;

public class CelestialCoordinate extends SbData<CelestialCoordinate>{

    private Vec3I location = new Vec3I();
    private OrbitalLevels orbitalLevels = new OrbitalLevels();

    public CelestialCoordinate() {
    }

    public CelestialCoordinate(Vec3I location, OrbitalLevels orbitalLevels) {
        this.location = location;
        this.orbitalLevels = orbitalLevels;
    }

    public CelestialCoordinate(ByteBuf in) {
        super(in);
    }

    public CelestialCoordinate(CelestialCoordinate celestialCoordinate) {
        this.location = celestialCoordinate.getLocation().copy();
        this.orbitalLevels = celestialCoordinate.getOrbitalLevels().copy();
    }

    public Vec3I getLocation() {
        return location;
    }

    public void setLocation(Vec3I location) {
        this.location = location;
    }

    public OrbitalLevels getOrbitalLevels() {
        return orbitalLevels;
    }

    public void setOrbitalLevels(OrbitalLevels orbitalLevels) {
        this.orbitalLevels = orbitalLevels;
    }

    @Override
    public void read(ByteBuf in) {
        this.location.read(in);
        this.orbitalLevels.read(in);
    }

    @Override
    public void write(ByteBuf out) {
        this.location.write(out);
        this.orbitalLevels.write(out);

    }

    @Override
    public String toString() {
        return "CelestialCoordinate{" +
                "location=" + location +
                ", orbitalLevels=" + orbitalLevels +
                "} " + super.toString();
    }
}
