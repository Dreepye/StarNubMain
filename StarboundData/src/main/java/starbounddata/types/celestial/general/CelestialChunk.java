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
import starbounddata.types.vectors.Vec2I;

public class CelestialChunk extends SbData<CelestialChunk> {

    private Vec2I chunkIndex = new Vec2I();
    private CelestialConstellationList constellations = new CelestialConstellationList();
    private SystemParameters systemParameters = new SystemParameters();
    private SystemObjects systemObjects = new SystemObjects();

    public CelestialChunk() {
    }

    public CelestialChunk(Vec2I chunkIndex, CelestialConstellationList constellations, SystemParameters systemParameters, SystemObjects systemObjects) {
        this.chunkIndex = chunkIndex;
        this.constellations = constellations;
        this.systemParameters = systemParameters;
        this.systemObjects = systemObjects;
    }

    public CelestialChunk(ByteBuf in) {
        super(in);
    }

    public CelestialChunk(CelestialChunk celestialChunk) {
        this.chunkIndex = celestialChunk.getChunkIndex().copy();
        this.constellations = celestialChunk.getConstellations().copy();
        this.systemParameters = celestialChunk.getSystemParameters().copy();
        this.systemObjects = celestialChunk.getSystemObjects().copy();
    }

    public Vec2I getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Vec2I chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public CelestialConstellationList getConstellations() {
        return constellations;
    }

    public void setConstellations(CelestialConstellationList constellations) {
        this.constellations = constellations;
    }

    public SystemParameters getSystemParameters() {
        return systemParameters;
    }

    public void setSystemParameters(SystemParameters systemParameters) {
        this.systemParameters = systemParameters;
    }

    public SystemObjects getSystemObjects() {
        return systemObjects;
    }

    public void setSystemObjects(SystemObjects systemObjects) {
        this.systemObjects = systemObjects;
    }

    @Override
    public void read(ByteBuf in) {

    }

    @Override
    public void write(ByteBuf out) {

    }

    @Override
    public String toString() {
        return "CelestialChunk{" +
                "chunkIndex=" + chunkIndex +
                ", constellations=" + constellations +
                ", systemParameters=" + systemParameters +
                ", systemObjects=" + systemObjects +
                "} " + super.toString();
    }
}
