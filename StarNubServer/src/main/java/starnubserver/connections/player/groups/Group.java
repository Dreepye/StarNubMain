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
import starnubserver.database.tables.GroupInheritances;
import starnubserver.database.tables.GroupPermissions;
import starnubserver.database.tables.Groups;

import java.util.HashSet;
import java.util.List;

@DatabaseTable(tableName = "GROUPS")
public class Group {

    private final static Groups GROUPS_DB = Groups.getInstance();

    /* COLUMN NAMES */
    private final String GROUP_NAME_ID_COLUMN = "GROUP_NAME_ID";
    private final String TAG_ID_COLUMN = "TAG_ID";
    private final String LADDER_NAME_COLUMN = "LADDER_NAME";
    private final String LADDER_RANK_COLUMN = "LADDER_RANK";

    @DatabaseField(id = true, dataType = DataType.STRING, unique = true, columnName = GROUP_NAME_ID_COLUMN)
    private volatile String name;

    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true, columnName = TAG_ID_COLUMN)
    private volatile Tag tag;

    @DatabaseField(dataType = DataType.STRING, columnName = LADDER_NAME_COLUMN)
    private volatile String ladderName;

    @DatabaseField(dataType = DataType.INTEGER, columnName = LADDER_RANK_COLUMN)
    private volatile int ladderRank;

    private final HashSet<Group> INHERITED_GROUPS = new HashSet<>();

    private final HashSet<String> GROUP_PERMISSIONS = new HashSet<>();

    /**
     * Constructor for database purposes
     */
    public Group(){}

    /**
     * This will construct a new Group and Tag
     *
     * @param name String name of the group
     * @param tag Tag the tag to be built
     * @param ladderName String ladder name
     * @param ladderRank int ladder rank
     * @param createEntry boolean representing if a database entry should be made
     */
    public Group(String name, Tag tag, String ladderName, int ladderRank, boolean createEntry) {
        this.name = name;
        this.tag = tag;
        this.ladderName = ladderName;
        this.ladderRank = ladderRank;
        if (createEntry) {
            GROUPS_DB.createOrUpdate(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public String getLadderName() {
        return ladderName;
    }

    public void setLadderName(String ladderName) {
        this.ladderName = ladderName;
    }

    public int getLadderRank() {
        return ladderRank;
    }

    public void setLadderRank(int ladderRank) {
        this.ladderRank = ladderRank;
    }

    public HashSet<Group> getINHERITED_GROUPS() {
        return INHERITED_GROUPS;
    }

    public HashSet<String> getGROUP_PERMISSIONS() {
        return GROUP_PERMISSIONS;
    }

    public void setINHERITED_GROUPS(){

    }

    public void setGROUP_PERMISSIONS(){

    }

    public void addGroupPermission(String permission){
        addGroupPermission(this, permission);
    }

    public static void addGroupPermission(Group group, String permission){
        permission = permission.toLowerCase();
        HashSet<String> groupPermissions = group.getGROUP_PERMISSIONS();
        if (!groupPermissions.contains(permission)){
            groupPermissions.add(permission);
        }
        GroupPermission groupPermission = GroupPermissions.getInstance().getMatchingColumn1FirstSimilarColumn2("GROUP_ID", this, "PERMISSION", permission);
        if (groupPermission != null){
            new GroupPermission(group, permission, true);
        }
    }

    public void addAllGroupPermissions(String permission, HashSet<String> permissions){
        addAllGroupPermissions(this, permissions);
    }

    public static void addAllGroupPermissions(Group group, HashSet<String> permissions){
        for (String permission : permissions){
            addGroupPermission(group, permission);
        }
    }

    public void removeGroupPermission(Group group, String permission){
        permission = permission.toLowerCase();
        if (GROUP_PERMISSIONS.contains(permission)){

        }
    }

    public void removeAllGroupPermissions(Group group, HashSet<String> permission){

    }

    /* DB Methods */

    public Group getTagFromDbById(String groupName){
        return GROUPS_DB.getById(groupName);
    }

    public Group getTagFromDbById(Tag tag){
        return GROUPS_DB.getFirstExact(TAG_ID_COLUMN, tag);
    }

    public List<Group> getMatchingGroupsById(String groupName){
        return GROUPS_DB.getAllSimilar(GROUP_NAME_ID_COLUMN, groupName);
    }

    public List<Group> getMatchingGroupsByLadder(String ladderName){
        return GROUPS_DB.getAllSimilar(LADDER_NAME_COLUMN, ladderName);
    }

    public List<Group> getMatchingGroupsByIdSimiliarLadder(String groupName, String ladderName){
        return GROUPS_DB.getMatchingColumn1AllSimilarColumn2(GROUP_NAME_ID_COLUMN, groupName, LADDER_NAME_COLUMN, ladderName);
    }

    public void deleteFromDatabase(boolean completePurge){
        deleteFromDatabase(this, completePurge);
    }

    public static void deleteFromDatabase(Group group, boolean completePurge){
        if (completePurge){
            group.getTag().deleteFromDatabase();
            List<GroupPermission> groupPermissions = GroupPermissions.getInstance().getAllExact("GROUP_ID", group);
            for (GroupPermission groupPermission : groupPermissions){
                groupPermission.deleteFromDatabase();
            }
            List<GroupInheritance> groupInheritances = GroupInheritances.getInstance().getAllExact("GROUP_ID", group);
            for (GroupInheritance groupInheritance : groupInheritances){
                groupInheritance.deleteFromDatabase();
            }
        }
        GROUPS_DB.delete(group);
    }
}
