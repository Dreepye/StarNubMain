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

package org.starnub.starnubserver.connections.player.generic;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;
import org.starnub.starnubserver.connections.player.character.PlayerCharacter;
import org.starnub.starnubserver.database.tables.BansHistory;
import org.starnub.starnubserver.connections.player.character.PlayerCharacter;
import org.starnub.starnubserver.database.tables.BansHistory;
import org.starnub.starnubserver.events.events.StarNubEvent;

import java.io.Serializable;

/**
 * This class represents a player ban history
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 *
 */
@DatabaseTable(tableName = "BANS_HISTORY")
public class BanHistory implements Serializable {

    private final static BansHistory BANS_HISTORY_DB = BansHistory.getInstance();

    //TODO DB COLUMNS - METHODS

    @DatabaseField(dataType = DataType.INTEGER, generatedId =true, columnName = "BAN_ID")
    private int banEntryId;

    @DatabaseField(foreign = true, columnName = "CHARACTER")
    private volatile PlayerCharacter playerCharacter;

    /**
     * Could represent a UUID or InetAddress. Is Unique.
     */
    @DatabaseField(dataType = DataType.STRING, unique = true, columnName = "IDENTIFIER")
    private String banIdentifier;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "DATE")
    private volatile DateTime date;

    @DatabaseField(foreign = true, columnName = "STAFF_ENTRY")
    private volatile StaffEntry staffEntry;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "DATE_REMOVED")
    private volatile DateTime dateRemoved;

    /**
     * Constructor for database purposes
     */
    public BanHistory() {}

    /**
     * This will take a ban and create a ban history and save it into the Bans History table
     *
     * @param ban Ban representing the ban to place into history
     * @param createEntry boolean representing if a database entry should be made
     */
    public BanHistory(Ban ban, boolean createEntry){
        this.playerCharacter = ban.getPlayerCharacter();
        this.banIdentifier = ban.getBanIdentifier();
        this. date = ban.getDate();
        this.staffEntry = ban.getStaffEntry();
        this.dateRemoved = DateTime.now();
        if (createEntry) {
            BANS_HISTORY_DB.createOrUpdate(this);
            new StarNubEvent("StarNub_Ban_History_Updated", this);
        }
    }

    public int getBanEntryId() {
        return banEntryId;
    }

    public void setBanEntryId(int banEntryId) {
        this.banEntryId = banEntryId;
    }

    public PlayerCharacter getPlayerCharacter() {
        return playerCharacter;
    }

    public void setPlayerCharacter(PlayerCharacter playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    public String getBanIdentifier() {
        return banIdentifier;
    }

    public void setBanIdentifier(String banIdentifier) {
        this.banIdentifier = banIdentifier;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public StaffEntry getStaffEntry() {
        return staffEntry;
    }

    public void setStaffEntry(StaffEntry staffEntry) {
        this.staffEntry = staffEntry;
    }

    public DateTime getDateRemoved() {
        return dateRemoved;
    }

    public void setDateRemoved(DateTime dateRemoved) {
        this.dateRemoved = dateRemoved;
    }

    @Override
    public String toString() {
        return "BanHistory{" +
                "banEntryId=" + banEntryId +
                ", characterName='" + playerCharacter + '\'' +
                ", banIdentifier='" + banIdentifier + '\'' +
                ", date=" + date +
                ", staffEntry=" + staffEntry +
                ", dateRemoved=" + dateRemoved +
                '}';
    }
}
