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

package starbounddata.types.hit;

import io.netty.buffer.ByteBuf;
import starbounddata.types.entity.EntityId;
import starbounddata.types.SbData;

/**
 * Starbound 1.0 Compliant (Versions 622)
 */
public class RemoteHitRequest extends SbData<RemoteHitRequest> {

    private EntityId causingEntityId = new EntityId();
    private EntityId targetEntityId =  new EntityId();

    public RemoteHitRequest() {
    }

    public RemoteHitRequest(EntityId causingEntityId, EntityId targetEntityId) {
        this.causingEntityId = causingEntityId;
        this.targetEntityId = targetEntityId;
    }

    public RemoteHitRequest(ByteBuf in) {
        super(in);
    }

    public RemoteHitRequest(RemoteHitRequest remoteHitRequest) {
        this.causingEntityId = remoteHitRequest.getCausingEntityId().copy();
        this.targetEntityId = remoteHitRequest.getTargetEntityId().copy();
    }

    public EntityId getCausingEntityId() {
        return causingEntityId;
    }

    public void setCausingEntityId(EntityId causingEntityId) {
        this.causingEntityId = causingEntityId;
    }

    public EntityId getTargetEntityId() {
        return targetEntityId;
    }

    public void setTargetEntityId(EntityId targetEntityId) {
        this.targetEntityId = targetEntityId;
    }

    @Override
    public void read(ByteBuf in) {
        this.causingEntityId.read(in);
        this.targetEntityId.read(in);
    }

    @Override
    public void write(ByteBuf out) {
        this.causingEntityId.write(out);
        this.targetEntityId.write(out);
    }

    @Override
    public String toString() {
        return "RemoteHitRequest{" +
                "causingEntityId=" + causingEntityId +
                ", targetEntityId=" + targetEntityId +
                '}';
    }
}
