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
import org.starnub.starbounddata.types.vectors.Vec2F;
import org.starnub.starbounddata.types.entity.EntityId;
import org.starnub.starbounddata.types.SbData;

import static org.starnub.starbounddata.packets.Packet.readVLQString;
import static org.starnub.starbounddata.packets.Packet.writeStringVLQ;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1) // DEBUG NOT WORKING PROBABLE DOUBLE POINT
 */
public class DamageNotification extends SbData<DamageNotification> {

    private EntityId sourceEntityId = new EntityId();
    private EntityId targetEntityId = new EntityId();
    private Vec2F position = new Vec2F();
    private float damage;
    private String damageSourceKind;
    private String targetMaterialKind;
    private boolean killed;

    public DamageNotification() {
    }

    public DamageNotification(EntityId sourceEntityId, EntityId targetEntityId, Vec2F position, float damage, String damageSourceKind, String targetMaterialKind, boolean killed) {
        this.sourceEntityId = sourceEntityId;
        this.targetEntityId = targetEntityId;
        this.position = position;
        this.damage = damage;
        this.damageSourceKind = damageSourceKind;
        this.targetMaterialKind = targetMaterialKind;
        this.killed = killed;
    }

    public DamageNotification(ByteBuf in) {
        super(in);
    }

    public DamageNotification(DamageNotification damageNotification) {
        this.sourceEntityId = damageNotification.getSourceEntityId().copy();
        this.targetEntityId = damageNotification.getTargetEntityId().copy();
        this.position = damageNotification.getPosition().copy();
        this.damage = damageNotification.getDamage();
        this.damageSourceKind = damageNotification.getDamageSourceKind();
        this.targetMaterialKind = damageNotification.getTargetMaterialKind();
        this.killed = damageNotification.isKilled();
    }

    public EntityId getSourceEntityId() {
        return sourceEntityId;
    }

    public void setSourceEntityId(EntityId sourceEntityId) {
        this.sourceEntityId = sourceEntityId;
    }

    public EntityId getTargetEntityId() {
        return targetEntityId;
    }

    public void setTargetEntityId(EntityId targetEntityId) {
        this.targetEntityId = targetEntityId;
    }

    public Vec2F getPosition() {
        return position;
    }

    public void setPosition(Vec2F position) {
        this.position = position;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public String getDamageSourceKind() {
        return damageSourceKind;
    }

    public void setDamageSourceKind(String damageSourceKind) {
        this.damageSourceKind = damageSourceKind;
    }

    public String getTargetMaterialKind() {
        return targetMaterialKind;
    }

    public void setTargetMaterialKind(String targetMaterialKind) {
        this.targetMaterialKind = targetMaterialKind;
    }

    public boolean isKilled() {
        return killed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    @Override
    public void read(ByteBuf in) {
        this.sourceEntityId.read(in);
        this.targetEntityId.read(in);
        this.position.read(in);
        this.damage = in.readFloat();
        this.damageSourceKind = readVLQString(in);
        this.targetMaterialKind = readVLQString(in);
        this.killed = in.readBoolean();
    }

    @Override
    public void write(ByteBuf out) {
        sourceEntityId.write(out);
        targetEntityId.write(out);
        position.write(out);
        out.writeFloat(damage);
        writeStringVLQ(out, damageSourceKind);
        writeStringVLQ(out, targetMaterialKind);
        out.writeBoolean(killed);
    }

    @Override
    public String toString() {
        return "DamageNotification{" +
                "sourceEntityId=" + sourceEntityId +
                ", targetEntityId=" + targetEntityId +
                ", position=" + position +
                ", damage=" + damage +
                ", damageSourceKind='" + damageSourceKind + '\'' +
                ", targetMaterialKind='" + targetMaterialKind + '\'' +
                ", killed=" + killed +
                '}';
    }
}
