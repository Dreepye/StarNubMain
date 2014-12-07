/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This utilities.file is part of org.starnub a Java Wrapper for Starbound.
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

package starnubserver.connections.player.groups;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.exception.ExceptionUtils;
import starnubserver.StarNub;
import starnubserver.database.tables.GroupInheritances;
import starnubserver.database.tables.GroupPermissions;
import starnubserver.database.tables.Groups;

import java.sql.SQLException;

@DatabaseTable(tableName = "GROUPS")
public class Group {

    private final static Groups GROUPS_DB = Groups.getInstance();

    @DatabaseField(id = true, dataType = DataType.STRING, unique = true, columnName = "GROUP_NAME")
    private volatile String name;


    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true, columnName = "TAG")
    private Tag tag;

    @DatabaseField(dataType = DataType.STRING, columnName = "LADDER_NAME")
    private volatile String ladderName;


    @DatabaseField(dataType = DataType.INTEGER, columnName = "LADDER_RANK")
    private volatile int ladderRank;


    @ForeignCollectionField(eager = true, columnName = "INHERITED_GROUPS")
    ForeignCollection<GroupInheritance> inheritedGroups;


    @ForeignCollectionField(eager = true, columnName = "PERMISSIONS")
    ForeignCollection<GroupPermission> permissions;

    public Group(){}

    public String getName() {
        return name;
    }

    public Tag getTag() {
        return tag;
    }

    public String getLadderName() {
        return ladderName;
    }

    public int getLadderRank() {
        return ladderRank;
    }

    public ForeignCollection<GroupInheritance> getInheritedGroups() {
        return inheritedGroups;
    }

    public ForeignCollection<GroupPermission> getPermissions() {
        return permissions;
    }

    public Group(String name, Tag tag, String ladderName, int ladderRank) {
        this.name = name;
        this.tag = tag;
        this.ladderName = ladderName;
        this.ladderRank = ladderRank;
        try {
            this.permissions = GROUPS_DB.getTableDao().getEmptyForeignCollection("PERMISSIONS");
        } catch (SQLException e) {
            StarNub.getLogger().cErrPrint("sn","An issue occurred when StarNub attempted to add permissions to a Group.");
        }
        try {
            this.inheritedGroups = GROUPS_DB.getTableDao().getEmptyForeignCollection("INHERITED_GROUPS");
        } catch (SQLException e) {
            StarNub.getLogger().cErrPrint("sn","An issue occurred when StarNub attempted to add group inheritance to a Group.");
        }
        GROUPS_DB.createOrUpdate(this);
    }

    public void setGroup(String name, String tagName, String tagColor, String ladderName, int ladderRank) {
        this.name = name;
        this.tag = new Tag("group", tagName, tagColor);
        this.ladderName = ladderName;
        this.ladderRank = ladderRank;
        GROUPS_DB.update(this);
    }

    public void setName(String name) {
        this.name = name;
        GROUPS_DB.update(this);
    }

    public void setTag(Tag tag) {
        this.tag = tag;
        GROUPS_DB.update(this);
    }

    public void setLadderName(String ladderName) {
        this.ladderName = ladderName;
        GROUPS_DB.update(this);
    }

    public void setLadderRank(int ladderRank) {
        this.ladderRank = ladderRank;
        GROUPS_DB.update(this);
    }

    public void setInheritedGroups(ForeignCollection<GroupInheritance> inheritedGroups) {
        this.inheritedGroups = inheritedGroups;
        GROUPS_DB.update(this);
    }

    public void setPermissions(ForeignCollection<GroupPermission> permissions) {
        this.permissions = permissions;
        GROUPS_DB.update(this);
    }

    public void addGroupPermission(String permission){
        if (GroupPermissions.getInstance().getGroupPermission(this, permission) == null) {
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
        if (GroupInheritances.getInstance().getGroupInheritance(this, inherited) == null) {
            this.inheritedGroups.add(new GroupInheritance(this, inherited));
        }
    }

    public void removeGroupInheritance(Group inherited){
        this.inheritedGroups.remove(GroupInheritances.getInstance().getGroupInheritance(this, inherited));
    }

    public void removeGroupInheritance(GroupInheritance inherited){
        for (GroupInheritance group : inheritedGroups) {
            if (inherited.getGroupInheritanceId() == group.getGroupInheritanceId())
                this.inheritedGroups.remove(group);
        }
    }




    public Group getGroupByName(String groupName) {
        try {
            QueryBuilder<Group, String> queryBuilder =
                    getTableDao().queryBuilder();
            Where<Group, String> where = queryBuilder.where();
            queryBuilder.where()
                    .like("GROUP_NAME", groupName);
            PreparedQuery<Group> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public Group getGroupByTag(Tag groupTag) {
        try {
            QueryBuilder<Group, String> queryBuilder =
                    getTableDao().queryBuilder();
            Where<Group, String> where = queryBuilder.where();
            queryBuilder.where()
                    .eq("TAG", groupTag);
            PreparedQuery<Group> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }
}
