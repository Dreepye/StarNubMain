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

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.exception.ExceptionUtils;
import starnubserver.StarNub;
import starnubserver.connections.player.character.PlayerCharacter;
import starnubserver.database.tables.GroupPermissions;

import java.sql.SQLException;
import java.util.List;

@DatabaseTable(tableName = "GROUP_PERMISSIONS")
public class GroupPermission {

    private final static GroupPermissions GROUP_PERMISSIONS_DB = GroupPermissions.getInstance();

    /**
     * Represents a group permissions id
     */

    @DatabaseField(generatedId =true, dataType = DataType.INTEGER, columnName = "GROUP_PERMISSIONS_ID")
    private volatile int groupPermissionsId;

    /**
     * Represents a group that the permission is attached to
     */

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "GROUP")
    private volatile Group group;

    /**
     * Represents a permission which is assigned to the group
     */

    @DatabaseField(columnName = "PERMISSION")
    private volatile String permission;

    /**
     * Constructor for database purposes
     */
    public GroupPermission(){}

    public int getGroupPermissionsId() {
        return groupPermissionsId;
    }

    public Group getGroup() {
        return group;
    }

    public String getPermission() {
        return permission;
    }

    /**
     * Constructor used in adding, removing or updating a groups permission
     * @param group
     * @param permission
     */
    public GroupPermission(Group group, String permission) {
        this.group = group;
        this.permission = permission;
    }




    public GroupPermission getGroupPermission (Group group, String permission) {
        GroupPermission groupAssignment = null;
        try {
            QueryBuilder<GroupPermission, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<GroupPermission, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .eq("GROUP", group)
                    .and()
                    .eq("PERMISSION", permission);
            PreparedQuery<GroupPermission> preparedQuery = queryBuilder.prepare();
            groupAssignment = getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return groupAssignment;
    }

    public List<GroupPermission> getGroupPermissions(PlayerCharacter groupId){
        try {
            return getTableDao().queryBuilder().where()
                    .eq("GROUP", groupId)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    public List<GroupPermission> getGroupPermissions(Group groupId){
        try {
            return getTableDao().queryBuilder().where()
                    .eq("GROUP", groupId)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }
}
