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

package org.starnub.server.database;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import lombok.Getter;
import org.starnub.server.StarNub;
import org.starnub.server.connectedentities.player.account.*;
import org.starnub.server.connectedentities.player.achievements.Achievement;
import org.starnub.server.connectedentities.player.achievements.CharacterAchievement;
import org.starnub.server.connectedentities.player.character.Character;
import org.starnub.server.connectedentities.player.character.CharacterIP;
import org.starnub.server.connectedentities.player.groups.*;
import org.starnub.server.connectedentities.player.session.Player;
import org.starnub.server.connectedentities.player.session.Restrictions;
import org.starnub.server.server.chat.ChatRoom;
import org.starnub.server.database.tables.*;

import java.util.Map;

public enum DatabaseTables {
    INSTANCE;

    @Getter
    private ConnectionSource connection;
    @Getter
    private Accounts accounts;
    @Getter
    private AccountSettings accountSettings;
    @Getter
    private AccountPermissions accountPermissions;
    @Getter
    private Achievements achievements;
    @Getter
    private Characters characters;
    @Getter
    private CharacterAchievements characterAchievements;
    @Getter
    private CharacterIgnores characterIgnores;
    @Getter
    private CharacterIPLog characterIPLog;
    @Getter
    private ChatRooms chatRooms;
    @Getter
    private ChatRoomSubscriptions chatRoomSubscriptions;
    @Getter
    private Groups groups;
    @Getter
    private GroupAssignments groupAssignments;
    @Getter
    private GroupInheritances groupInheritances;
    @Getter
    private GroupPermissions groupPermissions;
    @Getter
    private PlayerSessionLog playerSessionLog;
    @Getter
    private PlayerSessionRestrictions playerSessionRestrictions;
    @Getter
    private Tags tags;

    {
        setConnection();
        setTableWrappers();
    }

    public boolean setConnection() {
        String connectionString = "jdbc:";
        String databaseType = ((String) ((Map) StarNub.getConfiguration().getConfiguration().get("database")).get("type")).toLowerCase();
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
                String databaseUsername = ((String) ((Map) StarNub.getConfiguration().getConfiguration().get("database")).get("mysql_user"));
                String databasePassword = ((String) ((Map) StarNub.getConfiguration().getConfiguration().get("database")).get("mysql_pass"));
                String databaseUrl = ((String) ((Map) StarNub.getConfiguration().getConfiguration().get("database")).get("mysql_url"));
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
        characters = new Characters(connection, 0, Character.class, Integer.class);
        characterAchievements = new CharacterAchievements(connection, 0, CharacterAchievement.class, Integer.class);
        characterIgnores = new CharacterIgnores(connection, 0, CharacterIgnore.class, Integer.class);
        characterIPLog = new CharacterIPLog(connection, 0, CharacterIP.class, Integer.class);
        chatRooms = new ChatRooms(connection, 0, ChatRoom.class, Integer.class);
        chatRoomSubscriptions = new ChatRoomSubscriptions(connection, 0, ChatRoomSubscription.class, Integer.class);
        groups = new Groups(connection, 0, Group.class, String.class);
        groupAssignments = new GroupAssignments(connection, 0, GroupAssignment.class, Integer.class);
        groupInheritances = new GroupInheritances(connection, 0, GroupInheritance.class, Integer.class);
        groupPermissions = new GroupPermissions(connection, 0, GroupPermission.class, Integer.class);
        playerSessionLog = new PlayerSessionLog(connection, 0, Player.class, Integer.class);
        playerSessionRestrictions = new PlayerSessionRestrictions(connection, 0, Restrictions.class, Integer.class);
        tags = new Tags(connection, 0, Tag.class, Integer.class);
    }
}
