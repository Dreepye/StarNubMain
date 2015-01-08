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

package org.starnub.starbounddata.types.damage;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.types.entity.EntityId;
import org.starnub.starbounddata.types.SbData;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1) //DEBUG NOT WORKING PROBABLE DOUBLE POINT
 */
public class RemoteDamageNotification extends SbData<RemoteDamageNotification> {

    private EntityId sourceEntityId = new EntityId();
    private DamageNotification damageNotification = new DamageNotification();

    public RemoteDamageNotification() {
    }

    public RemoteDamageNotification(EntityId sourceEntityId, DamageNotification damageNotification) {
        this.sourceEntityId = sourceEntityId;
        this.damageNotification = damageNotification;
    }

    public RemoteDamageNotification(ByteBuf in){
        super(in);
    }

    public RemoteDamageNotification(RemoteDamageNotification remoteDamageRequest) {
        this.sourceEntityId = remoteDamageRequest.getSourceEntityId().copy();
        this.damageNotification = remoteDamageRequest.getDamageNotification().copy();
    }

    public EntityId getSourceEntityId() {
        return sourceEntityId;
    }

    public void setSourceEntityId(EntityId sourceEntityId) {
        this.sourceEntityId = sourceEntityId;
    }

    public DamageNotification getDamageNotification() {
        return damageNotification;
    }

    public void setDamageNotification(DamageNotification damageNotification) {
        this.damageNotification = damageNotification;
    }

    @Override
    public void read(ByteBuf in) {
        this.sourceEntityId.read(in);
        this.damageNotification.read(in);
    }

    @Override
    public void write(ByteBuf out){
        this.sourceEntityId.write(out);
        this.damageNotification.write(out);
    }

    @Override
    public String toString() {
        return "RemoteDamageNotification{" +
                "sourceEntityId=" + sourceEntityId +
                ", damageNotification=" + damageNotification +
                '}';
    }
}
