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

package starnubserver.connections.player.account;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import starnubserver.StarNub;
import starnubserver.connections.player.character.PlayerCharacter;
import starnubserver.connections.player.groups.Group;
import starnubserver.connections.player.groups.GroupAssignment;
import starnubserver.connections.player.groups.GroupInheritance;
import starnubserver.connections.player.groups.GroupPermission;
import starnubserver.database.tables.AccountPermissions;
import starnubserver.database.tables.Accounts;
import starnubserver.database.tables.GroupAssignments;
import starnubserver.database.tables.Groups;
import utilities.crypto.PasswordHash;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
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

    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 9, columnName = "SETTINGS_ID")
    private volatile Settings accountSettings;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "LAST_LOGIN")
    private volatile DateTime lastLogin;

    @ForeignCollectionField(eager = true, columnName = "CHARACTERS")
    private volatile ForeignCollection<PlayerCharacter> characters;

    @ForeignCollectionField(eager = true, columnName = "GROUP_ASSIGNMENTS")
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
    public Account(PlayerCharacter player, String accountName, String accountPassword) {
        this.accountName = accountName;
//        this.accountSettings = new Settings(accountName, StarNub.getStarboundServer().getServerChat().getChatRoomByName("Universe"));
        this.lastLogin = DateTime.now();
        try {
            this.accountPassword = new PasswordHash().getSaltedHash(accountPassword);
        } catch (Exception e) {
//            StarNub.getMessageSender().playerMessage("StarNub", player,"There was a critical error in creating your account. Something " +
//                    "went wrong with your password, please contact a administrator.");
        }
        try {
            this.groups = ACCOUNTS_DB.getTableDao().getEmptyForeignCollection("GROUP_ASSIGNMENTS");
        } catch (SQLException e) {
            StarNub.getLogger().cErrPrint("sn","An issue occurred when StarNub attempted to add permissions to a Group.");
        }
        ACCOUNTS_DB.createIfNotExist(this);
//        for (String groupName : StarNub.getStarboundServer().getConnectionss().getGroupSync().getGroups().keySet()) {
//            Map<String, Object> group = (Map) StarNub.getStarboundServer().getConnectionss().getGroupSync().getGroups().get(groupName);
//            if (((String) group.get("type")).equalsIgnoreCase("default")) {
//                getAndAddGroup(groupName);
//            }
//        }
        loadPermissions();
        setUpdateTagsMainGroups();
    }

    public Account getAccount(String accountName, String password){
        List<Account> accountList = ACCOUNTS_DB.getAllSimilar()
        try {
            accountList = getTableDao().queryBuilder().where()
                    .like("ACCOUNT_NAME", accountName)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        if (accountList.size() == 0) {
            return null;
        }
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

    public void refreshAccount(boolean settings, boolean tags) {
        ACCOUNTS_DB.refresh(this);
        if (settings) {
            accountSettings.refreshSettings(tags);
        }
    }

    public void setStarnubId(int starnubId) {
        this.starnubId = starnubId;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountSettings(Settings accountSettings) {
        this.accountSettings = accountSettings;
    }

    public ForeignCollection<PlayerCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(ForeignCollection<PlayerCharacter> characters) {
        this.characters = characters;
    }

    public void setGroups(ForeignCollection<GroupAssignment> groups) {
        this.groups = groups;
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


    public ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<String>>> getPermissions() {
        return permissions;
    }

    public void getAndAddGroup(String groupToGet){
        Group group = Groups.getInstance().getGroupByName(groupToGet);
        this.groups.add(new GroupAssignment(this, group));
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


    public HashSet<GroupAssignment> getGroups() {
        return groups;
    }



    @SuppressWarnings("unchecked")
    public void addGroupAssignment(Group group){
        if (GroupAssignments.getInstance().getGroupAssignments(this, group) == null) {
            this.groups.add(new GroupAssignment(this, group));
        }
    }

    @SuppressWarnings("unchecked")
    public void removeGroupAssignment(Group group){
        this.groups.remove(GroupAssignments.getInstance().getGroupAssignments(this, group));
    }

    public void refreshGroupAssignments(){

    }


    public void setUpdateTagsMainGroups(){
        for (GroupAssignment groupAssignment : groups) {
            boolean groupSet = true;
            Group group = groupAssignment.getGroup();
            String groupName = group.getName();
            String groupLadder = group.getLadderName();
            int groupLadderRank = group.getLadderRank();
            boolean px1Null = accountSettings.getChatPrefix1() == null;
            boolean px2Null = accountSettings.getChatPrefix2() == null;
            boolean sx1Null = accountSettings.getChatSuffix1() == null;
            boolean sx2Null = accountSettings.getChatSuffix2() == null;
            boolean px12Used = !px1Null & !px2Null;
            if (!px1Null && accountSettings.getChatPrefix1().getType().equalsIgnoreCase("group")) {
                Group tagGroup = Groups.getInstance().getGroupByName(accountSettings.getChatPrefix1().getName());
                if (tagGroup.getLadderName().equalsIgnoreCase(groupLadder) && tagGroup.getLadderRank() > groupLadderRank) {
                    accountSettings.setChatPrefix1(group.getTag());
                    return;
                }
            }
            if (!px2Null && accountSettings.getChatPrefix2().getType().equalsIgnoreCase("group")) {
                Group tagGroup = Groups.getInstance().getGroupByName(accountSettings.getChatPrefix2().getName());
                if (tagGroup.getLadderName().equalsIgnoreCase(groupLadder) && tagGroup.getLadderRank() > groupLadderRank) {
                    accountSettings.setChatPrefix2(group.getTag());
                    return;
                }
            }
            if (!sx1Null && accountSettings.getChatSuffix1().getType().equalsIgnoreCase("group")) {
                Group tagGroup = Groups.getInstance().getGroupByName(accountSettings.getChatSuffix1().getName());
                if (tagGroup.getLadderName().equalsIgnoreCase(groupLadder) && tagGroup.getLadderRank() > groupLadderRank) {
                    accountSettings.setChatSuffix1(group.getTag());
                    return;
                }
            }
            if (!sx2Null && accountSettings.getChatSuffix2().getType().equalsIgnoreCase("group")) {
                Group tagGroup = Groups.getInstance().getGroupByName(accountSettings.getChatSuffix2().getName());
                if (tagGroup.getLadderName().equalsIgnoreCase(groupLadder) && tagGroup.getLadderRank() > groupLadderRank) {
                    accountSettings.setChatSuffix2(group.getTag());
                    return;
                }
            }
            if (px12Used) {
                return;
            } else {
                if (px1Null) {
                    accountSettings.setChatPrefix1(group.getTag());
                } else if (px2Null) {
                    accountSettings.setChatPrefix2(group.getTag());
                }
            }
        }
    }

    //purge account

    //purge overage

    //static purge

    public void getTagList(){
//        LinkedHashSet<Group> groupHashSet = new LinkedHashSet<Group>();
//        groupAssignmentHashSet.addAll(groups);
//
//        groupAssignmentHashSet.addAll(recursiveGroupInheritanceList(groups));
    }
    public Account getAccountByName(String accountName) {
        try {
            QueryBuilder<Account, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<Account, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .like("ACCOUNT_NAME", accountName);
            PreparedQuery<Account> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public Account getAccount(String accountName, String password){
        List<Account> accountList = new ArrayList<>();
        try {
            accountList = getTableDao().queryBuilder().where()
                    .like("ACCOUNT_NAME", accountName)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        if (accountList.size() == 0) {
            return null;
        }
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

}
