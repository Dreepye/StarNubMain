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

package starbounddata.types.celestial.response;

import io.netty.buffer.ByteBuf;
import starbounddata.types.SbData;
import starbounddata.types.vectors.Vec2I;

public class CelestialResponse extends SbData<CelestialResponse>{

    private CelestialResponseType celestialRequestType;
    private Vec2I vec2or3;

    public CelestialResponse() {
    }
    public CelestialResponse(ByteBuf in) {
        super(in);
    }

//    public CelestialResponse(Vec2I vec2or3) {
//        if (vec2or3 instanceof Vec3I){
//            celestialRequestType = CelestialRequestType.VEC3I;
//        } else {
//            celestialRequestType = CelestialRequestType.VEC2I;
//        }
//        this.vec2or3 = vec2or3.copy();
//    }



    @Override
    public void read(ByteBuf in) {
        this.celestialRequestType = CelestialResponseType.values()[in.readUnsignedByte()];

    }

    @Override
    public void write(ByteBuf out) {
        out.writeByte(celestialRequestType.ordinal());

    }

    @Override
    public String toString() {
        return "CelestialRequest{" +
                "celestialRequestType=" + celestialRequestType +
                ", vec2or3=" + vec2or3 +
                "} " + super.toString();
    }
}
