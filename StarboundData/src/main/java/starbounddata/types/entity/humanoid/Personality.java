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

package starbounddata.types.entity.humanoid;

import io.netty.buffer.ByteBuf;
import starbounddata.types.SbData;
import starbounddata.types.vectors.Vec2F;

public class Personality extends SbData<Personality>{

    private String idle;
    private String armIdle;
    private Vec2F headOffset;
    private Vec2F armOffset;

    public Personality() {
    }

    public Personality(String idle, String armIdle, Vec2F headOffset, Vec2F armOffset) {
        this.idle = idle;
        this.armIdle = armIdle;
        this.headOffset = headOffset;
        this.armOffset = armOffset;
    }

    public Personality(ByteBuf in){
        read(in);
    }

    public Personality(Personality personality){
        this.idle = personality.getIdle();
        this.armIdle = personality.getArmIdle();
        this.headOffset = personality.getHeadOffset();
        this.armOffset = personality.getArmOffset();
    }

    public String getIdle() {
        return idle;
    }

    public void setIdle(String idle) {
        this.idle = idle;
    }

    public String getArmIdle() {
        return armIdle;
    }

    public void setArmIdle(String armIdle) {
        this.armIdle = armIdle;
    }

    public Vec2F getHeadOffset() {
        return headOffset;
    }

    public void setHeadOffset(Vec2F headOffset) {
        this.headOffset = headOffset;
    }

    public Vec2F getArmOffset() {
        return armOffset;
    }

    public void setArmOffset(Vec2F armOffset) {
        this.armOffset = armOffset;
    }

    @Override
    public void read(ByteBuf in) {

    }

    @Override
    public void write(ByteBuf out) {

    }

    @Override
    public String toString() {
        return "Personality{" +
                "idle='" + idle + '\'' +
                ", armIdle='" + armIdle + '\'' +
                ", headOffset=" + headOffset +
                ", armOffset=" + armOffset +
                "} " + super.toString();
    }
}
