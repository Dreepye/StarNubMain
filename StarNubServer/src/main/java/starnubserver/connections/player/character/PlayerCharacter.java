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

package starnubserver.connections.player.character;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;
import starnubserver.StarNub;
import starnubserver.connections.player.account.Account;
import starnubserver.connections.player.generic.Ban;
import utilities.strings.StringUtilities;

import java.util.List;
import java.util.UUID;


/**
 * StarNub's PlayerCharacter represents a character that belongs
 * to a player. We named it PlayerCharacter due to issues with
 * com.java having a Character class. We did not want to confuse
 * Plugin developers or API users.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 *
 */
@DatabaseTable(tableName = "CHARACTERS")
public class PlayerCharacter {


    @DatabaseField(generatedId = true, columnName = "CHARACTER_ID")
    private volatile int characterId;

    @DatabaseField(dataType = DataType.STRING, uniqueCombo=true, columnName = "NAME")
    private volatile String name;

    @DatabaseField(dataType = DataType.STRING, columnName = "CLEAN_NAME")
    private volatile String cleanName;

    @DatabaseField(dataType = DataType.UUID, uniqueCombo=true, columnName = "UUID")
    private volatile UUID uuid;

    @DatabaseField(canBeNull = true, foreign = true,  columnName = "BAN")
    private volatile Ban ban;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "LAST_SEEN")
    private volatile DateTime lastSeen;

    @DatabaseField(dataType = DataType.LONG, columnName = "PLAYED_TIME")
    private volatile long playedTime;

    @DatabaseField(canBeNull = true, foreign = true, columnName = "STARNUB_ID")
    private volatile Account account;

    private volatile List<CharacterIP> associatedIpsCharacters;

    /**
     * Constructor for database purposes
     */
    public PlayerCharacter(){}

    /**
     * Required to build a character class
     *
     * @param name String name of the character
     * @param uuid uuid of the character
     * @param createEntry boolean representing if we should create this entry on construction
     */
    public PlayerCharacter(String name, UUID uuid, boolean createEntry) {
        this.name = name;
        this.cleanName = StringUtilities.completeClean(name);
        this.uuid = uuid;
        this.lastSeen = DateTime.now();
        this.ban = null;
        if (createEntry){
            StarNub.getDatabaseTables().getCharacters().createOrUpdate(this);
        }
    }

    public int getCharacterId() {
        return characterId;
    }

    public void setCharacterId(int characterId) {
        this.characterId = characterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCleanName() {
        return cleanName;
    }

    public void setCleanName(String cleanName) {
        this.cleanName = cleanName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Ban getBan() {
        return ban;
    }

    public void setBan(Ban ban) {
        this.ban = ban;
    }

    public DateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(DateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public long getPlayedTime() {
        return playedTime;
    }

    public Account getAccount() {
        return account;
    }

    public void updateLastSeen(DateTime lastSeen) {
        this.lastSeen = DateTime.now();
        StarNub.getDatabaseTables().getCharacters().update(this);
    }

    public void updatePlayedTimeLastSeen() {
        this.lastSeen = DateTime.now();
        this.playedTime = this.getPlayedTime()+(DateTime.now().getMillis()-lastSeen.getMillis());
        StarNub.getDatabaseTables().getCharacters().update(this);
    }

    public void setPlayedTime(long playedTime) {
        this.playedTime = playedTime;
        StarNub.getDatabaseTables().getCharacters().update(this);
    }

    /**
     *
     * @param account Account which this character will belong too
     */
    public void setAccount(Account account) {
        this.account = account;
        StarNub.getDatabaseTables().getCharacters().update(this);
    }

    public void setAssociatedIps(ForeignCollection<CharacterIP> associatedIps) {

    }

    @SuppressWarnings("unchecked")
    public int initialLogInProcessing(String ip, String uuid){
        int accountId = 0;
        if (this.account != null) {
            accountId = account.getStarnubId();
            this.account.setLastLogin(DateTime.now());
            this.account.loadPermissions();
            StarNub.getDatabaseTables().getAccounts().update(this.account);
        }
        StarNub.getDatabaseTables().getCharacters().createOrUpdate(this);
        this.ban = StarNub.getConnections().getBANSList().banGet(ip, uuid);
        return accountId;
    }

    public void removeBan(){

    }

    public void addBan(){

    }


}
