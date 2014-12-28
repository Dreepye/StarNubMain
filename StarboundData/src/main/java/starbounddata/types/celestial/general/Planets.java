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

import java.util.HashMap;

public class Planets extends HashMap<Integer, CelestialPlanet> implements SbDataInterface<Planets> {

    public Planets() {
        super();
    }

    public Planets(ByteBuf in) {
        read(in);
    }

    public Planets(Planets systemObjects) {
        for (Entry<Integer, CelestialPlanet> entry : systemObjects.entrySet()){
            Integer key = entry.getKey();//DEBUG
            CelestialPlanet celestialPlanet = entry.getValue();
            CelestialPlanet celestialPlanetCopy = celestialPlanet.copy();
            this.put(key, celestialPlanetCopy);
        }
    }

    @Override
    public void read(ByteBuf in) {
        long mapLength = VLQ.readUnsignedFromBufferNoObject(in);
        for (int i = 0; i < mapLength; i++) {
            Integer key = in.readInt();//DEBUG
            CelestialPlanet celestialPlanet = new CelestialPlanet();
            celestialPlanet.read(in);
            this.put(key, celestialPlanet);
        }
    }

    @Override
    public void write(ByteBuf out) {
        long size = (long) this.size();
        byte[] bytes = VLQ.writeVLQNoObject(size);
        out.writeBytes(bytes);
        for (Entry<Integer, CelestialPlanet> entry : this.entrySet()){
            Integer key = entry.getKey();//DEBUG
            out.writeInt(key);
            CelestialPlanet celestialPlanet = entry.getValue();
            celestialPlanet.write(out);
        }
        this.clear();
    }

    @Override
    public Planets copy() {
        return new Planets(this);
    }

    @Override
    public String toString() {
        return "SystemObjects{} " + super.toString();
    }
}
