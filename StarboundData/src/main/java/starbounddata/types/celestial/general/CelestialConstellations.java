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
import starbounddata.types.SbDataInterface;
import starbounddata.types.variants.VLQ;

import java.util.ArrayList;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class CelestialConstellations extends ArrayList<CelestialConstellationList> implements SbDataInterface<CelestialConstellations> {

    public CelestialConstellations() {
        super();
    }

    public CelestialConstellations(ByteBuf in) {
        read(in);
    }

    public CelestialConstellations(CelestialConstellations celestialConstellations) {
        for (CelestialConstellationList celestialConstellationList : celestialConstellations){
            CelestialConstellationList celestialConstellationListCopy = celestialConstellationList.copy();
            this.add(celestialConstellationListCopy);
        }
    }

    @Override
    public void read(ByteBuf in){
        long arrayLength = VLQ.readUnsignedFromBufferNoObject(in);
        for (int i = 0; i < arrayLength; i++) {
            CelestialConstellationList  celestialConstellationList = new CelestialConstellationList();
            celestialConstellationList.read(in);
            this.add(celestialConstellationList);
        }
    }

    @Override
    public void write(ByteBuf out) {
        long size = (long) this.size();
        byte[] bytes = VLQ.writeVLQNoObject(size);
        out.writeBytes(bytes);
        for (CelestialConstellationList celestialConstellationList : this) {
            celestialConstellationList.write(out);
        }
        this.clear();
    }

    @Override
    public CelestialConstellations copy(){
        return new CelestialConstellations(this);
    }

    @Override
    public String toString() {
        return "CelestialConstellations{} " + super.toString();
    }
}
