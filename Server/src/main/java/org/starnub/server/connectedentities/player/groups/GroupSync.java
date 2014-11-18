package org.starnub.server.connectedentities.player.groups;

import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehome.utilities.files.YamlDumper;
import org.codehome.utilities.files.YamlLoader;
import org.starnub.server.StarNub;
import org.starnub.server.connectedentities.player.account.Account;
import org.starnub.server.connectedentities.player.session.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public enum GroupSync {
    INSTANCE;

    @Getter
    private Map<String, Object> groups;

    @Getter
    private NoAccountGroup noAccountGroup;

    // This will synchronize groups from file to DB
    public void groupSynchronizeFileToDB(Object sender){
        loadGroupsFromDisk();
        if (this.groups == null){
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, "The groups from file could not be synchronized with the" +
                    "Database groups, make sure that the StarNub/groups.yml file exist and that their is no YAML syntax errors.");
            return;
        }
        synchronizeGroupsFileToDB();
    }

    public void loadGroupsFromDisk(){
        this.groups = new YamlLoader().filePathYamlLoader("StarNub/groups.yml");
    }

    private void synchronizeGroupsFileToDB(){
        deleteDBGroupsTags();
        deleteDBTags();
        cleanPermissions();
        addDBGroups();
    }

    private void deleteDBGroupsTags(){
        try {
            List<Group> groupsInDb = StarNub.getDatabaseTables().getGroups().getTableDao().queryForAll();
            for (Group groupDb : groupsInDb) {
                if (!groups.containsKey(groupDb.getName())) {
                    for (GroupInheritance groupInheritance : groupDb.getInheritedGroups()) {
                        groupDb.getInheritedGroups().remove(groupInheritance);
                    }
                    for (GroupPermission groupPermission : groupDb.getPermissions()) {
                        groupDb.getPermissions().remove(groupPermission);
                    }
                    StarNub.getDatabaseTables().getGroups().delete(groupDb);
                }
            }
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
    }

    private void deleteDBTags(){
        try {
            List<Tag> tagsInDb = StarNub.getDatabaseTables().getTags().getTableDao().queryForAll();
            for (Tag tag : tagsInDb){
                if (!groups.containsKey(tag.getName())) {
                    StarNub.getDatabaseTables().getTags().delete(tag);
                }
            }
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
    }

    private void cleanPermissions(){
        try {
            List<GroupPermission> permissionsClean = StarNub.getDatabaseTables().getGroupPermissions().getTableDao().queryForAll();
            for (GroupPermission groupPermission : permissionsClean){
                if (groupPermission.getGroup() == null){
                    StarNub.getDatabaseTables().getGroupPermissions().delete(groupPermission);
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
            StarNub.getDatabaseTables().getGroups().createIfNotExist(newGroup);
            newGroup.getPermissions().addAll(permissionsUpdate(newGroup, (Object) ((Map) groups.get(groupName)).get("permissions")));
        }
        /* We already loaded the groups and the permission's, but now we need to load inheritances last */
        for (String groupName : groups.keySet()) {
            /* Part one is the group from the DB, Part two is the groups to inherit */
            Group groupDb = StarNub.getDatabaseTables().getGroups().getGroupByName(groupName);
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
        List<GroupPermission> permissionInDb = StarNub.getDatabaseTables().getGroupPermissions().getGroupPermissions(group);
        for (GroupPermission groupPermission : permissionInDb){
            String permission = groupPermission.getPermission();
            if(permissionsHashSet.contains(permission)){
                permissionsHashSet.remove(permission);
            } else {
                StarNub.getDatabaseTables().getGroupPermissions().delete(groupPermission);
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
                List<GroupInheritance> singleGroupInheritanceList = StarNub.getDatabaseTables().getGroupInheritances().getGroupInheritance(group);
                if (singleGroupInheritanceList.size() == 0) {
                    Group groupExist = StarNub.getDatabaseTables().getGroups().getGroupByName(groupString);
                    if (groupExist != null) {
                        groupInheritances.add(new GroupInheritance(group, groupExist));
                    }
                } else {
                    for (GroupInheritance singleGroupInheritance : singleGroupInheritanceList) {
                        Group groupToBeInherited = singleGroupInheritance.getInheritedGroup();
                        if (groupToBeInherited != null) {
                            if (!groupToBeInherited.getName().equalsIgnoreCase(groupString)) {
                                Group groupExist = StarNub.getDatabaseTables().getGroups().getGroupByName(groupString);
                                if (groupExist != null) {
                                    groupInheritances.add(new GroupInheritance(group, groupExist));
                                }
                            }
                        } else {
                            StarNub.getDatabaseTables().getGroupInheritances().delete(singleGroupInheritance);
                            Group groupExist = StarNub.getDatabaseTables().getGroups().getGroupByName(groupString);
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
        Group inheritedGroupFromDB = StarNub.getDatabaseTables().getGroups().getGroupByName(groupInherited);
        if (inheritedGroupFromDB != null) {
            GroupInheritance groupInheritance = StarNub.getDatabaseTables().getGroupInheritances().getGroupInheritance(mainGroup, inheritedGroupFromDB);
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
     * This method will save the configuration to file.
     */
    private void saveGroupsFile() {
        new YamlDumper().toFileYamlDump(this.groups, "StarNub/groups.yml");
    }

//    inherited_groups: ['none'],
//    group_permissions:

    public void reloadGroups(){


    }

    @SuppressWarnings("unchecked")
    public void updateGroupField(String groupName, String field, String value){
        ((Map) groups.get(groupName)).replace(field, value);
        saveGroupsFile();
        for (Player players : StarNub.getServer().getConnections().getConnectedPlayers().values()) {
            Account account = players.getCharacter().getAccount();
            if (account != null) {
                try {
                    account.getGroups().refreshAll();
                } catch (SQLException e) {
                    StarNub.getLogger().cFatPrint("StarNub", "Unable to refresh player assigned groups for account \""+account.getAccountName()+"\".");
                }
            }
        }
    }

}
