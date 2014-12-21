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

package starbounddata.types.warp;

import io.netty.buffer.ByteBuf;
import starbounddata.packets.Packet;

import java.util.UUID;

public class ClientShipWorld {

    private UUID uuid;

    public ClientShipWorld() {
    }

    public ClientShipWorld(UUID uuid) {
        this.uuid = uuid;
    }

    public ClientShipWorld(ByteBuf in) {
        readClientShipWorld(in);
    }

    public ClientShipWorld(ClientShipWorld clientShipWorld) {
        this.uuid = clientShipWorld.getUuid();
    }

    public void readClientShipWorld(ByteBuf in){
        boolean hasUUID = in.readBoolean();
        if (hasUUID) {
            this.uuid = Packet.readUUID(in);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * @param out ByteBuf out representing a {@link io.netty.buffer.ByteBuf} to write this Vec2F to
     */
    public void writeClientShipWorld(ByteBuf out) {
        if(uuid == null){
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            Packet.writeUUID(out, uuid);
        }
    }

    public ClientShipWorld copy(){
        return new ClientShipWorld(this);
    }

    @Override
    public String toString() {
        return "ClientShipWorld{" +
                "uuid=" + uuid +
                '}';
    }
}
