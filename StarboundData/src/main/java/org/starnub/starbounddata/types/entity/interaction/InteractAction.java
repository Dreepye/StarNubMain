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

package org.starnub.starbounddata.types.entity.interaction;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.types.entity.EntityVLQId;
import org.starnub.starbounddata.types.variants.Variant;
import org.starnub.starbounddata.types.SbData;

public class InteractAction extends SbData<InteractAction> {

    private InteractActionType type;
    private EntityVLQId entityId = new EntityVLQId();
    private Variant data = new Variant();

    public InteractAction() {
    }

    public InteractAction(InteractActionType type, EntityVLQId entityId, Variant data) {
        this.type = type;
        this.entityId = entityId;
        this.data = data;
    }

    public InteractAction(ByteBuf in) {
        super(in);
    }

    public InteractAction(InteractAction interactAction) {
        this.type = interactAction.getType();
        this.entityId = interactAction.getEntityId().copy();
        this.data = interactAction.getData().copy();
    }

    public InteractActionType getType() {
        return type;
    }

    public void setType(InteractActionType type) {
        this.type = type;
    }

    public EntityVLQId getEntityId() {
        return entityId;
    }

    public void setEntityId(EntityVLQId entityId) {
        this.entityId = entityId;
    }

    public Variant getData() {
        return data;
    }

    public void setData(Variant data) {
        this.data = data;
    }

    @Override
    public void read(ByteBuf in) {
        this.type = InteractActionType.values()[in.readUnsignedByte()];
        this.entityId.read(in);
        this.data.read(in);
    }

    @Override
    public void write(ByteBuf out){
        out.writeByte(type.ordinal());
        this.entityId.write(out);
        this.data.write(out);
    }

    @Override
    public String toString() {
        return "InteractAction{" +
                "type=" + type +
                ", entityId=" + entityId +
                ", data=" + data +
                "} " + super.toString();
    }
}
