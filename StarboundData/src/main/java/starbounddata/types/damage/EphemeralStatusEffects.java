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

import java.util.ArrayList;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class EphemeralStatusEffects extends ArrayList<EphemeralStatusEffect> {

    public EphemeralStatusEffects() {
        super();
    }

    /**
     * @param in ByteBuf data to be read into the Vec2I Array. 100 is set as a cap for data to prevent attacks against the starnubserver. This is still a sizable area
     */
    public EphemeralStatusEffects(ByteBuf in) throws ArrayIndexOutOfBoundsException {
        readEphemeralStatusEffects(in);
    }

    public EphemeralStatusEffects(EphemeralStatusEffects ephemeralStatusEffects) {
        for (EphemeralStatusEffect ephemeralStatusEffect : ephemeralStatusEffects){
            EphemeralStatusEffect ephemeralStatusEffectCopy = ephemeralStatusEffect.copy();
            this.add(ephemeralStatusEffectCopy);
        }
    }

    public void readEphemeralStatusEffects(ByteBuf in){
        long arrayLength = VLQ.readUnsignedFromBufferNoObject(in);
        for (int i = 0; i < arrayLength; i++) {
            EphemeralStatusEffect ephemeralStatusEffect = new EphemeralStatusEffect(in);
            this.add(ephemeralStatusEffect);
        }
    }

    /**
     * @param out ByteBuf out representing a {@link io.netty.buffer.ByteBuf} to write this Vec2IArray to
     */
    public void writeEphemeralStatusEffects(ByteBuf out) {
        out.writeBytes(VLQ.writeVLQNoObject((long) this.size()));
        for (EphemeralStatusEffect ephemeralStatusEffect : this) {
            ephemeralStatusEffect.writeEphemeralStatusEffect(out);
        }
        this.clear();
    }

    public EphemeralStatusEffects copy(){
        return new EphemeralStatusEffects(this);
    }

    @Override
    public String toString() {
        return "EphemeralStatusEffects{} " + super.toString();
    }
}
