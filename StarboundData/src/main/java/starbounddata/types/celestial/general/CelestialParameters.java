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
import starbounddata.types.variants.VLQ;
import starbounddata.types.variants.Variant;

import static starbounddata.packets.Packet.readVLQString;
import static starbounddata.packets.Packet.writeStringVLQ;

public class CelestialParameters extends SbData<CelestialParameters> {

    private CelestialCoordinate coordinate = new CelestialCoordinate();
    private long seed;
    private String name;
    private Variant parameters = new Variant();
//    VisitableWorldParametersConstPtr m_visitableParameters //DEBUG

    public CelestialParameters() {
    }

    public CelestialParameters(CelestialCoordinate coordinate, long seed, String name, Variant parameters) {
        this.coordinate = coordinate;
        this.seed = seed;
        this.name = name;
        this.parameters = parameters;
    }

    public CelestialParameters(ByteBuf in) {
        super(in);
    }

    public CelestialParameters(CelestialParameters celestialParameters) {
        this.coordinate = celestialParameters.getCoordinate().copy();
        this.seed = celestialParameters.getSeed();
        this.name = celestialParameters.getName();
        this.parameters = celestialParameters.getParameters().copy();
    }

    public CelestialCoordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(CelestialCoordinate coordinate) {
        this.coordinate = coordinate;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Variant getParameters() {
        return parameters;
    }

    public void setParameters(Variant parameters) {
        this.parameters = parameters;
    }

    @Override
    public void read(ByteBuf in) {
        this.coordinate.read(in);
        this.seed = VLQ.readUnsignedFromBufferNoObject(in);//DEBUG
        this.name = readVLQString(in);
        this.parameters.read(in);
    }

    @Override
    public void write(ByteBuf out) {
        this.coordinate.write(out);
        byte[] bytes = VLQ.writeVLQNoObject(seed);
        out.writeBytes(bytes);
        writeStringVLQ(out, name);
        this.parameters.write(out);
    }

    @Override
    public String toString() {
        return "CelestialParameters{" +
                "coordinate=" + coordinate +
                ", seed=" + seed +
                ", name='" + name + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
