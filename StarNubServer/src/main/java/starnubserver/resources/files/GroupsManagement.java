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

import starnubserver.StarNub;
import starnubserver.connections.player.groups.Group;
import starnubserver.database.tables.Groups;
import starnubserver.resources.ResourceManager;
import utilities.file.yaml.YAMLWrapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    private static final GroupsManagement instance = new GroupsManagement(); //DEBUG

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
        StarNub.getLogger().cInfoPrint("StarNub", groupSynchronizeFileToDB());
    }

    /**
     * This returns this Singleton - Singleton Pattern
     */
    public static GroupsManagement getInstance() {
        return instance;
    }

    public ConcurrentHashMap<String, Group> getGROUPS() {
        return GROUPS;
    }

    public String reloadAndSynchronizeGroups() throws Exception {
        loadOnConstruct(true);
        return groupSynchronizeFileToDB();
    }

    public String groupSynchronizeFileToDB(){
        if (getDATA().isEmpty()){
            return "The GROUPS from file could not be synchronized with the Database GROUPS, make sure that the StarNub/GROUPS.yml file exist and that their is no YAML syntax errors.";
        }
        synchronizeGroupsFileToDB();
        return "Synchronized Temp Message";
    }

    private void synchronizeGroupsFileToDB(){
        databasePurge();
        constructGroups();
    }

    public void databasePurge(){
        List<Group> groupsFromDb = Groups.getInstance().getAll();
        Set<String> fileGroupKeys = this.getAllKeys();
        for (Group group : groupsFromDb) {
            String groupName = group.getName();
            if (!fileGroupKeys.contains(groupName)) {
                group.deleteFromDatabaseAndFile(false, true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void constructGroups(){
        for (Map.Entry<String, Object> mapEntry : this.getDATA().entrySet()){
            String groupName = mapEntry.getKey();
            Map<String, Object> groupData = (Map<String, Object>) mapEntry.getValue();
            Group freshGroup = new Group(groupName, groupData, true);
            GROUPS.put(groupName, freshGroup);
        }
    }

    public void setGroupInheritances(){
        for (Group group : GROUPS.values()){
            group.setINHERITED_GROUPS();
        }
    }
}
