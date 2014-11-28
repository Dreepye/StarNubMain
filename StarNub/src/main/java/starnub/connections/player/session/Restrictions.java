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

package starnub.connections.player.session;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;
import starnub.connections.player.account.Account;

/**
 * StarNub's Restricted Player class
 * <p>
 * The class will hold any restrictions a player might have.
 * <p>
 * All data is stored in the database
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
@DatabaseTable(tableName = "PLAYER_RESTRICTIONS")
public class Restrictions {


    @DatabaseField(id = true, dataType = DataType.STRING, columnName = "CHARACTER_RESTRICTION")
    private volatile String accountSettings;

    /**
     * Represents if the restriction includes a mute from starbounddata.packets.chat
     */

    @DatabaseField(dataType = DataType.BOOLEAN, columnName = "MUTED")
    private volatile boolean muted;

    /**
     * Represents if the restriction includes a command block
     */

    @DatabaseField(dataType = DataType.BOOLEAN, columnName = "COMMAND_BLOCKED")
    private volatile boolean commandBlocked;

    /**
     * Represents if the restriction includes a ban from the starbounddata.packets.starbounddata.packets.starnub
     */

    @DatabaseField(dataType = DataType.BOOLEAN, columnName = "UUID_BANNED")
    private volatile boolean uuidBanned;


    /**
     * Represents if the restriction includes a ban from the starbounddata.packets.starbounddata.packets.starnub
     */

    @DatabaseField(dataType = DataType.BOOLEAN, columnName = "IP_BANNED")
    private volatile boolean ipBanned;

    /**
     * Represents who imposed the restriction as a String
     */

    @DatabaseField(dataType = DataType.STRING, columnName = "IMPOSER_NAME")
    private volatile String imposerName;

    /**
     * Represents who imposed the restriction as a Account
     */

    @DatabaseField(foreign = true, columnName = "STARNUB_ID")
    private volatile Account imposerAccount;

    /**
     * Represents the reason for the restriction
     */

    @DatabaseField(dataType = DataType.STRING, columnName = "REASON")
    private volatile String reason;


    /**
     * Represents the time the restriction was placed
     */

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "DATE")
    private volatile DateTime dateRestricted;

    /**
     * Represents the time the restriction will be removed
     */

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "DATE_RESTRICTION_EXPIRES")
    private volatile DateTime dateRestrictionExpires;

    /**
     * Constructor for database purposes
     */
    public Restrictions(){}

    public boolean isMuted() {
        return muted;
    }

    public boolean isCommandBlocked() {
        return commandBlocked;
    }

    public String getImposerName() {
        return imposerName;
    }

    public Account getImposerAccount() {
        return imposerAccount;
    }

    public String getReason() {
        return reason;
    }

    public DateTime getDateRestricted() {
        return dateRestricted;
    }

    public DateTime getDateRestrictionExpires() {
        return dateRestrictionExpires;
    }

    /**
     * This method is for setting a ban restriction
     * @param restrictedIdentifier String Represents the restrictedIdentifier which can be a IP or uuid
     * @param dateRestrictionExpires long Represents the time the restriction will be removed
     */
    public void setBan(String accountSettings, String imposerName, String reason, Account imposerAccount, DateTime dateRestrictionExpires) {
        this.ipBanned = true;
        this.imposerName = imposerName;
        this.imposerAccount = imposerAccount;
        this.reason = reason;
        this.dateRestricted = DateTime.now();
        this.dateRestrictionExpires = dateRestrictionExpires;
    }

    /**
     * This method is for setting a mute restriction
     * @param restrictedIdentifier String Represents the restrictedIdentifier which can be a IP or uuid
     * @param dateRestrictionExpires long Represents the time the restriction will be removed
     */
    public void setMuted(String restrictedIdentifier, String imposerName, String reason, Account imposerAccount, DateTime dateRestrictionExpires) {
        this.muted = true;
        this.imposerName = imposerName;
        this.imposerAccount = imposerAccount;
        this.reason = reason;
        this.dateRestricted = DateTime.now();
        this.dateRestrictionExpires = dateRestrictionExpires;
    }

    /**
     * This method is for setting a command block restriction
     * @param restrictedIdentifier String Represents the restrictedIdentifier which can be a IP or uuid
     * @param dateRestrictionExpires long Represents the time the restriction will be removed
     */
    public void setCommandBlocked(String restrictedIdentifier, String imposerName, String reason, Account imposerAccount, DateTime dateRestrictionExpires) {
        this.commandBlocked = true;
        this.imposerName = imposerName;
        this.imposerAccount = imposerAccount;
        this.reason = reason;
        this.dateRestricted = DateTime.now();
        this.dateRestrictionExpires = dateRestrictionExpires;
    }

    /**
     * This method is for setting a command block and mute restriction
     * @param restrictedIdentifier String Represents the restrictedIdentifier which can be a IP or uuid
     * @param dateRestrictionExpires long Represents the time the restriction will be removed
     */
    public void setMutedCommandBlocked(String restrictedIdentifier, String imposerName, String reason, Account imposerAccount, DateTime dateRestrictionExpires) {
        this.muted = true;
        this.commandBlocked = true;
        this.imposerName = imposerName;
        this.imposerAccount = imposerAccount;
        this.reason = reason;
        this.dateRestricted = DateTime.now();
        this.dateRestrictionExpires = dateRestrictionExpires;
    }

    /**
     * This method is the master restriction setter. Used in the {@link starnub.server.Connectionss} enum
     * singleton.
     * @param restrictedIdentifier String Represents the restrictedIdentifier which can be a IP or uuid
     * @param muted boolean setting the mute restriction
     * @param commandBlocked boolean setting the commandblock restriction
     * @param banned boolean setting if banned restriction
     * @param dateRestrictionExpires DateTime of when the restriction is to expire, used for auto remove restriction functions
     */
    public Restrictions(String restrictedIdentifier, boolean muted, boolean commandBlocked, boolean banned, String imposerName, Account imposerAccount, String reason, DateTime dateRestrictionExpires) {
        this.muted = muted;
        this.commandBlocked = commandBlocked;
        this.ipBanned = banned;
        this.imposerName = imposerName;
        this.imposerAccount = imposerAccount;
        this.reason = reason;
        this.dateRestricted = DateTime.now();
        this.dateRestrictionExpires = dateRestrictionExpires;
    }
}
