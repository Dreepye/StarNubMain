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

package starnubserver.resources.files;

import org.apache.commons.lang3.exception.ExceptionUtils;
import starnubserver.StarNub;
import starnubserver.connections.player.groups.*;
import starnubserver.database.tables.GroupInheritances;
import starnubserver.database.tables.GroupPermissions;
import starnubserver.database.tables.Groups;
import starnubserver.database.tables.Tags;
import starnubserver.resources.ResourceManager;
import utilities.file.yaml.YAMLWrapper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Represents StarNubs GroupsVerifyDump instance extending YAMLWrapper
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class GroupsManagement extends YAMLWrapper {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final GroupsManagement instance = new GroupsManagement();

    private final ConcurrentHashMap<String, Group> GROUPS = new ConcurrentHashMap<>();

    /**
     * This constructor is private - Singleton Pattern
     */
    public GroupsManagement() {
        super(
                "StarNub",
                (String) ResourceManager.getInstance().getListNestedValue(0, "default_groups_configuration", "file"),
                ResourceManager.getInstance().getNestedValue("default_groups_configuration", "map"),
                (String) ResourceManager.getInstance().getListNestedValue(1, "default_groups_configuration", "file"),
                false,
                true,
                true,
                true,
                true
        );
        StarNub.getLogger().cErrPrint("StarNub", groupSynchronizeFileToDB());
    }

    /**
     * This returns this Singleton - Singleton Pattern
     */
    public static GroupsManagement getInstance() {
        return instance;
    }

    public String groupSynchronizeFileToDB(){
        if (getDATA().isEmpty()){
            return "The groups from utilities.file could not be synchronized with the Database groups, make sure that the StarNub/groups.yml utilities.file exist and that their is no YAML syntax errors.");
        }
        synchronizeGroupsFileToDB();
    }

    private void synchronizeGroupsFileToDB(){
        deleteDBGroupsTags();
        deleteDBTags();
        cleanPermissions();
        addDBGroups();
    }

    private void



    private void deleteDBGroupsTags(){
        try {
            List<Group> groupsInDb = Groups.getInstance().getTableDao().queryForAll();
            for (Group groupDb : groupsInDb) {
                if (!this.getDATA().containsKey(groupDb.getName())) {
                    for (GroupInheritance groupInheritance : groupDb.getInheritedGroups()) {
                        groupDb.getInheritedGroups().remove(groupInheritance);
                    }
                    for (GroupPermission groupPermission : groupDb.getPermissions()) {
                        groupDb.getPermissions().remove(groupPermission);
                    }
                    Groups.getInstance().delete(groupDb);
                }
            }
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
    }

    private void deleteDBTags(){
        try {
            List<Tag> tagsInDb = Tags.getInstance().getTableDao().queryForAll();
            for (Tag tag : tagsInDb){
                if (!groups.containsKey(tag.getName())) {
                    Tags.getInstance().delete(tag);
                }
            }
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
    }

    private void cleanPermissions(){
        try {
            List<GroupPermission> permissionsClean = GroupPermissions.getInstance().getTableDao().queryForAll();
            for (GroupPermission groupPermission : permissionsClean){
                if (groupPermission.getGroup() == null){
                    GroupPermissions.getInstance().delete(groupPermission);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void addDBGroups(){
        for (String groupName : groups.keySet()) {
            Group newGroup = new Group(
                    groupName,
                    createTag((Map) ((Map) groups.get(groupName)).get("tag")),
                    (String) ((Map) ((Map) groups.get(groupName)).get("group_ranking")).get("name"),
                    (int) ((Map) ((Map) groups.get(groupName)).get("group_ranking")).get("rank")
            );
            Groups.getInstance().createIfNotExist(newGroup);
            newGroup.getPermissions().addAll(permissionsUpdate(newGroup, (Object) ((Map) groups.get(groupName)).get("permissions")));
        }
        /* We already loaded the groups and the permission's, but now we need to load inheritances last */
        for (String groupName : groups.keySet()) {
            /* Part one is the group from the DB, Part two is the groups to inherit */
            Group groupDb = Groups.getInstance().getGroupByName(groupName);
            groupDb.getInheritedGroups().addAll(groupInheritancesUpdate(groupDb, ((Map) groups.get(groupName)).get("inherited_groups")));
        }
    }

    private Tag createTag(Map<String, Object> tagMap){
        return new Tag (
                "group",
                (String) tagMap.get("look"),
                (String) tagMap.get("color")
        );
    }

    @SuppressWarnings("unchecked")
    private HashSet<GroupPermission> permissionsUpdate(Group group, Object permissionsObject){
        HashSet<String> permissionsHashSet = new HashSet<>();
        HashSet<GroupPermission> groupPermissions = new HashSet<>();
        if (permissionsObject instanceof String){
            permissionsHashSet.add((String) permissionsObject);
        } else if (permissionsObject instanceof List){
            permissionsHashSet.addAll((List) permissionsObject);
        }
        List<GroupPermission> permissionInDb = GroupPermissions.getInstance().getGroupPermissions(group);
        for (GroupPermission groupPermission : permissionInDb){
            String permission = groupPermission.getPermission();
            if(permissionsHashSet.contains(permission)){
                permissionsHashSet.remove(permission);
            } else {
                GroupPermissions.getInstance().delete(groupPermission);
            }
        }
        for (String stringPermission : permissionsHashSet){
            if (stringPermission.equalsIgnoreCase("none")){
                permissionsHashSet.remove(stringPermission);
            }
        }
        groupPermissions.addAll(permissionsHashSet.stream().map(permissionString -> new GroupPermission(group, permissionString)).collect(Collectors.toList()));
        return groupPermissions;
    }

    @SuppressWarnings("unchecked")
    private HashSet<GroupInheritance> groupInheritancesUpdate(Group group, Object groupObject){
        HashSet<String> groupHashSet = new HashSet<String>();
        HashSet<GroupInheritance> groupInheritances = new HashSet<GroupInheritance>();
        if (groupObject instanceof String){
            groupHashSet.add((String) groupObject);
        } else if (groupObject instanceof List){
            groupHashSet.addAll((List) groupObject);
        }
        for (String groupString : groupHashSet) {
            if (!(groupString.equalsIgnoreCase("none"))) {
                List<GroupInheritance> singleGroupInheritanceList = GroupInheritances.getInstance().getGroupInheritance(group);
                if (singleGroupInheritanceList.size() == 0) {
                    Group groupExist = Groups.getInstance().getGroupByName(groupString);
                    if (groupExist != null) {
                        groupInheritances.add(new GroupInheritance(group, groupExist));
                    }
                } else {
                    for (GroupInheritance singleGroupInheritance : singleGroupInheritanceList) {
                        Group groupToBeInherited = singleGroupInheritance.getInheritedGroup();
                        if (groupToBeInherited != null) {
                            if (!groupToBeInherited.getName().equalsIgnoreCase(groupString)) {
                                Group groupExist = Groups.getInstance().getGroupByName(groupString);
                                if (groupExist != null) {
                                    groupInheritances.add(new GroupInheritance(group, groupExist));
                                }
                            }
                        } else {
                            GroupInheritances.getInstance().delete(singleGroupInheritance);
                            Group groupExist = Groups.getInstance().getGroupByName(groupString);
                            if (groupExist != null) {
                                groupInheritances.add(new GroupInheritance(group, groupExist));
                            }
                        }
                    }
                }
            }
        }
        return groupInheritances;
    }

    public void setNoAccountGroup() {
        noAccountGroup = new NoAccountGroup();
    }

    private void groupInheritance(Group mainGroup, String groupInherited) {
        Group inheritedGroupFromDB = Groups.getInstance().getGroupByName(groupInherited);
        if (inheritedGroupFromDB != null) {
            GroupInheritance groupInheritance = GroupInheritances.getInstance().getGroupInheritance(mainGroup, inheritedGroupFromDB);
            if (groupInheritance == null) {
                mainGroup.addGroupInheritance(inheritedGroupFromDB);
            }
        } else {
            if (!groupInherited.equalsIgnoreCase("none")) {
                StarNub.getLogger().cErrPrint("StarNub", "Error a group is not in the data base but is set as a inherited group. " +
                        "Please add this group to your groups.yml or remove the inheritance.");
            }
        }
    }

    /**
     * This method will save the configuration to utilities.file.
     */
    private void saveGroupsFile() {
//        new YamlDumper().toFileYamlDump(this.groups, "StarNub/groups.yml");
    }

//    inherited_groups: ['none'],
//    group_permissions:

    public void reloadGroups(){


    }

    @SuppressWarnings("unchecked")
    public void updateGroupField(String groupName, String field, String value){
        ((Map) groups.get(groupName)).replace(field, value);
        saveGroupsFile();
//        for (Player players : StarNub.getStarboundServer().getConnectionss().getConnectedPlayers().values()) {
//            Account account = players.getPlayerCharacter().getAccount();
//            if (account != null) {
//                try {
//                    account.getGroups().refreshAll();
//                } catch (SQLException e) {
//                    StarNub.getLogger().cFatPrint("StarNub", "Unable to refresh player assigned groups for account \""+account.getAccountName()+"\".");
//                }
//            }
//        }
    }
}
