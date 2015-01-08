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

package org.starnub.starbounddata.types.celestial.request;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.types.SbData;
import org.starnub.starbounddata.types.vectors.Vec2I;
import org.starnub.starbounddata.types.vectors.Vec3I;
import org.starnub.starbounddata.types.vectors.VecI;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 *
 * Usually VEC3I contain the planet x, y, z the person is on
 */
public class CelestialRequest extends SbData<CelestialRequest> {

    private CelestialRequestType celestialRequestType;
    private VecI vec2or3;

    public CelestialRequest() {
    }

    public CelestialRequest(CelestialRequestType celestialRequestType, VecI vec2or3) {
        this.celestialRequestType = celestialRequestType;
        this.vec2or3 = vec2or3;
    }

    public CelestialRequest(ByteBuf in) {
        super(in);
    }

    public CelestialRequest(VecI vec2or3) {
        if (vec2or3 instanceof Vec3I){
            celestialRequestType = CelestialRequestType.VEC3I;
        } else {
            celestialRequestType = CelestialRequestType.VEC2I;
        }
        this.vec2or3 = (VecI) this.vec2or3.copy();
    }

    public CelestialRequestType getCelestialRequestType() {
        return celestialRequestType;
    }

    public void setCelestialRequestType(CelestialRequestType celestialRequestType) {
        this.celestialRequestType = celestialRequestType;
    }

    public VecI getVec2or3() {
        return vec2or3;
    }

    public void setVec2or3(VecI vec2or3) {
        this.vec2or3 = vec2or3;
    }

    @Override
    public void read(ByteBuf in) {
        this.celestialRequestType = CelestialRequestType.values()[in.readUnsignedByte()];
        switch (celestialRequestType){
            case VEC2I: {
                Vec2I vec2I = new Vec2I();
                vec2I.read(in);
                vec2or3 = vec2I;
                break;
            }
            case VEC3I: {
                Vec3I vec3I = new Vec3I();
                vec3I.read(in);
                vec2or3 = vec3I;
                break;
            }
        }
    }

    @Override
    public void write(ByteBuf out) {
        out.writeByte(celestialRequestType.ordinal());
        vec2or3.write(out);
    }

    @Override
    public String toString() {
        return "CelestialRequest{" +
                "celestialRequestType=" + celestialRequestType +
                ", vec2or3=" + vec2or3 +
                "} " + super.toString();
    }
}
