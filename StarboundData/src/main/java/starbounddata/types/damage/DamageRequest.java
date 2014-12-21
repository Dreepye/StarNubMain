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

package starbounddata.types.damage;

import io.netty.buffer.ByteBuf;
import starbounddata.packets.Packet;
import starbounddata.types.variants.VLQ;
import starbounddata.types.vectors.Vec2F;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class DamageRequest {

    private HitType hitType;
    private DamageType damageType;
    private float damage;
    private Vec2F knockbackMomentum = new Vec2F();
    private long sourceEntityId;
    private String damageSourceKind;
    private EphemeralStatusEffects statusEffects = new EphemeralStatusEffects();

    public DamageRequest() {
    }

    public DamageRequest(HitType hitType, DamageType damageType, float damage, Vec2F knockbackMomentum, int sourceEntityId, String damageSourceKind, EphemeralStatusEffects statusEffects) {
        this.hitType = hitType;
        this.damageType = damageType;
        this.damage = damage;
        this.knockbackMomentum = knockbackMomentum;
        this.sourceEntityId = sourceEntityId;
        this.damageSourceKind = damageSourceKind;
        this.statusEffects = statusEffects;
    }

    public DamageRequest(ByteBuf in) {
        readDamageRequest(in);
    }

    public DamageRequest(DamageRequest damageRequest) {
        this.hitType = damageRequest.getHitType();
        this.damageType = damageRequest.getDamageType();
        this.damage = damageRequest.getDamage();
        this.knockbackMomentum = damageRequest.getKnockbackMomentum().copy();
        this.sourceEntityId = damageRequest.getSourceEntityId();
        this.damageSourceKind = damageRequest.getDamageSourceKind();
        this.statusEffects = damageRequest.getStatusEffects().copy();
    }

    public void readDamageRequest(ByteBuf in){
        this.hitType = HitType.values()[in.readUnsignedByte()];
        this.damageType = DamageType.values()[in.readUnsignedByte()];
        this.damage = in.readFloat();
        this.knockbackMomentum.readVec2F(in);
        this.sourceEntityId = VLQ.readUnsignedFromBufferNoObject(in);
        this.damageSourceKind = Packet.readVLQString(in);
        this.statusEffects.readEphemeralStatusEffects(in);
    }

    public HitType getHitType() {
        return hitType;
    }

    public void setHitType(HitType hitType) {
        this.hitType = hitType;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public Vec2F getKnockbackMomentum() {
        return knockbackMomentum;
    }

    public void setKnockbackMomentum(Vec2F knockbackMomentum) {
        this.knockbackMomentum = knockbackMomentum;
    }

    public long getSourceEntityId() {
        return sourceEntityId;
    }

    public void setSourceEntityId(int sourceEntityId) {
        this.sourceEntityId = sourceEntityId;
    }

    public String getDamageSourceKind() {
        return damageSourceKind;
    }

    public void setDamageSourceKind(String damageSourceKind) {
        this.damageSourceKind = damageSourceKind;
    }

    public EphemeralStatusEffects getStatusEffects() {
        return statusEffects;
    }

    public void setStatusEffects(EphemeralStatusEffects statusEffects) {
        this.statusEffects = statusEffects;
    }

    public DamageRequest copy(){
        return new DamageRequest(this);
    }

    /**
     * @param out ByteBuf out representing a {@link io.netty.buffer.ByteBuf} to write this TileDamage to
     */
    public void writeDamageRequest(ByteBuf out) {
        out.writeByte(hitType.ordinal());
        out.writeByte(damageType.ordinal());
        out.writeFloat(damage);
        knockbackMomentum.writeVec2F(out);
        out.writeBytes(VLQ.writeSignedVLQNoObject(sourceEntityId));
        Packet.writeStringVLQ(out, damageSourceKind);
        statusEffects.writeEphemeralStatusEffects(out);
    }

    @Override
    public String toString() {
        return "DamageRequest{" +
                "hitType=" + hitType +
                ", damageType=" + damageType +
                ", damage=" + damage +
                ", knockbackMomentum=" + knockbackMomentum +
                ", sourceEntityId=" + sourceEntityId +
                ", damageSourceKind='" + damageSourceKind + '\'' +
                ", statusEffects=" + statusEffects +
                '}';
    }
}
