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

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import starnubserver.StarNub;
import starnubserver.connections.player.account.Account;
import starnubserver.database.tables.Characters;
import starnubserver.events.events.StarNubEvent;
import utilities.strings.StringUtilities;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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

    @DatabaseField(generatedId = true, columnName = "CHARACTER_ID")
    private volatile int characterId;

    @DatabaseField(dataType = DataType.STRING, uniqueCombo=true, columnName = "NAME")
    private volatile String name;

    @DatabaseField(dataType = DataType.STRING, columnName = "CLEAN_NAME")
    private volatile String cleanName;

    @DatabaseField(dataType = DataType.UUID, uniqueCombo=true, columnName = "UUID")
    private volatile UUID uuid;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "LAST_SEEN")
    private volatile DateTime lastSeen;

    @DatabaseField(dataType = DataType.LONG, columnName = "PLAYED_TIME")
    private volatile long playedTime;

    @DatabaseField(canBeNull = true, foreign = true, columnName = "STARNUB_ID")
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
        PlayerCharacter playerCharacter = Characters.getInstance().getCharacterFromNameUUIDCombo(name, uuid);
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

    public PlayerCharacter getCharacterFromNameUUIDCombo(String nameString, UUID UUID) {
        try {
            QueryBuilder<PlayerCharacter, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<PlayerCharacter, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .eq("NAME", nameString)
                    .and()
                    .eq("uuid", UUID);
            PreparedQuery<PlayerCharacter> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public PlayerCharacter getCharacterFromCleanNameCombo(String nameString) {
        PlayerCharacter playerCharacter = null;
        try {
            QueryBuilder<PlayerCharacter, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<PlayerCharacter, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .like("CLEAN_NAME", nameString);
            PreparedQuery<PlayerCharacter> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public List<PlayerCharacter> getCharacterByName(String characterString){
        try {
            return getTableDao().queryBuilder().where()
                    .like("CLEAN_NAME", characterString)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    public List<PlayerCharacter> getCharactersListFromStarnubId(int starnubId) {
        try {
            return getTableDao().queryForEq("STARNUB_ID", starnubId);
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    public ArrayList<Integer> getCharacterIDsListFromStarnubId(int starnubId){
        try {
            /* Get UUIDs with that match the Starnub ID */
            GenericRawResults<Object[]> rawResults = getTableDao().queryRaw("select CHARACTER_ID from CHARACTERS where STARNUB_ID = " + starnubId, new DataType[] { DataType.INTEGER });
            /* Get results of the query */
            List<Object[]> results = rawResults.getResults();
            ArrayList<Integer> characterIdList = new ArrayList<Integer>();
            for (Object[] result : results) {
                for (Object objectResult : result) {
                    if (!characterIdList.contains(objectResult)) {
                        characterIdList.add((Integer) objectResult);
                    }
                }
            }
            return characterIdList;
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    public ArrayList<UUID> getCharactersUUIDListFromStarnubId(int starnubId) {
        try {
            /* Get UUIDs with that match the Starnub ID */
            GenericRawResults<Object[]> rawResults = getTableDao().queryRaw("select uuid from CHARACTERS where STARNUB_ID = "+ starnubId, new DataType[] { DataType.UUID });
            /* Get results of the query */
            List<Object[]> results = rawResults.getResults();
            ArrayList<UUID> uuidList = new ArrayList<UUID>();
            for (Object[] result : results) {
                for (Object objectResult : result) {
                    if (!uuidList.contains(objectResult)) {
                        uuidList.add((UUID) objectResult);
                    }
                }
            }
            return uuidList;
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
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
