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

package starnubserver.database;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import starnubserver.StarNub;
import starnubserver.connections.player.account.Account;
import starnubserver.connections.player.account.AccountPermission;
import starnubserver.connections.player.account.Settings;
import starnubserver.connections.player.character.CharacterIP;
import starnubserver.connections.player.character.PlayerCharacter;
import starnubserver.connections.player.generic.*;
import starnubserver.connections.player.groups.*;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.database.tables.*;

/**
 * Represents StarNubs Database Tables
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class DatabaseTables {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final DatabaseTables instance = new DatabaseTables();

    /**
     * This constructor is private - Singleton Pattern
     */
    private DatabaseTables(){
        setConnection();
    }

    /**
     *
     * @return DateAndTimes Singleton Instance
     */
    public static DatabaseTables getInstance() {
        return instance;
    }

    private ConnectionSource connection;

    private Accounts accounts = new Accounts(connection, 0, Account.class, Integer.class);
    private AccountSettings accountSettings =  new AccountSettings(connection, 0, Settings.class, String.class);
    private AccountPermissions accountPermissions = new AccountPermissions(connection, 0, AccountPermission.class, Integer.class);
    private Characters characters = new Characters(connection, 0, PlayerCharacter.class, Integer.class);
    private Bans bans = new Bans(connection, 0, Ban.class, Integer.class);
    private BansHistory bansHistory = new BansHistory(connection, 0, BanHistory.class, Integer.class);
    private CharacterIPLog characterIPLog = new CharacterIPLog(connection, 0, CharacterIP.class, Integer.class);
    private Disables disables = new Disables(connection, 0, Disabled.class, String.class);
    private DisablesHistory disablesHistory = new DisablesHistory(connection, 0, DisabledHistory.class, String.class);
    private Groups groups = new Groups(connection, 0, Group.class, String.class);
    private GroupAssignments groupAssignments = new GroupAssignments(connection, 0, GroupAssignment.class, Integer.class);
    private GroupInheritances groupInheritances = new GroupInheritances(connection, 0, GroupInheritance.class, Integer.class);
    private GroupPermissions groupPermissions = new GroupPermissions(connection, 0, GroupPermission.class, Integer.class);
    private PlayerSessionLog playerSessionLog = new PlayerSessionLog(connection, 0, PlayerSession.class, Integer.class);
    private PlayerSessionRestrictions playerSessionRestrictions = new PlayerSessionRestrictions(connection, 0, Ban.class, Integer.class);
    private StaffEntries staffEntries = new StaffEntries(connection, 0, StaffEntry.class, Integer.class);
    private Tags tags = new Tags(connection, 0, Tag.class, Integer.class);

    public ConnectionSource getConnection() {
        return connection;
    }

    public void setConnection(ConnectionSource connection) {
        this.connection = connection;
    }

    public Accounts getAccounts() {
        return accounts;
    }

    public void setAccounts(Accounts accounts) {
        this.accounts = accounts;
    }

    public AccountSettings getAccountSettings() {
        return accountSettings;
    }

    public void setAccountSettings(AccountSettings accountSettings) {
        this.accountSettings = accountSettings;
    }

    public AccountPermissions getAccountPermissions() {
        return accountPermissions;
    }

    public void setAccountPermissions(AccountPermissions accountPermissions) {
        this.accountPermissions = accountPermissions;
    }

    public Characters getCharacters() {
        return characters;
    }

    public void setCharacters(Characters characters) {
        this.characters = characters;
    }

    public Bans getBans() {
        return bans;
    }

    public void setBans(Bans bans) {
        this.bans = bans;
    }

    public BansHistory getBansHistory() {
        return bansHistory;
    }

    public void setBansHistory(BansHistory bansHistory) {
        this.bansHistory = bansHistory;
    }

    public CharacterIPLog getCharacterIPLog() {
        return characterIPLog;
    }

    public void setCharacterIPLog(CharacterIPLog characterIPLog) {
        this.characterIPLog = characterIPLog;
    }

    public Disables getDisables() {
        return disables;
    }

    public void setDisables(Disables disables) {
        this.disables = disables;
    }

    public DisablesHistory getDisablesHistory() {
        return disablesHistory;
    }

    public void setDisablesHistory(DisablesHistory disablesHistory) {
        this.disablesHistory = disablesHistory;
    }

    public Groups getGroups() {
        return groups;
    }

    public void setGroups(Groups groups) {
        this.groups = groups;
    }

    public GroupAssignments getGroupAssignments() {
        return groupAssignments;
    }

    public void setGroupAssignments(GroupAssignments groupAssignments) {
        this.groupAssignments = groupAssignments;
    }

    public GroupInheritances getGroupInheritances() {
        return groupInheritances;
    }

    public void setGroupInheritances(GroupInheritances groupInheritances) {
        this.groupInheritances = groupInheritances;
    }

    public GroupPermissions getGroupPermissions() {
        return groupPermissions;
    }

    public void setGroupPermissions(GroupPermissions groupPermissions) {
        this.groupPermissions = groupPermissions;
    }

    public PlayerSessionLog getPlayerSessionLog() {
        return playerSessionLog;
    }

    public void setPlayerSessionLog(PlayerSessionLog playerSessionLog) {
        this.playerSessionLog = playerSessionLog;
    }

    public PlayerSessionRestrictions getPlayerSessionRestrictions() {
        return playerSessionRestrictions;
    }

    public void setPlayerSessionRestrictions(PlayerSessionRestrictions playerSessionRestrictions) {
        this.playerSessionRestrictions = playerSessionRestrictions;
    }

    public StaffEntries getStaffEntries() {
        return staffEntries;
    }

    public void setStaffEntries(StaffEntries staffEntries) {
        this.staffEntries = staffEntries;
    }

    public Tags getTags() {
        return tags;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }

    public boolean setConnection() {
        String connectionString = "jdbc:";
        String databaseType = (((String) (StarNub.getConfiguration().getNestedValue("database", "type"))).toLowerCase());
        if (databaseType.equals("sqlite")) {
            try {
                connectionString = connectionString+databaseType+":StarNub/Databases/StarNub.db";
                connection = new JdbcPooledConnectionSource(connectionString);
            } catch (Exception e) {
                return false;
            }
            return true;
        } else if (databaseType.equals("mysql")) {
            try {
                String databaseUsername = ((String) (StarNub.getConfiguration().getNestedValue("database", "mysql_user")));
                String databasePassword = ((String) (StarNub.getConfiguration().getNestedValue("database", "mysql_pass")));
                String databaseUrl = ((String) (StarNub.getConfiguration().getNestedValue("database", "mysql_url")));
                connectionString = connectionString+databaseType+"://"+databaseUrl;
                connection = new JdbcPooledConnectionSource(connectionString, databaseUsername, databasePassword);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void setTableWrappers(){

    }
}
