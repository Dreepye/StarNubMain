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

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;
import starnubserver.connections.player.account.Account;
import starnubserver.database.tables.Characters;
import starnubserver.events.events.StarNubEvent;
import utilities.strings.StringUtilities;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * StarNub's PlayerCharacter represents a character that belongs
 * to a player. We named it PlayerCharacter due to issues with
 * com.java having a Character class. We did not want to confuse or
 *  have Plugin developers or API users using the wrong class.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 *
 */
@DatabaseTable(tableName = "CHARACTERS")
public class PlayerCharacter implements Serializable {

    private final static Characters CHARACTERS_DB = Characters.getInstance();

    /* COLUMN NAMES */
    private final static String CHARACTER_ID_COLUMN = "CHARACTER_ID";
    private final static String NAME_COLUMN = "NAME";
    private final static String CLEAN_NAME_COLUMN = "CLEAN_NAME";
    private final static String UUID_COLUMN = "UUID";
    private final static String LAST_SEEN_COLUMN = "LAST_SEEN";
    private final static String PLAYED_TIME_COLUMN = "PLAYED_TIME";
    private final static String STARNUB_ID_COLUMN = "STARNUB_ID";

    @DatabaseField(generatedId = true, columnName = CHARACTER_ID_COLUMN)
    private volatile int characterId;

    @DatabaseField(dataType = DataType.STRING, uniqueCombo=true, columnName = NAME_COLUMN)
    private volatile String name;

    @DatabaseField(dataType = DataType.STRING, columnName = CLEAN_NAME_COLUMN)
    private volatile String cleanName;

    @DatabaseField(dataType = DataType.UUID, uniqueCombo=true, columnName = UUID_COLUMN)
    private volatile UUID uuid;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = LAST_SEEN_COLUMN)
    private volatile DateTime lastSeen;

    @DatabaseField(dataType = DataType.LONG, columnName = PLAYED_TIME_COLUMN)
    private volatile long playedTime;

    @DatabaseField(canBeNull = true, foreign = true, columnName = STARNUB_ID_COLUMN)
    private volatile Account account;

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
        if (createEntry){
            CHARACTERS_DB.createOrUpdate(this);
        }
    }

    public static PlayerCharacter getPlayerCharacter(String name, UUID uuid){
        PlayerCharacter playerCharacter = getCharacterByNameUUIDCombo(name, uuid);
        if (playerCharacter != null) {
            playerCharacter.getAccount().refreshAccount(true, true);
            return playerCharacter;
        } else {
            playerCharacter = new PlayerCharacter(name, uuid, true);
            new StarNubEvent("Player_New_Character", playerCharacter);
            return playerCharacter;
        }
    }

    public int getCharacterId() {
        return characterId;
    }

    public String getName() {
        return name;
    }

    public String getCleanName() {
        return cleanName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public DateTime getLastSeen() {
        return lastSeen;
    }

    public long getPlayedTime() {
        return playedTime;
    }

    public Account getAccount() {
        return account;
    }

    public void updateLastSeen(DateTime lastSeen) {
        this.lastSeen = DateTime.now();
        CHARACTERS_DB.update(this);
    }

    public void updatePlayedTimeLastSeen() {
        this.lastSeen = DateTime.now();
        this.playedTime = this.getPlayedTime()+(DateTime.now().getMillis()-lastSeen.getMillis());
        CHARACTERS_DB.update(this);
    }

    /**
     *
     * @param account Account which this character will belong too
     */
    public void setAccount(Account account) {
        this.account = account;
        CHARACTERS_DB.update(this);
    }

    /* DB Methods */

    public PlayerCharacter getCharacterByNameUUIDCombo() {
        return getCharacterByNameUUIDCombo(this.name, this.uuid);
    }

    public static PlayerCharacter getCharacterByNameUUIDCombo(String name, UUID uuid) {
        return CHARACTERS_DB.getMatchingColumn1FirstSimilarColumn2(NAME_COLUMN, name, UUID_COLUMN, uuid);
    }

    public List<PlayerCharacter> getCharacterByName(){
        return getCharacterByName(this.name);
    }

    public static List<PlayerCharacter> getCharacterByName(String name){
        return CHARACTERS_DB.getAllSimilar(NAME_COLUMN, name);
    }

    public List<PlayerCharacter> getCharacterByCleanName(){
        return getCharacterByName(this.cleanName);
    }

    public List<PlayerCharacter> getCharacterByCleanName(String cleanName){
        return CHARACTERS_DB.getAllSimilar(CLEAN_NAME_COLUMN, cleanName);
    }

    public List<PlayerCharacter> getCharactersByStarNubId(){
        return getCharactersByStarNubId(this.getAccount());
    }

    public static List<PlayerCharacter> getCharactersByStarNubId(Account account){
        return CHARACTERS_DB.getAllExact(STARNUB_ID_COLUMN, account);
    }

    public Set<UUID> getCharactersUUIDListFromStarnubId(){
        return getCharactersUUIDListFromStarnubId(this.account);
    }

    public static Set<UUID> getCharactersUUIDListFromStarnubId(Account account) {
        List<PlayerCharacter> playerCharacters = getCharactersByStarNubId(account);
        return playerCharacters.stream().map(PlayerCharacter::getUuid).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "PlayerCharacter{" +
                "characterId=" + characterId +
                ", name='" + name + '\'' +
                ", cleanName='" + cleanName + '\'' +
                ", uuid=" + uuid +
                ", lastSeen=" + lastSeen +
                ", playedTime=" + playedTime +
                ", account=" + account +
                '}';
    }
}
