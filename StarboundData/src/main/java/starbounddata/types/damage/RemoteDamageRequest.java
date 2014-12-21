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
import starbounddata.types.variants.VLQ;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class RemoteDamageRequest {

    private long causingEntityId;
    private long targetEntityId;
    private DamageRequest damageRequest = new DamageRequest();

    public RemoteDamageRequest() {
    }

    public RemoteDamageRequest(RemoteDamageRequest remoteDamageRequest) {
        this.causingEntityId = remoteDamageRequest.getCausingEntityId();
        this.targetEntityId = remoteDamageRequest.getTargetEntityId();
        this.damageRequest = remoteDamageRequest.getDamageRequest().copy();
    }

    public RemoteDamageRequest(int causingEntityId, int targetEntityId, DamageRequest damageRequest) {
        this.causingEntityId = causingEntityId;
        this.targetEntityId = targetEntityId;
        this.damageRequest = damageRequest;
    }

    public void readRemoteDamageRequest(ByteBuf in) {
        this.causingEntityId = VLQ.readSignedFromBufferNoObject(in);
        this.targetEntityId = VLQ.readSignedFromBufferNoObject(in);
        this.damageRequest.readDamageRequest(in);
    }

    public long getCausingEntityId() {
        return causingEntityId;
    }

    public void setCausingEntityId(int causingEntityId) {
        this.causingEntityId = causingEntityId;
    }

    public long getTargetEntityId() {
        return targetEntityId;
    }

    public void setTargetEntityId(int targetEntityId) {
        this.targetEntityId = targetEntityId;
    }

    public DamageRequest getDamageRequest() {
        return damageRequest;
    }

    public void setDamageRequest(DamageRequest damageRequest) {
        this.damageRequest = damageRequest;
    }

    public void writeRemoteDamageRequest(ByteBuf out){
        out.writeBytes(VLQ.writeSignedVLQNoObject(causingEntityId));
        out.writeBytes(VLQ.writeSignedVLQNoObject(targetEntityId));
        this.damageRequest.writeDamageRequest(out);
    }

    public RemoteDamageRequest copy(){
        return new RemoteDamageRequest(this);
    }

    @Override
    public String toString() {
        return "RemoteDamageRequest{" +
                "causingEntityId=" + causingEntityId +
                ", targetEntityId=" + targetEntityId +
                ", damageRequest=" + damageRequest +
                '}';
    }
}
