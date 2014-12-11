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
import starnubserver.connections.player.generic.Tag;
import starnubserver.database.tables.Groups;
import starnubserver.resources.files.GroupsManagement;
import utilities.exceptions.CollectionDoesNotExistException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@DatabaseTable(tableName = "GROUPS")
public class Group {

    private final static Groups GROUPS_DB = Groups.getInstance();

    /* COLUMN NAMES */
    private final static String GROUP_NAME_ID_COLUMN = "GROUP_NAME_ID";
    private final static String TYPE_COLUMN = "TYPE";
    private final static String TAG_ID_COLUMN = "TAG_ID";
    private final static String LADDER_NAME_COLUMN = "LADDER_NAME";
    private final static String LADDER_RANK_COLUMN = "LADDER_RANK";

    @DatabaseField(id = true, dataType = DataType.STRING, unique = true, columnName = GROUP_NAME_ID_COLUMN)
    private volatile String name;

    @DatabaseField(dataType = DataType.STRING, columnName = TYPE_COLUMN)
    private volatile String type;

    @DatabaseField(canBeNull = true, foreign = true, columnName = TAG_ID_COLUMN)
    private volatile Tag tag;

    @DatabaseField(dataType = DataType.STRING, columnName = LADDER_NAME_COLUMN)
    private volatile String ladderName;

    @DatabaseField(dataType = DataType.INTEGER, columnName = LADDER_RANK_COLUMN)
    private volatile int ladderRank;

    private final HashSet<Group> INHERITED_GROUPS = new HashSet<>();
    private final HashSet<String> INHERITED_GROUPS_STRINGS = new HashSet<>();
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
    public Group(String name, String type, Tag tag, String ladderName, int ladderRank, List<String> inheritedGroups, List<String> permissions, boolean createEntry) {
        this.name = name;
        this.type = type;
        this.tag = tag;
        this.ladderName = ladderName;
        this.ladderRank = ladderRank;
        INHERITED_GROUPS_STRINGS.addAll(inheritedGroups);
        GROUP_PERMISSIONS.addAll(permissions);
        if (createEntry) {
            GROUPS_DB.create(this);
        }
    }

    public Group(String name, String type, String tagName, String tagColor, String tagType, String tagLeftBracket, String tagRightBracket, String bracketsColors, String ladderName, int ladderRank, List<String> inheritedGroups, List<String> permissions, boolean createEntry) {
        this.name = name;
        this.type = type;
        this.tag = new Tag(tagName, tagColor, tagType, tagLeftBracket, tagRightBracket, bracketsColors, true);
        this.ladderName = ladderName;
        this.ladderRank = ladderRank;
        INHERITED_GROUPS_STRINGS.addAll(inheritedGroups);
        GROUP_PERMISSIONS.addAll(permissions);
        if (createEntry) {
            GROUPS_DB.create(this);
        }
    }

    @SuppressWarnings("unchecked")
    public Group(String name, Map<String, Object> group, boolean createEntry) {
        this.name = name;
        this.type = (String) group.get("type");
        Map<String, Object> groupLadderData = (Map<String, Object>) group.get("group_ranking");
        this.ladderName = (String) groupLadderData.get("name");
        this.ladderRank = (int) groupLadderData.get("rank");
        Map<String, Object> tagData = (Map<String, Object>) group.get("tag");
        String tagName = (String) tagData.get("look");
        String tagColor = (String) tagData.get("color");
        List<String> brackets = (List<String>) tagData.get("brackets");
        String bracketColor = (String) tagData.get("bracket_color");
        this.tag = new Tag(tagName, tagColor, "group", brackets.get(0), brackets.get(1), bracketColor, true);
        List<String> inheritedGroups = (List<String>) group.get("inherited_groups");
        List<String> permissions = (List<String>) group.get("permissions");
        INHERITED_GROUPS_STRINGS.addAll(inheritedGroups);
        GROUP_PERMISSIONS.addAll(permissions);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    /* Permission Methods */

    public void setGROUP_PERMISSIONS(){
        List<GroupPermission> groupPermissionsDb = GroupPermission.getGroupPermissionsByGroup(this);
        for (GroupPermission groupPermission : groupPermissionsDb){
            String permission = groupPermission.getPermission();
            if(!GROUP_PERMISSIONS.contains(permission)){
                groupPermission.deleteFromDatabase();
            }
        }
        GROUP_PERMISSIONS.forEach(this::addGroupPermission);
    }

    public HashSet<String> getGROUP_PERMISSIONS() {
        return GROUP_PERMISSIONS;
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
        GroupPermission groupPermission = GroupPermission.getGroupPermissionByGroupFirstMatch(group, permission);
        if (groupPermission == null){
            try {
                GroupsManagement.getInstance().addToCollection(permission,  false, false,  group.getName(), "permissions");
            } catch (IOException | CollectionDoesNotExistException e) {
                e.printStackTrace();
            }
            new GroupPermission(group, permission, true, true);
        }
    }

    public void addAllGroupPermissions(HashSet<String> permissions){
        addAllGroupPermissions(this, permissions);
    }

    public static void addAllGroupPermissions(Group group, HashSet<String> permissions){
        for (String permission : permissions){
            addGroupPermission(group, permission);
        }
    }

    public void removeGroupPermission(String permission){
        removeGroupPermission(this, permission);
    }

    public static void removeGroupPermission(Group group, String permission){
        permission = permission.toLowerCase();
        HashSet<String> groupPermissions = group.getGROUP_PERMISSIONS();
        groupPermissions.remove(permission);
        GroupPermission groupPermission = GroupPermission.getGroupPermissionByGroupFirstMatch(group, permission);
        if (groupPermission != null){
            groupPermission.deleteFromDatabase();
            groupPermission.refreshAllRelatedPermissions();
            try {
                GroupsManagement.getInstance().removeFromCollection(permission, group.getName(), "permissions");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeAllGroupPermissions(HashSet<String> permissions){
        removeAllGroupPermissions(this, permissions);
    }

    public static void removeAllGroupPermissions(Group group, HashSet<String> permissions){
        for (String permission : permissions){
            removeGroupPermission(group, permission);
        }
    }

    /* Inherited Group Methods */

    public HashSet<Group> getINHERITED_GROUPS() {
        return INHERITED_GROUPS;
    }

    public void setINHERITED_GROUPS(){
        List<GroupInheritance> groupInheritancesDb = GroupInheritance.getGroupInheritanceByGroup(this);
        for (GroupInheritance groupInheritance : groupInheritancesDb){
            String groupName = groupInheritance.getInheritedGroup().getName();
            if(!INHERITED_GROUPS_STRINGS.contains(groupName)){
                groupInheritance.deleteFromDatabase();
            }
        }
        INHERITED_GROUPS_STRINGS.forEach(this::addGroupInheritance);
    }


    public void addGroupInheritance(String groupName){
        Group group = GroupsManagement.getInstance().getGROUPS().get(groupName);
        if (group != null){
            new GroupInheritance(this, group, true);
            INHERITED_GROUPS_STRINGS.add(groupName);
            INHERITED_GROUPS.add(group);
            try {
                GroupsManagement.getInstance().addToCollection(groupName, false, false, this.getName(), "inherited_groups");
            } catch (IOException | CollectionDoesNotExistException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeGroupInheritance(String groupName){
        Group group = GroupsManagement.getInstance().getGROUPS().remove(groupName);
        if (group != null){
            GroupInheritance groupInheritance = GroupInheritance.getGroupInheritanceByGroupFirstMatch(this, group);
            groupInheritance.deleteFromDatabase();
            INHERITED_GROUPS_STRINGS.remove(groupName);
            INHERITED_GROUPS.remove(group);
            try {
                GroupsManagement.getInstance().removeFromCollection(groupName, this.getName(), "inherited_groups");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public LinkedHashSet<Group> getAllInheritedGroups(){
        LinkedHashSet<Group> finalGroupList = new LinkedHashSet<>();
        HashSet<String> groupsComplete = new HashSet<>();
        finalGroupList.addAll(INHERITED_GROUPS);
        groupsComplete.addAll(INHERITED_GROUPS.stream().map(Group::getName).collect(Collectors.toList()));
        for(Group groupFromFinal : finalGroupList){
            HashSet<Group> inheritedGroups = groupFromFinal.getINHERITED_GROUPS();
            for (Group groupToInherit : inheritedGroups) {
                String groupName = groupToInherit.getName();
                if (!groupsComplete.contains(groupName)){
                    finalGroupList.add(groupToInherit);
                    groupsComplete.add(groupName);
                }
            }
        }
        return finalGroupList;
    }

    public Set<String> getGroupInheritanceNames(){
        return INHERITED_GROUPS.stream().map(Group::getName).collect(Collectors.toSet());
    }

    /* DB Methods */

    public Group getTagFromDbById(){
        return getTagFromDbById(this.name);
    }

    public static Group getTagFromDbById(String groupName){
        return GROUPS_DB.getById(groupName);
    }

    public Group getTagFromDbByIdFirstMatch(){
        return getTagFromDbByIdFirstMatch(this.tag);
    }

    public static Group getTagFromDbByIdFirstMatch(Tag tag){
        return GROUPS_DB.getFirstExact(TAG_ID_COLUMN, tag);
    }

    public List<Group> getMatchingGroupsById(){
        return getMatchingGroupsById(this.name);
    }

    public static List<Group> getMatchingGroupsById(String groupName){
        return GROUPS_DB.getAllSimilar(GROUP_NAME_ID_COLUMN, groupName);
    }

    public List<Group> getMatchingGroupsByLadder(){
        return getMatchingGroupsByLadder(this.ladderName);
    }

    public static List<Group> getMatchingGroupsByLadder(String ladderName){
        return GROUPS_DB.getAllSimilar(LADDER_NAME_COLUMN, ladderName);
    }

    public List<Group> getMatchingGroupsByIdSimiliarLadder(){
        return getMatchingGroupsByIdSimilarLadder(this.name, this.ladderName);
    }

    public static List<Group> getMatchingGroupsByIdSimilarLadder(String groupName, String ladderName){
        return GROUPS_DB.getMatchingColumn1AllSimilarColumn2(GROUP_NAME_ID_COLUMN, groupName, LADDER_NAME_COLUMN, ladderName);
    }

    public void deleteFromDatabaseAndFile(boolean filePurge, boolean completePurge){
        deleteFromDatabaseAndFile(this, filePurge, completePurge);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will purge a group from the database, file if set and if set its inheritances, assignments, tags and permissions.
     *
     * @param group Group representing the group to purge
     * @param filePurge boolean representing if twe should purge the file
     * @param completePurge boolean representing if we should purge inheritances, assignments and permissions
     */
    public static void deleteFromDatabaseAndFile(Group group, boolean filePurge, boolean completePurge){
        if (completePurge){
            group.getTag().deleteFromDatabase();
            List<GroupPermission> groupPermissions = GroupPermission.getGroupPermissionsByGroup(group);
            for (GroupPermission groupPermission : groupPermissions){
                groupPermission.deleteFromDatabase();
            }
            List<GroupInheritance> groupInheritances = GroupInheritance.getGroupInheritanceByGroup(group);
            for (GroupInheritance groupInheritance : groupInheritances){
                groupInheritance.deleteFromDatabase();
            }
            List<GroupInheritance> groupInheritances2 = GroupInheritance.getGroupInheritanceByInheritedGroup(group);
            for (GroupInheritance groupInheritance : groupInheritances2){
                groupInheritance.deleteFromDatabase();
            }
            List<GroupAssignment> groupAssignments = GroupAssignment.getGroupAssignmentByGroup(group);
            for (GroupAssignment groupAssignment : groupAssignments){
                groupAssignment.deleteFromDatabase();
            }
        }
        GROUPS_DB.delete(group);
        String groupName = group.getName();
        if (filePurge) {
            GroupsManagement.getInstance().getGROUPS().remove(groupName);
            try {
                GroupsManagement.getInstance().removeValue(groupName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", tag=" + tag +
                ", ladderName='" + ladderName + '\'' +
                ", ladderRank=" + ladderRank +
                ", INHERITED_GROUPS=" + INHERITED_GROUPS +
                ", INHERITED_GROUPS_STRINGS=" + INHERITED_GROUPS_STRINGS +
                ", GROUP_PERMISSIONS=" + GROUP_PERMISSIONS +
                '}';
    }
}
