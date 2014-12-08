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
import com.j256.ormlite.table.DatabaseTable;
import starnubserver.StarNub;
import starnubserver.connections.player.account.Account;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.database.tables.GroupPermissions;
import starnubserver.resources.connections.Players;

import java.util.HashSet;
import java.util.List;

@DatabaseTable(tableName = "GROUP_PERMISSIONS")
public class GroupPermission {

    private final static GroupPermissions GROUP_PERMISSIONS_DB = GroupPermissions.getInstance();

    /* COLUMN NAMES */
    private final static String PERMISSION_ID_COLUMN = "PERMISSION_ID";
    private final static String GROUP_ID_COLUMN = "GROUP_ID";
    private final static String PERMISSION_COLUMN = "PERMISSION";

    @DatabaseField(generatedId =true, dataType = DataType.INTEGER, columnName = PERMISSION_ID_COLUMN)
    private volatile int groupPermissionsId;

    @DatabaseField(foreign = true, columnName = GROUP_ID_COLUMN)
    private volatile Group group;

    @DatabaseField(columnName = PERMISSION_COLUMN)
    private volatile String permission;

    /**
     * Constructor for database purposes
     */
    public GroupPermission(){}

    /**
     * Constructor used in adding, removing or updating a groups permission
     *
     * @param group Group representing the group to add a permission to
     * @param permission String representing the permission to set for the group
     * @param createEntry boolean representing if a database entry should be made
     */
    public GroupPermission(Group group, String permission, boolean createEntry) {
        this.group = group;
        this.permission = permission;
        if(createEntry){
            GROUP_PERMISSIONS_DB.createOrUpdate(this);
        }
    }

    public int getGroupPermissionsId() {
        return groupPermissionsId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    /* DB METHODS */

    public GroupPermission getGroupPermissionByGroupFirstMatch () {
        return getGroupPermissionByGroupFirstMatch(this.group, this.permission);
    }

    public static GroupPermission getGroupPermissionByGroupFirstMatch (Group group, String permission) {
        return GROUP_PERMISSIONS_DB.getMatchingColumn1FirstSimilarColumn2(GROUP_ID_COLUMN, group, PERMISSION_ID_COLUMN, permission);
    }

    public List<GroupPermission> getGroupPermissionsByGroup(){
        return getGroupPermissionsByGroup(this.group);
    }

    public static List<GroupPermission> getGroupPermissionsByGroup(Group group){
        return GROUP_PERMISSIONS_DB.getAllExact(GROUP_ID_COLUMN, group);
    }

    public List<GroupPermission> getGroupPermissionsByPermission(){
        return getGroupPermissionsByPermission(this.permission);
    }

    public static List<GroupPermission> getGroupPermissionsByPermission(String permission){
        return GROUP_PERMISSIONS_DB.getAllExact(PERMISSION_COLUMN, permission);
    }

    public void deleteFromDatabase(){
        deleteFromDatabase(this);
    }

    public static void deleteFromDatabase(GroupPermission groupPermission){
        GROUP_PERMISSIONS_DB.delete(groupPermission);
    }

    public void refreshAllRelatedPermissions(){
        Players connectedPlayers = StarNub.getConnections().getCONNECTED_PLAYERS();
        if (group.getType().equalsIgnoreCase("noaccount")){
            //noAccount refresh
        } else {
            for (PlayerSession playerSession : connectedPlayers.values()) {
                Account account = playerSession.getPlayerCharacter().getAccount();
                HashSet<GroupAssignment> groups = account.getGroups();

                if ()
                .reloadPermissions();
            }
        }
    }

    @Override
    public String toString() {
        return "GroupPermission{" +
                "groupPermissionsId=" + groupPermissionsId +
                ", group=" + group +
                ", permission='" + permission + '\'' +
                '}';
    }
}
