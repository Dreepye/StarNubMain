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

package org.starnub.starnubserver.connections.player.account;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;
import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.connections.player.generic.Tag;
import org.starnub.starnubserver.connections.player.groups.Group;
import org.starnub.starnubserver.connections.player.groups.GroupAssignment;
import org.starnub.starnubserver.database.tables.Accounts;
import org.starnub.starnubserver.resources.files.GroupsManagement;
import org.starnub.utilities.crypto.PasswordHash;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class represents a StarNub account.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 *
 */
@DatabaseTable(tableName = "ACCOUNTS")
public class Account implements Serializable{

    private final static Accounts ACCOUNTS_DB = Accounts.getInstance();

    /* COLUMN NAMES */
    private final static String STARNUB_ID_COLUMN = "STARNUB_ID";
    private final static String NAME_COLUMN = "NAME";
    private final static String PASSWORD_COLUMN = "PASSWORD";
    private final static String SALT_COULMN = "SALT";
    private final static String SETTINGS_ID_COLUMN = "SETTINGS_ID";
    private final static String LAST_LOGIN_COLUMN = "LAST_LOGIN";

    @DatabaseField(generatedId = true, columnName = STARNUB_ID_COLUMN)
    private volatile int starnubId;

    @DatabaseField(dataType = DataType.STRING, columnName = NAME_COLUMN)
    private volatile String accountName;

    @DatabaseField(dataType = DataType.STRING, columnName = PASSWORD_COLUMN)
    private volatile String accountPassword;

    /**
     * Used with DANKS CMS
     */
    @DatabaseField(dataType = DataType.STRING, columnName = "SALT")
    private volatile String accountSalt;

    @DatabaseField(foreign = true, columnName = "SETTINGS_ID")
    private volatile Settings accountSettings;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "LAST_LOGIN")
    private volatile DateTime lastLogin;

    private final HashSet<Group> GROUP_ASSIGNMENTS = new HashSet<>();

    /**
     * Constructor for database purposes
     */
    public Account() {}

    /**
     * Constructor used in account creation. Once the account is created, the account name cannot be changed.
     *
     * @param accountName String representing the account name
     * @param accountPassword String representing the password
     */
    public Account(String accountName, String accountPassword) throws Exception {
        this.accountName = accountName;
        this.lastLogin = DateTime.now();
        this.accountPassword = new PasswordHash().getSaltedHash(accountPassword);
        ConcurrentHashMap<String, Group> groups = GroupsManagement.getInstance().getGROUPS();
        for (Group group : groups.values()){
            String groupType = group.getType();
            if (groupType.equalsIgnoreCase("default")){
                GROUP_ASSIGNMENTS.add(group);
            }
        }
        ACCOUNTS_DB.createIfNotExist(this);
        updateTagsMainGroups();
    }

    public void refreshAccount(boolean settings, boolean tags) {
        ACCOUNTS_DB.refresh(this);
        if (settings) {
            accountSettings.refreshSettings(tags);
        }
    }

    //GROUP ASSIGNMENT TAG REFRESH

    public void setStarnubId(int starnubId) {
        this.starnubId = starnubId;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountSettings(Settings accountSettings) {
        this.accountSettings = accountSettings;
    }

    public int getStarnubId() {
        return starnubId;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public String getAccountSalt() {
        return accountSalt;
    }

    public Settings getAccountSettings() {
        return accountSettings;
    }

    public DateTime getLastLogin() {
        return lastLogin;
    }

    public boolean setAccountPassword(String accountPassword) {
        try {
            this.accountPassword = new PasswordHash().getSaltedHash(accountPassword);
        } catch (Exception e) {
            return false;
        }
        return ACCOUNTS_DB.update(this);
    }

    public void setAccountSalt(String accountSalt) {
        this.accountSalt = accountSalt;
    }

    public boolean setLastLogin(DateTime lastLogin) {
        this.lastLogin = lastLogin;
        return ACCOUNTS_DB.update(this);
    }

    /* Group Methods */

    public Set<String> getAllGroupNames(){
        return getAllGroups().stream().map(Group::getName).collect(Collectors.toSet());
    }

    public TreeMap<Integer, Group> getAllGroupsOrderedByRank(){
        TreeMap<Integer, Group> finalGroupList = new TreeMap<>();
        HashSet<String> groupsComplete = new HashSet<>();
        for (Group group : GROUP_ASSIGNMENTS){
            finalGroupList.put(group.getLadderRank(), group);
        }
        groupsComplete.addAll(GROUP_ASSIGNMENTS.stream().map(Group::getName).collect(Collectors.toList()));
        for(Group groupFromFinal : finalGroupList.values()){
            HashSet<Group> inheritedGroups = groupFromFinal.getINHERITED_GROUPS();
            for (Group groupToInherit : inheritedGroups) {
                String groupName = groupToInherit.getName();
                if (!groupsComplete.contains(groupName)){
                    finalGroupList.put(groupToInherit.getLadderRank(), groupToInherit);
                    groupsComplete.add(groupName);
                }
            }
        }
        return finalGroupList;
    }

    public LinkedHashSet<Group> getAllGroups(){
        LinkedHashSet<Group> finalGroupList = new LinkedHashSet<>();
        HashSet<String> groupsComplete = new HashSet<>();
        finalGroupList.addAll(GROUP_ASSIGNMENTS);
        groupsComplete.addAll(GROUP_ASSIGNMENTS.stream().map(Group::getName).collect(Collectors.toList()));
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

    @SuppressWarnings("unchecked")
    public void addGroupAssignment(Group group){



        //LADDER CHECK
        new GroupAssignment(this, group, true);
//        GROUPS.
    }

    @SuppressWarnings("unchecked")
    public void removeGroupAssignment(Group group){
        GroupAssignment groupAssignment = GroupAssignment.getGroupAssigmentByGroupFirstMatch(this, group);
        groupAssignment.deleteFromDatabase();
    }

    public void refreshGroupAssignments(){

    }

    /* Tag Methods - Account & Settings */

    public Set<Tag> getAvailableTags(){
        return getAvailableTags(this);
    }

    public static Set<Tag> getAvailableTags(Account account){
        return account.getAllGroups().stream().map(Group::getTag).collect(Collectors.toSet());
    }

    public void updateTagsMainGroups(){
        for (Group group : GROUP_ASSIGNMENTS) {
            String groupLadder = group.getLadderName();
            int groupLadderRank = group.getLadderRank();
            boolean px1Null = accountSettings.getChatPrefix1() == null;
            boolean px2Null = accountSettings.getChatPrefix2() == null;
            boolean sx1Null = accountSettings.getChatSuffix1() == null;
            boolean sx2Null = accountSettings.getChatSuffix2() == null;
            boolean px12Used = !px1Null & !px2Null;
            if (!px1Null && accountSettings.getChatPrefix1().getType().equalsIgnoreCase("group")) {
                Group tagGroup = Group.getTagFromDbById(accountSettings.getChatPrefix1().getName());
                if (tagGroup.getLadderName().equalsIgnoreCase(groupLadder) && tagGroup.getLadderRank() > groupLadderRank) {
                    accountSettings.setChatPrefix1(group.getTag());
                    return;
                }
            }
            if (!px2Null && accountSettings.getChatPrefix2().getType().equalsIgnoreCase("group")) {
                Group tagGroup = Group.getTagFromDbById(accountSettings.getChatPrefix2().getName());
                if (tagGroup.getLadderName().equalsIgnoreCase(groupLadder) && tagGroup.getLadderRank() > groupLadderRank) {
                    accountSettings.setChatPrefix2(group.getTag());
                    return;
                }
            }
            if (!sx1Null && accountSettings.getChatSuffix1().getType().equalsIgnoreCase("group")) {
                Group tagGroup = Group.getTagFromDbById(accountSettings.getChatSuffix1().getName());
                if (tagGroup.getLadderName().equalsIgnoreCase(groupLadder) && tagGroup.getLadderRank() > groupLadderRank) {
                    accountSettings.setChatSuffix1(group.getTag());
                    return;
                }
            }
            if (!sx2Null && accountSettings.getChatSuffix2().getType().equalsIgnoreCase("group")) {
                Group tagGroup = Group.getTagFromDbById(accountSettings.getChatSuffix2().getName());
                if (tagGroup.getLadderName().equalsIgnoreCase(groupLadder) && tagGroup.getLadderRank() > groupLadderRank) {
                    accountSettings.setChatSuffix2(group.getTag());
                    return;
                }
            }
            if (px12Used) {
                return;
            } else {
                if (px1Null){
                    accountSettings.setChatPrefix1(group.getTag());
                } else {
                    accountSettings.setChatPrefix2(group.getTag());
                }
            }
        }
    }

    /* DB Methods */

    public Account getAccountByName(){
        return getAccountByNameFirstSimilar(this.accountName);
    }

    public static Account getAccountByNameFirstSimilar(String accountName) {
        return ACCOUNTS_DB.getFirstSimilar(NAME_COLUMN, accountName);
    }

    public List<Account> getAccountByNameAllSimiliar(){
        return getAccountByNameAllSimiliar(this.accountName);
    }

    public static List<Account> getAccountByNameAllSimiliar(String accountName) {
        return ACCOUNTS_DB.getAllSimilar(NAME_COLUMN, accountName);
    }

    public Account getAccountByNamePassword(String accountPassword){
        return getAccountByNamePassword(accountName, accountPassword);
    }

    public static Account getAccountByNamePassword(String accountName, String password){
        List<Account> accountList = getAccountByNameAllSimiliar(accountName);
        PasswordHash passwordHash = new PasswordHash();
        boolean matchingHashPass = false;
        for (Account account : accountList) {
            try {
                matchingHashPass = passwordHash.check(password, account.getAccountPassword());
            } catch (Exception e) {
                StarNub.getLogger().cErrPrint("StarNub", "StarNub had a critical error trying to determine if a password hash from " +
                        "an account matched the input.");
            }
            if (matchingHashPass) {
                return account;
            }
        }
        return null;
    }

    public void deleteFromDatabase( boolean completePurge){
        deleteFromDatabase(this, completePurge);
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will purge a account from the database and if set its group assignments, settings and permissions.
     *
     * @param account Account representing the Account to purge
     * @param completePurge boolean representing if we should purge inheritances, assignments and permissions
     */
    public static void deleteFromDatabase(Account account, boolean completePurge){
        if (completePurge){
            account.getAccountSettings().deleteFromDatabase();
            List<AccountPermission> accountPermissions = AccountPermission.getAccountPermissionsByAccount(account);
            for (AccountPermission accountPermission : accountPermissions){
                accountPermission.deleteFromDatabase();
            }
            List<GroupAssignment> groupAssignments = GroupAssignment.getGroupAssignmentByAccount(account);
            for (GroupAssignment groupAssignment : groupAssignments){
                groupAssignment.deleteFromDatabase();
            }
        }
        ACCOUNTS_DB.delete(account);
    }
}
