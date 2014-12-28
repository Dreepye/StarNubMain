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

public class SatelliteParameters extends HashMap<Integer, CelestialParameters> implements SbDataInterface<SatelliteParameters> {

    public SatelliteParameters() {
        super();
    }

    public SatelliteParameters(ByteBuf in) {
        read(in);
    }

    public SatelliteParameters(SatelliteParameters satelliteParameters) {
        for (Entry<Integer, CelestialParameters> entry : satelliteParameters.entrySet()){
            Integer key = entry.getKey();
            CelestialParameters celestialParameters = entry.getValue();
            CelestialParameters celestialParametersCopy = celestialParameters.copy();
            this.put(key, celestialParametersCopy);
        }
    }

    @Override
    public void read(ByteBuf in) {
        long mapLength = VLQ.readUnsignedFromBufferNoObject(in);
        for (int i = 0; i < mapLength; i++) {
            Integer key = in.readInt();//DEBUG
            CelestialParameters celestialParameters = new CelestialParameters();
            celestialParameters.read(in);
            this.put(key, celestialParameters);
        }
    }

    @Override
    public void write(ByteBuf out) {
        long size = (long) this.size();
        byte[] bytes = VLQ.writeVLQNoObject(size);
        out.writeBytes(bytes);
        for (Entry<Integer, CelestialParameters> entry : this.entrySet()){
            Integer key = entry.getKey();//DEBUG
            out.writeInt(key);
            CelestialParameters celestialParameters = entry.getValue();
            celestialParameters.write(out);
        }
        this.clear();
    }

    @Override
    public SatelliteParameters copy() {
        return new SatelliteParameters(this);
    }

    @Override
    public String toString() {
        return "SatelliteParameters{} " + super.toString();
    }
}
