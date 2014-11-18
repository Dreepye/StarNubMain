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

package server.connectedentities.player.groups;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import server.StarNub;

import java.sql.SQLException;

@DatabaseTable(tableName = "GROUPS")
public class Group {

    @Getter
    @DatabaseField(id = true, dataType = DataType.STRING, unique = true, columnName = "GROUP_NAME")
    private volatile String name;

    @Getter
    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true, columnName = "TAG")
    private Tag tag;

    @Getter
    @DatabaseField(dataType = DataType.STRING, columnName = "LADDER_NAME")
    private volatile String ladderName;

    @Getter
    @DatabaseField(dataType = DataType.INTEGER, columnName = "LADDER_RANK")
    private volatile int ladderRank;

    @Getter
    @ForeignCollectionField(eager = true, columnName = "INHERITED_GROUPS")
    ForeignCollection<GroupInheritance> inheritedGroups;

    @Getter
    @ForeignCollectionField(eager = true, columnName = "PERMISSIONS")
    ForeignCollection<GroupPermission> permissions;

    public Group(){}

    public Group(String name, Tag tag, String ladderName, int ladderRank) {
        this.name = name;
        this.tag = tag;
        this.ladderName = ladderName;
        this.ladderRank = ladderRank;
        try {
            this.permissions = StarNub.getDatabaseTables().getGroups().getTableDao().getEmptyForeignCollection("PERMISSIONS");
        } catch (SQLException e) {
            StarNub.getLogger().cErrPrint("sn","An issue occurred when StarNub attempted to add permissions to a Group.");
        }
        try {
            this.inheritedGroups = StarNub.getDatabaseTables().getGroups().getTableDao().getEmptyForeignCollection("INHERITED_GROUPS");
        } catch (SQLException e) {
            StarNub.getLogger().cErrPrint("sn","An issue occurred when StarNub attempted to add group inheritance to a Group.");
        }
        StarNub.getDatabaseTables().getGroups().createOrUpdate(this);
    }

    public void setGroup(String name, String tagName, String tagColor, String ladderName, int ladderRank) {
        this.name = name;
        this.tag = new Tag("group", tagName, tagColor);
        this.ladderName = ladderName;
        this.ladderRank = ladderRank;
        StarNub.getDatabaseTables().getGroups().update(this);
    }

    public void setName(String name) {
        this.name = name;
        StarNub.getDatabaseTables().getGroups().update(this);
    }

    public void setTag(Tag tag) {
        this.tag = tag;
        StarNub.getDatabaseTables().getGroups().update(this);
    }

    public void setLadderName(String ladderName) {
        this.ladderName = ladderName;
        StarNub.getDatabaseTables().getGroups().update(this);
    }

    public void setLadderRank(int ladderRank) {
        this.ladderRank = ladderRank;
        StarNub.getDatabaseTables().getGroups().update(this);
    }

    public void setInheritedGroups(ForeignCollection<GroupInheritance> inheritedGroups) {
        this.inheritedGroups = inheritedGroups;
        StarNub.getDatabaseTables().getGroups().update(this);
    }

    public void setPermissions(ForeignCollection<GroupPermission> permissions) {
        this.permissions = permissions;
        StarNub.getDatabaseTables().getGroups().update(this);
    }

    public void addGroupPermission(String permission){
        if (StarNub.getDatabaseTables().getGroupPermissions().getGroupPermission(this, permission) == null) {
            this.permissions.add(new GroupPermission(this, permission));
        }
    }

    public void removeGroupPermissions(String permission){
        for (GroupPermission groupPermission : permissions) {
            if (groupPermission.getPermission().equalsIgnoreCase(permission)) {
                this.permissions.remove(groupPermission);
            }
        }
    }

    public void addGroupInheritance(Group inherited){
        if (StarNub.getDatabaseTables().getGroupInheritances().getGroupInheritance(this, inherited) == null) {
            this.inheritedGroups.add(new GroupInheritance(this, inherited));
        }
    }

    public void removeGroupInheritance(Group inherited){
        this.inheritedGroups.remove(StarNub.getDatabaseTables().getGroupInheritances().getGroupInheritance(this, inherited));
    }

    public void removeGroupInheritance(GroupInheritance inherited){
        for (GroupInheritance group : inheritedGroups) {
            if (inherited.getGroupInheritanceId() == group.getGroupInheritanceId())
                this.inheritedGroups.remove(group);
        }
    }
}
