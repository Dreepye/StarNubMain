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

package starbounddata.types.entity.team;

import io.netty.buffer.ByteBuf;
import starbounddata.types.SbData;

public class EntityDamageTeam extends SbData<EntityDamageTeam> {

    private TeamType teamType;
    private byte team;

    public EntityDamageTeam(TeamType teamType, byte team) {
        this.teamType = teamType;
        this.team = team;
    }

    public EntityDamageTeam(ByteBuf in) {
        super(in);
    }

    public EntityDamageTeam(EntityDamageTeam entityDamageTeam) {
        this.teamType = entityDamageTeam.getTeamType();
        this.team = entityDamageTeam.getTeam();
    }

    public TeamType getTeamType() {
        return teamType;
    }

    public void setTeamType(TeamType teamType) {
        this.teamType = teamType;
    }

    public byte getTeam() {
        return team;
    }

    public void setTeam(byte team) {
        this.team = team;
    }

    @Override
    public void read(ByteBuf in) {
        this.teamType = TeamType.values()[in.readUnsignedByte()];
        this.team = (byte) in.readUnsignedByte();

    }

    @Override
    public void write(ByteBuf out) {
        out.writeByte(teamType.ordinal());
        out.writeByte(team);
    }

    @Override
    public String toString() {
        return "EntityDamageTeam{" +
                "teamType=" + teamType +
                ", team=" + team +
                "} " + super.toString();
    }
}
