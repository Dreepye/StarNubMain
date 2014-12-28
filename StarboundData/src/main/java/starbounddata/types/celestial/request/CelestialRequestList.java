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

package starbounddata.types.celestial.request;

import io.netty.buffer.ByteBuf;
import starbounddata.types.SbDataInterface;
import starbounddata.types.variants.VLQ;

import java.util.ArrayList;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class CelestialRequestList extends ArrayList<CelestialRequest> implements SbDataInterface<CelestialRequestList> {

    public CelestialRequestList() {
        super();
    }

    public CelestialRequestList(ByteBuf in) {
        read(in);
    }

    public CelestialRequestList(CelestialRequestList celestialRequestList) {
        for (CelestialRequest celestialRequest : celestialRequestList){
            CelestialRequest celestialRequestCopy = celestialRequest.copy();
            this.add(celestialRequestCopy);
        }
    }

    @Override
    public void read(ByteBuf in){
        long arrayLength = VLQ.readUnsignedFromBufferNoObject(in);
        for (int i = 0; i < arrayLength; i++) {
            CelestialRequest celestialRequest = new CelestialRequest();
            celestialRequest.read(in);
            this.add(celestialRequest);
        }
    }

    @Override
    public void write(ByteBuf out) {
        long size = (long) this.size();
        byte[] bytes = VLQ.writeVLQNoObject(size);
        out.writeBytes(bytes);
        for (CelestialRequest celestialRequest : this) {
            celestialRequest.write(out);
        }
        this.clear();
    }

    @Override
    public CelestialRequestList copy(){
        return new CelestialRequestList(this);
    }

    @Override
    public String toString() {
        return "CelestialRequestList{} " + super.toString();
    }
}
