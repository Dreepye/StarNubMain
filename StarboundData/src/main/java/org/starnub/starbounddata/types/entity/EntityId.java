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

package org.starnub.starbounddata.types.entity;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.types.SbData;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 *
 * This is sometimes used over strait EntityVLQId which uses a variant length quantity
 */
public class EntityId extends SbData<EntityId> {

    private long entityId;

    public EntityId() {
    }

    public EntityId(long entityId) {
        this.entityId = entityId;
    }

    public EntityId(ByteBuf in) {
        super(in);
    }

    public EntityId(EntityId entityId) {
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
        this.entityId = in.readInt();
    }

    @Override
    public void write(ByteBuf out){
        out.writeInt((int) entityId);
    }

    @Override
    public String toString() {
        return "EntityId{" +
                "entityId=" + entityId +
                '}';
    }
}
