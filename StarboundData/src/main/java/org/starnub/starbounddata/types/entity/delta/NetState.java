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

package org.starnub.starbounddata.types.entity.delta;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.types.vectors.Vec2F;
import org.starnub.starbounddata.types.SbData;

import static org.starnub.starbounddata.packets.Packet.readVLQString;

public class NetState extends SbData<NetState>{

    private NetStateType netStateType;
    private Object netField;

    public NetState() {
    }

    public NetState(ByteBuf in) {
        read(in);
    }

    public NetState(Object netField) throws Exception {

    }

    public NetStateType getNetStateType() {
        return netStateType;
    }

    public void setNetStateType(NetStateType netStateType) {
        this.netStateType = netStateType;
    }

    public Object getNetField() {
        return netField;
    }

    public void setNetField(Object netField) {
        this.netField = netField;
    }

    @Override
    public void read(ByteBuf in) {
        this.netStateType = NetStateType.values()[in.readUnsignedByte()];
        switch (netStateType) {
            case STATE_STATE: {
                netField = null;
                break;
            }
            case SHIFT_STATE: {
                netField = null;
                break;
            }
            case LOUNGE_IN: {

                break;
            }
            case X_AIM_POSITION: {

                break;
            }
            case Y_AIM_POSITION: {

                break;
            }
            case IDENTITY: {

                break;
            }
            case TEAM: {

                break;
            }
            case LANDED: {

                break;
            }
            case TELEPORT_UP: {
                netField = new Vec2F(in);
                break;
            }
            case TELEPORT_DOWN: {

                break;
            }
            case CHAT_MESSAGE: {
                netField = readVLQString(in);
                break;
            }
            case NEW_CHAT_MESSAGE: {

                break;
            }
            case EMOTE: {
                netField = readVLQString(in);
                break;
            }
            case MOVEMENT_CONTROLLER: {

                break;
            }
            case ARMOR: {

                break;
            }
            case TOOLS: {

                break;
            }
            case INVENTORY: {

                break;
            }
            case SONG_BOOK: {

                break;
            }
            case EFFECT_EMITTER: {

                break;
            }
            default:
                System.err.println("Unknown NetState Type, NetState Byte: " + netStateType);
        }
    }




    @Override
    public void write(ByteBuf out) {
        switch (netStateType) {
            case STATE_STATE: {

                break;
            }
            case SHIFT_STATE: {

                break;
            }
            case LOUNGE_IN: {

                break;
            }
            case X_AIM_POSITION: {

                break;
            }
            case Y_AIM_POSITION: {

                break;
            }
            case IDENTITY: {

                break;
            }
            case TEAM: {

                break;
            }
            case LANDED: {

                break;
            }
            case TELEPORT_UP: {
                ((Vec2F) netField).write(out);
                break;
            }
            case TELEPORT_DOWN: {

                break;
            }
            case CHAT_MESSAGE: {

                break;
            }
            case NEW_CHAT_MESSAGE: {

                break;
            }
            case EMOTE: {

                break;
            }
            case MOVEMENT_CONTROLLER: {

                break;
            }
            case ARMOR: {

                break;
            }
            case TOOLS: {

                break;
            }
            case INVENTORY: {

                break;
            }
            case SONG_BOOK: {

                break;
            }
            case EFFECT_EMITTER: {

                break;
            }
        }

    }

    @Override
    public String toString() {
        return "Variant{" +
                "value="  +
                '}';
    }


}
