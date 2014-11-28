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

package starnub.database;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import starnub.StarNub;
import starnub.connections.player.account.*;
import starnub.connections.player.achievements.Achievement;
import starnub.connections.player.achievements.CharacterAchievement;
import starnub.connections.player.character.CharacterIP;
import starnub.connections.player.character.PlayerCharacter;
import starnub.connections.player.groups.*;
import starnub.connections.player.session.Player;
import starnub.connections.player.session.Ban;
import starnub.database.tables.*;

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
        setTableWrappers();
    }

    /**
     *
     * @return DateAndTimes Singleton Instance
     */
    public static DatabaseTables getInstance() {
        return instance;
    }

    private ConnectionSource connection;
    private Accounts accounts;
    private AccountSettings accountSettings;
    private AccountPermissions accountPermissions;
    private Achievements achievements;
    private Characters characters;
    private CharacterAchievements characterAchievements;
    private CharacterIgnores characterIgnores;
    private CharacterIPLog characterIPLog;
    private ChatRooms chatRooms;
    private ChatRoomSubscriptions chatRoomSubscriptions;
    private Groups groups;
    private GroupAssignments groupAssignments;
    private GroupInheritances groupInheritances;
    private GroupPermissions groupPermissions;
    private PlayerSessionLog playerSessionLog;
    private PlayerSessionRestrictions playerSessionRestrictions;
    private Tags tags;

    {

    }

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

    public Achievements getAchievements() {
        return achievements;
    }

    public void setAchievements(Achievements achievements) {
        this.achievements = achievements;
    }

    public Characters getCharacters() {
        return characters;
    }

    public void setCharacters(Characters characters) {
        this.characters = characters;
    }

    public CharacterAchievements getCharacterAchievements() {
        return characterAchievements;
    }

    public void setCharacterAchievements(CharacterAchievements characterAchievements) {
        this.characterAchievements = characterAchievements;
    }

    public CharacterIgnores getCharacterIgnores() {
        return characterIgnores;
    }

    public void setCharacterIgnores(CharacterIgnores characterIgnores) {
        this.characterIgnores = characterIgnores;
    }

    public CharacterIPLog getCharacterIPLog() {
        return characterIPLog;
    }

    public void setCharacterIPLog(CharacterIPLog characterIPLog) {
        this.characterIPLog = characterIPLog;
    }

    public ChatRooms getChatRooms() {
        return chatRooms;
    }

    public void setChatRooms(ChatRooms chatRooms) {
        this.chatRooms = chatRooms;
    }

    public ChatRoomSubscriptions getChatRoomSubscriptions() {
        return chatRoomSubscriptions;
    }

    public void setChatRoomSubscriptions(ChatRoomSubscriptions chatRoomSubscriptions) {
        this.chatRoomSubscriptions = chatRoomSubscriptions;
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

    public PlayerSessionRestrictions getPlayerSessionBans() {
        return playerSessionRestrictions;
    }

    public void setPlayerSessionRestrictions(PlayerSessionRestrictions playerSessionRestrictions) {
        this.playerSessionRestrictions = playerSessionRestrictions;
    }

    public Tags getTags() {
        return tags;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }

    public boolean setConnection() {
        String connectionString = "jdbc:";
        String databaseType = (((String) (StarNub.getConfiguration().getNestedValue("type", "database"))).toLowerCase());
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
        accounts = new Accounts(connection, 0, Account.class, Integer.class);
        accountSettings = new AccountSettings(connection, 0, Settings.class, String.class);
        accountPermissions = new AccountPermissions(connection, 0, AccountPermission.class, Integer.class);
        achievements = new Achievements(connection, 0, Achievement.class, Integer.class);
        characters = new Characters(connection, 0, PlayerCharacter.class, Integer.class);
        characterAchievements = new CharacterAchievements(connection, 0, CharacterAchievement.class, Integer.class);
        characterIgnores = new CharacterIgnores(connection, 0, CharacterIgnore.class, Integer.class);
        characterIPLog = new CharacterIPLog(connection, 0, CharacterIP.class, Integer.class);
//        chatRooms = new ChatRooms(connection, 0, ChatRoom.class, Integer.class);
        chatRoomSubscriptions = new ChatRoomSubscriptions(connection, 0, ChatRoomSubscription.class, Integer.class);
        groups = new Groups(connection, 0, Group.class, String.class);
        groupAssignments = new GroupAssignments(connection, 0, GroupAssignment.class, Integer.class);
        groupInheritances = new GroupInheritances(connection, 0, GroupInheritance.class, Integer.class);
        groupPermissions = new GroupPermissions(connection, 0, GroupPermission.class, Integer.class);
        playerSessionLog = new PlayerSessionLog(connection, 0, Player.class, Integer.class);
        playerSessionRestrictions = new PlayerSessionRestrictions(connection, 0, Ban.class, Integer.class);
        tags = new Tags(connection, 0, Tag.class, Integer.class);
    }
}
