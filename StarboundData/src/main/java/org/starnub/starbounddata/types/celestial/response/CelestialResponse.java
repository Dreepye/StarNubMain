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

package org.starnub.starbounddata.types.celestial.response;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.types.celestial.general.Celestial;
import org.starnub.starbounddata.types.celestial.general.CelestialChunk;
import org.starnub.starbounddata.types.celestial.general.CelestialSystemObjects;
import org.starnub.starbounddata.types.SbData;

public class CelestialResponse extends SbData<CelestialResponse>{

    private CelestialResponseType celestialRequestType;
    private Celestial celestial;

    public CelestialResponse() {
    }

    public CelestialResponse(CelestialResponseType celestialRequestType, Celestial celestial) {
        this.celestialRequestType = celestialRequestType;
        this.celestial = celestial;
    }

    public CelestialResponse(ByteBuf in) {
        super(in);
    }

    public CelestialResponse(Celestial celestial) {
        if (celestial instanceof CelestialChunk){
            celestialRequestType = CelestialResponseType.CELESTIAL_CHUNK;
        } else if (celestial instanceof CelestialSystemObjects){
            celestialRequestType = CelestialResponseType.CELESTIAL_SYSTEM_OBJECTS;
        }
        this.celestial = (Celestial) celestial.copy();
    }

    @Override
    public void read(ByteBuf in) {
        this.celestialRequestType = CelestialResponseType.values()[in.readUnsignedByte()];
        switch (celestialRequestType){
            case CELESTIAL_CHUNK: {
                CelestialChunk celestialChunk = new CelestialChunk();
                celestialChunk.read(in);
                this.celestial = celestialChunk;
                break;
            }
            case CELESTIAL_SYSTEM_OBJECTS: {
                CelestialSystemObjects celestialSystemObjects = new CelestialSystemObjects();
                celestialSystemObjects.read(in);
                this.celestial = celestialSystemObjects;
                break;
            }
        }
    }

    @Override
    public void write(ByteBuf out) {
        out.writeByte(celestialRequestType.ordinal());
        this.celestial.write(out);
    }

    @Override
    public String toString() {
        return "CelestialResponse{" +
                "celestialRequestType=" + celestialRequestType +
                ", celestial=" + celestial +
                "} " + super.toString();
    }
}
