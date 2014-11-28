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

package starnub.connections.player.account;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import org.codehome.utilities.crypto.PasswordHash;
import org.joda.time.DateTime;
import starnub.StarNub;
import starnub.connections.player.character.PlayerCharacter;
import starnub.connections.player.groups.Group;
import starnub.connections.player.groups.GroupAssignment;
import starnub.connections.player.groups.GroupInheritance;
import starnub.connections.player.groups.GroupPermission;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Represents a StarNub account. Accounts are crucial. They are require for StarNub.
 * This will explain how the account system works...
 * <p>
 * When a player logs into the starbounddata.packets.starbounddata.packets.starnub, that is a character the {@link starnub.connections.player.character.PlayerCharacter}
 * class represents this that character gets attached to a {@link starnub.connections.player.session.Player} session, the Player
 * class represents the session of a character while online. Now the character/player can choose to create an account. This account
 * then hold their personal starbounddata.packets.starbounddata.packets.starnub settings and preferences, like ignore list, starbounddata.packets.chat rooms they were in, ect ect. This Account class
 * also hold the groups the player is apart of, the permissions they have and a list of all the other characters associated with
 * this Account. This is how a user is abel to switch characters but maintain all of the data previously started on another character.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
@DatabaseTable(tableName = "ACCOUNTS")
public class Account {

    /**
     * Represents the accounts unique StarNub ID which is used in other database tables
     */

    @DatabaseField(generatedId = true, columnName = "STARNUB_ID")
    private volatile int starnubId;

    /**
     * Represents the account name associated with the client id
     */

    @DatabaseField(dataType = DataType.STRING, columnName = "ACCOUNT_NAME")
    private volatile String accountName;

    /**
     * Represents the account password associated with the account id and name
     */

    @DatabaseField(dataType = DataType.STRING, columnName = "PASSWORD")
    private volatile String accountPassword;

    /**
     * Represents the account password associated with the account id and name
     */

    @DatabaseField(dataType = DataType.STRING, columnName = "SALT")
    private volatile String accountSalt;

    /**
     * This holds the account settings and preferences
     */

    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 9, columnName = "SETTINGS_ID")
    private volatile Settings accountSettings;

    /**
     * Represents the start time in UTC from when the Player starbounddata.packets.connection was completely excepted
     */

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "LAST_LOGIN")
    private volatile DateTime lastLogin;

    /**
     * Represents the groups associated to this account
     */

    @ForeignCollectionField(eager = true, columnName = "GROUP_ASSIGNMENTS")
    private volatile ForeignCollection<GroupAssignment> groups;

    /**
     * Represents the characters associated to this account
     */

    @ForeignCollectionField(eager = true, columnName = "GROUP_ASSIGNMENTS")
    private volatile ForeignCollection<PlayerCharacter> playerCharacters;

    /**
     * This represents whether an account has been disabled or not
     * <p>
     */

    @DatabaseField(dataType = DataType.BOOLEAN, canBeNull = true, columnName = "DISABLED")
    private volatile boolean disbaled;

    /**
     * Represents the time the account was disabled
     */

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "DISABLED_DATE")
    private volatile DateTime dateDisabled;

    /**
     * This reason the account was disabled
     */

    @DatabaseField(dataType = DataType.STRING, canBeNull = true, columnName = "DISABLED_REASON")
    private volatile String disabledReason;

    /**
     * Represents the time the account will no longer be disabled
     */

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "DISABLED_EXPIRE_DATE")
    private volatile DateTime dateRestrictionExpires;

    /**
     * Represents who disabled the account as a String
     */

    @DatabaseField(dataType = DataType.STRING, columnName = "IMPOSER_NAME")
    private volatile String imposerName;

    /**
     * Represents who disabled this as a Account
     */

    @DatabaseField(foreign = true, columnName = "IMPOSER_STARNUB_ID")
    private volatile Account imposerAccount;

    /**
     * Represents the permissions an account has. They are stored in a tree for mate.
     * <p>
     * Example: commandname -|
     *                          -sub_permissions
     *                                            - commands
     *
     * starnub.whisper.pm
     * starnub.whisper.msg
     * starnub.whisper.w
     * starnub.whisper.whisper
     * Where "starnub" is the command name, "whisper" is the sub permission, pm, msg, w, whisper are the commands that can be
     * used to invoke the whisper command. If an account only has starnub.whisper.pm, then only /sn pm, /starnub pm or /pm would
     * work. In this ConcurrentHashMap.... ConcurrentHashMap<{commandname}ConcurrentHashMap<subpermission, ArrayList<commands>>>
     * <p>
     * Lets take the above commands and a few more and show that the tree would look like. starboundmanager.kick.kick, starboundmanager.admin.broadcast,
     * starboundmanager.admin.killplayer, someplugin.chatness.chatcolors
     * starnub -|
     *          |-whisper |- List containing (w, msg, pm, whisper)
     * starboundmanager -|
     *            |- kick  |- List containing kick
     *            |- admin |- List containing broadcast, killplayer
     * someplugin -|
     *             |- chatness |- chatcolors
     */

    private ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<String>>> permissions;

    /**
     * Constructor for database purposes
     */
    public Account() {}

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

    public ForeignCollection<PlayerCharacter> getPlayerCharacters() {
        return playerCharacters;
    }

    public boolean isDisbaled() {
        return disbaled;
    }

    public DateTime getDateDisabled() {
        return dateDisabled;
    }

    public String getDisabledReason() {
        return disabledReason;
    }

    public DateTime getDateRestrictionExpires() {
        return dateRestrictionExpires;
    }

    public String getImposerName() {
        return imposerName;
    }

    public Account getImposerAccount() {
        return imposerAccount;
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<String>>> getPermissions() {
        return permissions;
    }

    /**
     * Constructor used in account creation. Once the account is created, the account name cannot be changed.
     *
     * @param accountName String representing the account name
     * @param accountPassword String representing the password
     */
    public Account(PlayerCharacter player, String accountName, String accountPassword) {
        this.accountName = accountName;
        this.accountSettings = new Settings(accountName, StarNub.getServer().getServerChat().getChatRoomByName("Universe"));
        this.lastLogin = DateTime.now();
        try {
            this.accountPassword = new PasswordHash().getSaltedHash(accountPassword);
        } catch (Exception e) {
            StarNub.getMessageSender().playerMessage("StarNub", player,"There was a critical error in creating your account. Something " +
                    "went wrong with your password, please contact a administrator.");
        }
        try {
            this.groups = StarNub.getDatabaseTables().getAccounts().getTableDao().getEmptyForeignCollection("GROUP_ASSIGNMENTS");
        } catch (SQLException e) {
            StarNub.getLogger().cErrPrint("sn","An issue occurred when StarNub attempted to add permissions to a Group.");
        }
        StarNub.getDatabaseTables().getAccounts().createIfNotExist(this);
        for (String groupName : StarNub.getServer().getConnectionss().getGroupSync().getGroups().keySet()) {
            Map<String, Object> group = (Map) StarNub.getServer().getConnectionss().getGroupSync().getGroups().get(groupName);
            if (((String) group.get("type")).equalsIgnoreCase("default")) {
                getAndAddGroup(groupName);
            }
        }
        loadPermissions();
        setUpdateTagsMainGroups();
    }

    public void getAndAddGroup(String groupToGet){
        Group group = StarNub.getDatabaseTables().getGroups().getGroupByName(groupToGet);
        this.groups.add(new GroupAssignment(this, group));
    }

    public boolean setAccountPassword(String accountPassword) {
        try {
            this.accountPassword = new PasswordHash().getSaltedHash(accountPassword);
        } catch (Exception e) {
            return false;
        }
        return StarNub.getDatabaseTables().getAccounts().update(this);
    }

    public void setAccountSalt(String accountSalt) {
        this.accountSalt = accountSalt;
    }

    public boolean setLastLogin(DateTime lastLogin) {
        this.lastLogin = lastLogin;
        return StarNub.getDatabaseTables().getAccounts().update(this);
    }

    public void setPermissions(ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<String>>> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(String permission) {
        addPermissionToMap(permission);
        StarNub.getDatabaseTables().getAccountPermissions().createIfNotExist(new AccountPermission(this.starnubId, permission));
    }

    public void deletePermission(String permission){
        String[] permissionBreak = permission.split("\\.", 3);
        if (permissionBreak.length == 3) {
            permissions.get(permissionBreak[0]).get(permissionBreak[1]).remove(permissionBreak[2]);
            if (permissions.get(permissionBreak[0]).get(permissionBreak[1]).isEmpty()) {
                permissions.get(permissionBreak[0]).remove(permissionBreak[1]);
                if (permissions.get(permissionBreak[0]).isEmpty()){
                    permissions.remove(permissionBreak[0]);
                }
            }
        } else if (permissionBreak.length == 2 ) {
            permissions.get(permissionBreak[0]).remove(permissionBreak[1]);
            if (permissions.get(permissionBreak[0]).isEmpty()) {
                permissions.remove(permissionBreak[0]);
            }
        } else if (permissionBreak.length == 1){
            permissions.remove(permissionBreak[0]);
        }
        AccountPermission accountPermission = StarNub.getDatabaseTables().getAccountPermissions().getAccountPermission(this.starnubId, permission);
        StarNub.getDatabaseTables().getAccountPermissions().delete(accountPermission);
    }

    public void reloadPermissions() {
        permissions.clear();
        loadPermissions();
    }

    public void loadPermissions(){
        permissions = new ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<String>>>();
        LinkedHashSet<String> permissionsToLoad = new LinkedHashSet<String>();
        /* Load account permissions first */
        List<AccountPermission> accountPermissionList = StarNub.getDatabaseTables().getAccountPermissions().getAccountPermissions(this.starnubId);
        permissionsToLoad.addAll(accountPermissionList.stream().map(AccountPermission::getPermission).collect(Collectors.toList()));
        /* Group assignment then recursively all inherited groups */
        LinkedHashSet<String> groupPermissions = new LinkedHashSet<String>();
        if (groups != null) {
            LinkedHashSet<Group> groupsToLoad = recursiveGroupInheritanceList(groups);
            for (Group group : groupsToLoad) {
                for (GroupPermission groupPermission : group.getPermissions()) {
                    groupPermissions.add(groupPermission.getPermission());
                }
            }
        }
        permissionsToLoad.addAll(groupPermissions);
        permissionsToLoad.forEach(this::addPermissionToMap);
    }

    private LinkedHashSet<Group> recursiveGroupInheritanceList(ForeignCollection<GroupAssignment> groups){
        LinkedHashSet<Group> uniqueGroups = new LinkedHashSet<Group>();
        for (GroupAssignment groupAssignment : groups) {
            Group group = groupAssignment.getGroup();
            uniqueGroups.add(group);
            uniqueGroups.addAll(recursiveGroupAdd(group.getInheritedGroups()));
        }
        return uniqueGroups;
    }

    private LinkedHashSet<Group> recursiveGroupAdd(ForeignCollection<GroupInheritance> groupInheritances){
        LinkedHashSet<Group> uniqueGroups = new LinkedHashSet<Group>();
        for (GroupInheritance groupInheritance : groupInheritances) {
            StarNub.getDatabaseTables().getGroups().refresh(groupInheritance.getInheritedGroup());
            Group group = groupInheritance.getInheritedGroup();
            uniqueGroups.add(group);
            uniqueGroups.addAll(recursiveGroupAdd(group.getInheritedGroups()));
        }
        return uniqueGroups;
    }

    private void addPermissionToMap(String permissionFinal){
        String[] permissionBreak = permissionFinal.split("\\.", 3);
        if (permissionBreak.length == 3) {
            permissions.putIfAbsent(permissionBreak[0], new ConcurrentHashMap<String, ArrayList<String>>());
            permissions.get(permissionBreak[0]).putIfAbsent(permissionBreak[1], new ArrayList<String>());
            permissions.get(permissionBreak[0]).get(permissionBreak[1]).add(permissionBreak[2]);
        } else if (permissionBreak.length == 2 ) {
            permissions.putIfAbsent(permissionBreak[0], new ConcurrentHashMap<String, ArrayList<String>>());
            permissions.get(permissionBreak[0]).putIfAbsent(permissionBreak[1], new ArrayList<String>());
        } else if (permissionBreak.length == 1){
            permissions.putIfAbsent(permissionBreak[0], new ConcurrentHashMap<String, ArrayList<String>>());
        }
    }

    public boolean hasBasePermission(String basePermission){
        return permissions.containsKey("*") || permissions.containsKey(basePermission);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will return if a player has a permission. It first for a wildcard "*" basically OP,
     * Then this method will see if the person even has the plugin permission. If yes, then we check to see if
     * they have the wildcard "*" for all of that plugins permissions. If not we check for that plugins sub permission,
     * If they have the sub permission, we then check the wildcard "*" for all of the commands or permission ends that
     * fall under that plugins sub permission. If no wild card, we check to see if they have the actual command.
     * <P>
     * Example: {plugname}.{subpermission}.{command}
     *          starnub.starbounddata.packets.starbounddata.packets.starnub.shutdown
     *          starnub.starbounddata.packets.starbounddata.packets.starnub.start
     *          starnub.starbounddata.packets.starbounddata.packets.starnub.*
     *          starnub.*
     * <br>
     * The first line would give me shutdown. The second line would be start. The third line give me anything under
     * starnub.starbounddata.packets.starbounddata.packets.starnub The fourth line gives me all permissions for the starnub plugin.
     *
     * <p>
     * @param startPermissionString String representing the plugin command name to check
     * @param subPermissionString String representing the plugins command specific sub permission
     * @param endPermissionString String representing the plugins sub permission specific Command
     * @return boolean if the account has the permission
     */
    public boolean hasPermission(String startPermissionString, String subPermissionString, String endPermissionString, boolean fullPermission, boolean checkWildCards) {
        if (checkWildCards) {
            try {
                if (permissions.containsKey("*")) {
                    return true;
                }
                if (!fullPermission) {
                    if (permissions.get(startPermissionString).containsKey(subPermissionString)
                            || permissions.get(startPermissionString).containsKey("*")) {
                        return true;
                    }
                }
                if (permissions.get(startPermissionString).get(subPermissionString).contains("*")
                    || permissions.get(startPermissionString).get(subPermissionString).contains(endPermissionString))
                    return true;
            } catch (NullPointerException e) {
                return false;
            }
        } else {
            try {
                if (!fullPermission) {
                    if (permissions.get(startPermissionString).containsKey(subPermissionString)) {
                        return true;
                    }
                }
                if (permissions.get(startPermissionString).get(subPermissionString).contains(endPermissionString))
                    return true;
            } catch (NullPointerException e) {
                return false;
            }
        }
        return false;
    }

    public ArrayList<String> getPermission(String startPermissionString, String subPermissionString, String endPermissionString) {
        try {
            return permissions.get(startPermissionString).get(subPermissionString);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public ArrayList<String> getPermission(String permission){
        String perm3 = null;
        String[] perms;
        try {
            perms = permission.split("\\.", 3);
            perm3 = perms[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            perms = permission.split("\\.", 2);
        }
        return getPermission(perms[0], perms[1], perm3);
    }

    //Only needs the command, sub permission
    public String getPermissionSpecific(String startPermissionString, String subPermissionString) {
        try {
            return permissions.get(startPermissionString).get(subPermissionString).get(0);
        } catch (NullPointerException e) {
            return null;
        }
    }

    //only needs the first parts {base}.{sub}
    public String getPermissionSpecific(String permission){
        String[] strings = permission.split("\\.", 3);
        return getPermissionSpecific(strings[0], strings[1]);
    }

    public ForeignCollection<GroupAssignment> getGroups() {
        return groups;
    }



    @SuppressWarnings("unchecked")
    public void addGroupAssignment(Group group){
        if (StarNub.getDatabaseTables().getGroupAssignments().getGroupAssignments(this, group) == null) {
            this.groups.add(new GroupAssignment(this, group));
        }
    }

    @SuppressWarnings("unchecked")
    public void removeGroupAssignment(Group group){
        this.groups.remove(StarNub.getDatabaseTables().getGroupAssignments().getGroupAssignments(this, group));
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
            if (!px1Null && accountSettings.getChatPrefix1().getTypeOfTag().equalsIgnoreCase("group")) {
                Group tagGroup = StarNub.getDatabaseTables().getGroups().getGroupByName(accountSettings.getChatPrefix1().getName());
                if (tagGroup.getLadderName().equalsIgnoreCase(groupLadder) && tagGroup.getLadderRank() > groupLadderRank) {
                    accountSettings.setChatPrefix1(group.getTag());
                    return;
                }
            }
            if (!px2Null && accountSettings.getChatPrefix2().getTypeOfTag().equalsIgnoreCase("group")) {
                Group tagGroup = StarNub.getDatabaseTables().getGroups().getGroupByName(accountSettings.getChatPrefix2().getName());
                if (tagGroup.getLadderName().equalsIgnoreCase(groupLadder) && tagGroup.getLadderRank() > groupLadderRank) {
                    accountSettings.setChatPrefix2(group.getTag());
                    return;
                }
            }
            if (!sx1Null && accountSettings.getChatSuffix1().getTypeOfTag().equalsIgnoreCase("group")) {
                Group tagGroup = StarNub.getDatabaseTables().getGroups().getGroupByName(accountSettings.getChatSuffix1().getName());
                if (tagGroup.getLadderName().equalsIgnoreCase(groupLadder) && tagGroup.getLadderRank() > groupLadderRank) {
                    accountSettings.setChatSuffix1(group.getTag());
                    return;
                }
            }
            if (!sx2Null && accountSettings.getChatSuffix2().getTypeOfTag().equalsIgnoreCase("group")) {
                Group tagGroup = StarNub.getDatabaseTables().getGroups().getGroupByName(accountSettings.getChatSuffix2().getName());
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


    public void getTagList(){
//        LinkedHashSet<Group> groupHashSet = new LinkedHashSet<Group>();
//        groupAssignmentHashSet.addAll(groups);
//
//        groupAssignmentHashSet.addAll(recursiveGroupInheritanceList(groups));
    }

}
