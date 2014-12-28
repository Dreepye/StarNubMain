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

package starbounddata.types.vectors;

import io.netty.buffer.ByteBuf;

public class Vec3I extends Vec2I {

    private int z;

    public Vec3I() {
    }

    public Vec3I(int x, int y, int z) {
        super(x, y);
        this.z = z;
    }

    public Vec3I(ByteBuf in) {
        super(in);
    }

    public Vec3I(Vec3I vec3I) {
        setX(vec3I.getX());
        setY(vec3I.getY());
        this.z = vec3I.getZ();

    }

    public Vec3I(float x, float y, float z){
        setX(Math.round(x));
        setY(Math.round(y));
        this.z = Math.round(z);
    }

//    public Vec3I(Vec3F vec3F) {
//        setX(Math.round(vec3F.getX()));
//        setY(Math.round(vec3F.getY()));
//        this.z =  Math.round(vec3f.getZ()));
//    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public void read(ByteBuf in) {
        super.read(in);
        this.z = in.readInt();
    }

    @Override
    public void write(ByteBuf out) {
        super.write(out);
        out.writeInt(this.z);
    }

    @Override
    public String toString() {
        return "Vec3I{" +
                "z=" + z +
                "} " + super.toString();
    }

}
