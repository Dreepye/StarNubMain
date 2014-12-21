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
import starbounddata.types.vectors.Vec2I;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class CelestialBaseInformation {

    private int planetOrbitalLevels;
    private int satelliteOrbitalLevels;
    private int chunkSize;
    private Vec2I xYCoordinate = new Vec2I();
    private Vec2I  zCoordinateRange = new Vec2I();

    public CelestialBaseInformation(int planetOrbitalLevels, int satelliteOrbitalLevels, int chunkSize, Vec2I xYCoordinate, Vec2I zCoordinateRange) {
        this.planetOrbitalLevels = planetOrbitalLevels;
        this.satelliteOrbitalLevels = satelliteOrbitalLevels;
        this.chunkSize = chunkSize;
        this.xYCoordinate = xYCoordinate;
        this.zCoordinateRange = zCoordinateRange;
    }


    public CelestialBaseInformation(ByteBuf in) {
        readCelestialBaseInformation(in);
    }

    public CelestialBaseInformation(CelestialBaseInformation celestialBaseInformation) {
        this.planetOrbitalLevels = celestialBaseInformation.getPlanetOrbitalLevels();
        this.satelliteOrbitalLevels = celestialBaseInformation.getSatelliteOrbitalLevels();
        this.chunkSize = celestialBaseInformation.getChunkSize();
        this.xYCoordinate = celestialBaseInformation.xYCoordinate.copy();
        this.zCoordinateRange = celestialBaseInformation.zCoordinateRange.copy();
    }


    public void readCelestialBaseInformation(ByteBuf in) {
        this.planetOrbitalLevels = in.readInt();
        this.satelliteOrbitalLevels = in.readInt();
        this.chunkSize = in.readInt();
        this.xYCoordinate.readVec2I(in);
        this.zCoordinateRange.readVec2I(in);
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

    /**
     * @param out ByteBuf out representing a {@link io.netty.buffer.ByteBuf} to write this Vec2I to
     */
    public void writeCelestialBaseInformation(ByteBuf out) {
        out.writeInt(planetOrbitalLevels);
        out.writeInt(satelliteOrbitalLevels);
        out.writeInt(chunkSize);
        this.xYCoordinate.writeVec2I(out);
        this.zCoordinateRange.writeVec2I(out);
    }

    public CelestialBaseInformation copy(){
        return new CelestialBaseInformation(this);
    }

    @Override
    public String toString() {
        return "CelestialBaseInformation{" +
                "planetOrbitalLevels=" + planetOrbitalLevels +
                ", satelliteOrbitalLevels=" + satelliteOrbitalLevels +
                ", chunkSize=" + chunkSize +
                ", xYCoordinate=" + xYCoordinate +
                ", zCoordinateRange=" + zCoordinateRange +
                '}';
    }

}
