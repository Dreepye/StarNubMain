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

package starbounddata.types.entity;

import io.netty.buffer.ByteBuf;
import starbounddata.types.SbData;
import starbounddata.types.variants.VLQ;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 *
 * This is sometimes used over strait EntityId which uses just an Integer
 */
public class EntityVLQId extends SbData<EntityVLQId> {

    private long entityId;

    public EntityVLQId() {
    }

    public EntityVLQId(long entityId) {
        this.entityId = entityId;
    }

    public EntityVLQId(ByteBuf in) {
        read(in);
    }

    public EntityVLQId(EntityVLQId entityId) {
        this.entityId = entityId.getEntityId();
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    @Override
    public void read(ByteBuf in) {
        this.entityId = VLQ.readSignedFromBufferNoObject(in);
    }

    @Override
    public void write(ByteBuf out){
        out.writeBytes(VLQ.writeSignedVLQNoObject(entityId));
    }

    @Override
    public String toString() {
        return "EntityId{" +
                "entityId=" + entityId +
                '}';
    }
}
