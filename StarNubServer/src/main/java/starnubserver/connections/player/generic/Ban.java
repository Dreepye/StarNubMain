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

package starnubserver.connections.player.generic;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;
import starnubserver.StarNub;
import starnubserver.connections.player.character.PlayerCharacter;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.database.tables.Bans;
import starnubserver.events.events.StarNubEvent;

import java.io.Serializable;

/**
 * This class represents a player ban
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 *
 */
@DatabaseTable(tableName = "BANS")
public class Ban implements Serializable {

    private final static Bans BANS_DB = Bans.getInstance();

    @DatabaseField(dataType = DataType.INTEGER, generatedId =true, columnName = "BAN_ID")
    private volatile int banEntryId;

    @DatabaseField(foreign = true, columnName = "CHARACTER")
    private volatile PlayerCharacter playerCharacter;

    /**
     * Could represent a UUID or InetAddress. Is Unique.
     */
    @DatabaseField(dataType = DataType.STRING, unique = true, columnName = "IDENTIFIER")
    private volatile String banIdentifier;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "DATE")
    private volatile DateTime date;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "DATE_EXPIRES")
    private volatile DateTime dateExpires;

    @DatabaseField(foreign = true, columnName = "STAFF_ENTRY")
    private volatile StaffEntry staffEntry;

    /**
     * Constructor for database purposes
     */
    public Ban() {}

    public Ban(PlayerCharacter playerCharacter, String banIdentifier, DateTime date, DateTime dateExpires, StaffEntry staffEntry, boolean createEntry) {
        this.playerCharacter = playerCharacter;
        this.banIdentifier = banIdentifier;
        this.date = date;
        this.dateExpires = dateExpires;
        this.staffEntry = staffEntry;
        if (createEntry){
            addBan();
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

    public DateTime getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(DateTime dateExpires) {
        this.dateExpires = dateExpires;
    }

    public StaffEntry getStaffEntry() {
        return staffEntry;
    }

    public void setStaffEntry(StaffEntry staffEntry) {
        this.staffEntry = staffEntry;
    }

    public void addBan() {
        BANS_DB.createOrUpdate(this);
        StarNub.getLogger().cInfoPrint("StarNub", "A ban was added for " + playerCharacter.getCleanName() + ". Ban Identifier: " + this.banIdentifier + ".");
        new StarNubEvent("StarNub_Ban_Added", this);
        StarNub.getConnections().getBANSList().put(this.banIdentifier, this);
        PlayerSession playerSession = StarNub.getConnections().getCONNECTED_PLAYERS().getOnlinePlayerByAnyIdentifier(this.banIdentifier);
        if (playerSession != null){
            playerSession.disconnectReason("Banned");
        }
    }

    public void removeBan() {
        new BanHistory(this, true);
        BANS_DB.delete(this);
        StarNub.getLogger().cInfoPrint("StarNub", "A ban was removed for " + playerCharacter.getCleanName() + ". Ban Identifier: " + this.banIdentifier + ".");
        new StarNubEvent("StarNub_Ban_Removed", this);
        StarNub.getConnections().getBANSList().remove(this.banIdentifier);
    }

    @Override
    public String toString() {
        return "Ban{" +
                "banEntryId=" + banEntryId +
                ", characterName='" + playerCharacter + '\'' +
                ", banIdentifier='" + banIdentifier + '\'' +
                ", date=" + date +
                ", dateExpires=" + dateExpires +
                ", staffEntry=" + staffEntry +
                '}';
    }
}
