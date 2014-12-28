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
import starbounddata.types.vectors.Vec2I;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class CelestialBaseInformation extends SbData<CelestialBaseInformation> {

    private OrbitalLevels orbitalLevels;
    private int chunkSize;
    private Vec2I xYCoordinate = new Vec2I();
    private Vec2I  zCoordinateRange = new Vec2I();

    public CelestialBaseInformation() {
        super();
    }

    public CelestialBaseInformation(OrbitalLevels orbitalLevels, int chunkSize, Vec2I xYCoordinate, Vec2I zCoordinateRange) {
        this.orbitalLevels = orbitalLevels;
        this.chunkSize = chunkSize;
        this.xYCoordinate = xYCoordinate;
        this.zCoordinateRange = zCoordinateRange;
    }


    public CelestialBaseInformation(ByteBuf in) {
        super(in);
    }

    public CelestialBaseInformation(CelestialBaseInformation celestialBaseInformation) {
        this.orbitalLevels = celestialBaseInformation.getOrbitalLevels();
        this.chunkSize = celestialBaseInformation.getChunkSize();
        this.xYCoordinate = celestialBaseInformation.xYCoordinate.copy();
        this.zCoordinateRange = celestialBaseInformation.zCoordinateRange.copy();
    }

    public OrbitalLevels getOrbitalLevels() {
        return orbitalLevels;
    }

    public void setOrbitalLevels(OrbitalLevels orbitalLevels) {
        this.orbitalLevels = orbitalLevels;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Vec2I getxYCoordinate() {
        return xYCoordinate;
    }

    public void setxYCoordinate(Vec2I xYCoordinate) {
        this.xYCoordinate = xYCoordinate;
    }

    public Vec2I getzCoordinateRange() {
        return zCoordinateRange;
    }

    public void setzCoordinateRange(Vec2I zCoordinateRange) {
        this.zCoordinateRange = zCoordinateRange;
    }

    @Override
    public void read(ByteBuf in) {
        this.orbitalLevels.read(in);
        this.chunkSize = in.readInt();
        this.xYCoordinate.read(in);
        this.zCoordinateRange.read(in);
    }

    @Override
    public void write(ByteBuf out) {
        this.orbitalLevels.write(out);
        out.writeInt(chunkSize);
        this.xYCoordinate.write(out);
        this.zCoordinateRange.write(out);
    }

    @Override
    public String toString() {
        return "CelestialBaseInformation{" +
                "orbitalLevels=" + orbitalLevels +
                ", chunkSize=" + chunkSize +
                ", xYCoordinate=" + xYCoordinate +
                ", zCoordinateRange=" + zCoordinateRange +
                "} " + super.toString();
    }
}
