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
import starbounddata.types.SbData;

import static starbounddata.packets.Packet.readVLQString;
import static starbounddata.packets.Packet.writeStringVLQ;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class EphemeralStatusEffect extends SbData<EphemeralStatusEffect>{

    private String uniqueStatusEffect;
    private float duration;

    public EphemeralStatusEffect() {
    }

    public EphemeralStatusEffect(ByteBuf in) {
        super(in);
    }

    public EphemeralStatusEffect(String uniqueStatusEffect, float duration) {
        this.uniqueStatusEffect = uniqueStatusEffect;
        this.duration = duration;
    }


    public EphemeralStatusEffect(EphemeralStatusEffect ephemeralStatusEffect) {
        this.uniqueStatusEffect = ephemeralStatusEffect.getUniqueStatusEffect();
        this.duration = ephemeralStatusEffect.getDuration();
    }

    public String getUniqueStatusEffect() {
        return uniqueStatusEffect;
    }

    public void setUniqueStatusEffect(String uniqueStatusEffect) {
        this.uniqueStatusEffect = uniqueStatusEffect;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    @Override
    public void read(ByteBuf in){
        this.uniqueStatusEffect = readVLQString(in);
        boolean hasDuration = in.readBoolean();
        if (hasDuration) {
            this.duration = in.readFloat();
        }
    }

    @Override
    public void write(ByteBuf out) {
        writeStringVLQ(out, uniqueStatusEffect);
        if (duration == 0){
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeFloat(duration);
        }
    }

    @Override
    public String toString() {
        return "EphemeralStatusEffect{" +
                "uniqueStatusEffect='" + uniqueStatusEffect + '\'' +
                ", duration=" + duration +
                '}';
    }
}
