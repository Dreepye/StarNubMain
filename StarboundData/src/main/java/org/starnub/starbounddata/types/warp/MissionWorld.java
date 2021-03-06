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

package org.starnub.starbounddata.types.warp;

import io.netty.buffer.ByteBuf;
import org.starnub.starbounddata.types.SbData;

import java.util.UUID;

import static org.starnub.starbounddata.packets.Packet.*;

/**
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 */
public class MissionWorld extends SbData<MissionWorld> {

    private String name;
    private UUID uuid;

    public MissionWorld() {
    }

    public MissionWorld(String name, UUID uuid) {
        this.uuid = uuid;
        this.name = name;
    }

    public MissionWorld(ByteBuf in) {
        super(in);
    }

    public MissionWorld(MissionWorld missionWorld) {
        this.name = missionWorld.getName();
        this.uuid = missionWorld.getUuid();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void read(ByteBuf in){
        this.name = readVLQString(in);
        boolean hasUUID = in.readBoolean();
        if (hasUUID) {
            this.uuid = readUUID(in);
        }
    }

    @Override
    public void write(ByteBuf out) {
        writeStringVLQ(out, name);
        if(uuid == null){
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            writeUUID(out, uuid);
        }
    }

    @Override
    public String toString() {
        return "MissionWorld{" +
                "name='" + name + '\'' +
                ", uuid=" + uuid +
                '}';
    }
}
