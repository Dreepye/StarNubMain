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
import starbounddata.types.SbData;

public class Vec4B extends SbData<Vec4B> {

    private byte byteOne;
    private byte byteTwo;
    private byte byteThree;
    private byte byteFour;

    public Vec4B() {
    }

    public Vec4B(byte byteOne, byte byteTwo, byte byteThree, byte byteFour) {
        this.byteOne = byteOne;
        this.byteTwo = byteTwo;
        this.byteThree = byteThree;
        this.byteFour = byteFour;
    }

    public Vec4B(ByteBuf in) {
        super(in);
    }

    public Vec4B(Vec4B vec4B) {
        this.byteOne = vec4B.getByteOne();
        this.byteTwo = vec4B.getByteTwo();
        this.byteThree = vec4B.getByteThree();
        this.byteFour = vec4B.getByteFour();
    }

    public byte getByteOne() {
        return byteOne;
    }

    public void setByteOne(byte byteOne) {
        this.byteOne = byteOne;
    }

    public byte getByteTwo() {
        return byteTwo;
    }

    public void setByteTwo(byte byteTwo) {
        this.byteTwo = byteTwo;
    }

    public byte getByteThree() {
        return byteThree;
    }

    public void setByteThree(byte byteThree) {
        this.byteThree = byteThree;
    }

    public byte getByteFour() {
        return byteFour;
    }

    public void setByteFour(byte byteFour) {
        this.byteFour = byteFour;
    }

    @Override
    public void read(ByteBuf in) {
        this.byteOne = (byte) in.readUnsignedByte();
        this.byteTwo =  (byte) in.readUnsignedByte();
        this.byteThree =  (byte) in.readUnsignedByte();
        this.byteFour =  (byte) in.readUnsignedByte();
    }

    @Override
    public void write(ByteBuf out) {
        out.writeByte(byteOne);
        out.writeByte(byteTwo);
        out.writeByte(byteThree);
        out.writeByte(byteFour);
    }

    @Override
    public String toString() {
        return "Vec4B{" +
                "byteOne=" + byteOne +
                ", byteTwo=" + byteTwo +
                ", byteThree=" + byteThree +
                ", byteFour=" + byteFour +
                "} " + super.toString();
    }
}
