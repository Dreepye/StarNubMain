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
import starbounddata.types.SbData;

import java.util.UUID;

import static starbounddata.packets.Packet.readUUID;
import static starbounddata.packets.Packet.writeUUID;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class ClientShipWorld extends SbData<ClientShipWorld> {

    private UUID uuid;

    public ClientShipWorld() {
    }

    public ClientShipWorld(UUID uuid) {
        this.uuid = uuid;
    }

    public ClientShipWorld(ByteBuf in) {
        super(in);
    }

    public ClientShipWorld(ClientShipWorld clientShipWorld) {
        this.uuid = clientShipWorld.getUuid();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void read(ByteBuf in){
        boolean hasUUID = in.readBoolean();
        if (hasUUID) {
            this.uuid = readUUID(in);
        }
    }

    @Override
    public void write(ByteBuf out) {
        if(uuid == null){
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            writeUUID(out, uuid);
        }
    }

    @Override
    public String toString() {
        return "ClientShipWorld{" +
                "uuid=" + uuid +
                '}';
    }
}
