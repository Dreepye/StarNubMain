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
import starbounddata.types.vectors.Vec3I;

import java.util.HashMap;

public class SystemParameters extends HashMap<Vec3I, CelestialParameters> implements SbDataInterface<SystemParameters> {

    public SystemParameters() {
        super();
    }

    public SystemParameters(ByteBuf in) {
        read(in);
    }

    public SystemParameters(SystemParameters systemParameters) {
        for (Entry<Vec3I, CelestialParameters> entry : systemParameters.entrySet()){
            Vec3I key = entry.getKey();
            Vec3I keyCopy = key.copy();
            CelestialParameters celestialParameters = entry.getValue();
            CelestialParameters celestialParametersCopy = celestialParameters.copy();
            this.put(keyCopy, celestialParametersCopy);
        }
    }

    @Override
    public void read(ByteBuf in) {
        long mapLength = VLQ.readUnsignedFromBufferNoObject(in);
        for (int i = 0; i < mapLength; i++) {
            Vec3I key = new Vec3I();
            key.read(in);
            CelestialParameters celestialParameters = new CelestialParameters();
            celestialParameters.read(in);
            this.put(key, celestialParameters);
        }
    }

    @Override
    public void write(ByteBuf out) {
        long size = (long) this.size();
        byte[] bytes = VLQ.writeUnsignedVLQNoObject(size);
        out.writeBytes(bytes);
        for (Entry<Vec3I, CelestialParameters> entry : this.entrySet()){
            Vec3I key = entry.getKey();
            key.write(out);
            CelestialParameters celestialParameters = entry.getValue();
            celestialParameters.write(out);
        }
        this.clear();
    }

    @Override
    public SystemParameters copy() {
        return new SystemParameters(this);
    }

    @Override
    public String toString() {
        return "SystemParameters{} " + super.toString();
    }
}
