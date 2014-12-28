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

public class CelestialConstellation extends SbData<CelestialConstellation>{

    private Vec2I vec2I1 = new Vec2I();
    private Vec2I vec2I2 = new Vec2I();

    public CelestialConstellation() {
    }

    public CelestialConstellation(Vec2I vec2I1, Vec2I vec2I2) {
        this.vec2I1 = vec2I1;
        this.vec2I2 = vec2I2;
    }

    public CelestialConstellation(ByteBuf in) {
        super(in);
    }

    public CelestialConstellation(CelestialConstellation celestialConstellation) {
        this.vec2I1 = celestialConstellation.getVec2I1();
        this.vec2I2 = celestialConstellation.getVec2I2();
    }

    public Vec2I getVec2I1() {
        return vec2I1;
    }

    public void setVec2I1(Vec2I vec2I1) {
        this.vec2I1 = vec2I1;
    }

    public Vec2I getVec2I2() {
        return vec2I2;
    }

    public void setVec2I2(Vec2I vec2I2) {
        this.vec2I2 = vec2I2;
    }

    @Override
    public void read(ByteBuf in) {
        this.vec2I1.read(in);
        this.vec2I2.read(in);
    }

    @Override
    public void write(ByteBuf out) {
        this.vec2I1.write(out);
        this.vec2I2.write(out);
    }

    @Override
    public String toString() {
        return "CelestialConstellation{" +
                "vec2I1=" + vec2I1 +
                ", vec2I2=" + vec2I2 +
                "} " + super.toString();
    }
}
