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
public class CelestialConstellationList extends ArrayList<CelestialConstellation> implements SbDataInterface<CelestialConstellationList> {

    public CelestialConstellationList() {
        super();
    }

    public CelestialConstellationList(ByteBuf in) {
        read(in);
    }

    public CelestialConstellationList(CelestialConstellationList celestialConstellationsList) {
        for (CelestialConstellation celestialConstellation : celestialConstellationsList){
            CelestialConstellation celestialConstellationCopy = celestialConstellation.copy();
            this.add(celestialConstellationCopy);
        }
    }

    @Override
    public void read(ByteBuf in){
        long arrayLength = VLQ.readUnsignedFromBufferNoObject(in);
        for (int i = 0; i < arrayLength; i++) {
            CelestialConstellation  celestialConstellation = new CelestialConstellation();
            celestialConstellation.read(in);
            this.add(celestialConstellation);
        }
    }

    @Override
    public void write(ByteBuf out) {
        long size = (long) this.size();
        byte[] bytes = VLQ.writeUnsignedVLQNoObject(size);
        out.writeBytes(bytes);
        for (CelestialConstellation celestialConstellation : this) {
            celestialConstellation.write(out);
        }
        this.clear();
    }

    @Override
    public CelestialConstellationList copy(){
        return new CelestialConstellationList(this);
    }

    @Override
    public String toString() {
        return "CelestialRequestList{} " + super.toString();
    }
}
