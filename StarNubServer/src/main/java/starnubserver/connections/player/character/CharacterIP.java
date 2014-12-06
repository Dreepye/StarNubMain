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
import org.apache.commons.lang3.StringUtils;
import starnubserver.StarNub;

import java.lang.*;
import java.net.InetAddress;

/**
 * This class represents a character ip log entry
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 *
 */
@DatabaseTable(tableName = "CHARACTER_IP_LOG")
public class CharacterIP {

    @DatabaseField(generatedId = true, columnName = "IP_LOG_ID")
    private int characterIPLogId;

    @DatabaseField(foreign = true, canBeNull = false, uniqueCombo = true, columnName = "CHARACTER_ID")
    private PlayerCharacter playerCharacter;

    @DatabaseField(dataType = DataType.STRING, canBeNull = false,  uniqueCombo = true, columnName = "IP")
    private String sessionIpString;

    /**
     * Constructor for database purposes
     */
    public CharacterIP(){}

    /**
     * Constructor used in adding, removing or updating a object in the database table character_ip_log
     *
     * @param playerCharacter PlayerCharacter of the character that was seen with this ip
     * @param sessionIpString String representing the IP Address as a string
     */
    public CharacterIP(PlayerCharacter playerCharacter, String sessionIpString, boolean createEntry) {
        this.playerCharacter = playerCharacter;
        this.sessionIpString = sessionIpString;
        if (createEntry){
            StarNub.getDatabaseTables().getCharacterIPLog().createOrUpdate(this);
        }
    }

    /**
     * Constructor used in adding, removing or updating a object in the database table character_ip_log
     *
     * @param playerCharacter PlayerCharacter of the character that was seen with this ip
     * @param sessionIp InetAddress representing the IP Address
     */
    public CharacterIP(PlayerCharacter playerCharacter, InetAddress sessionIp, boolean createEntry) {
        this.playerCharacter = playerCharacter;
        this.sessionIpString = StringUtils.remove(sessionIp.toString(), "/");
        if (createEntry){
            StarNub.getDatabaseTables().getCharacterIPLog().createOrUpdate(this);
        }
    }

    public int getCharacterIPLogId() {
        return characterIPLogId;
    }

    public void setCharacterIPLogId(int characterIPLogId) {
        this.characterIPLogId = characterIPLogId;
    }

    public PlayerCharacter getPlayerCharacter() {
        return playerCharacter;
    }

    public void setPlayerCharacter(PlayerCharacter playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    public String getSessionIpString() {
        return sessionIpString;
    }

    public void setSessionIpString(String sessionIpString) {
        this.sessionIpString = sessionIpString;
    }

    @Override
    public String toString() {
        return "CharacterIP{" +
                "characterIPLogId=" + characterIPLogId +
                ", playerCharacter=" + playerCharacter +
                ", sessionIpString='" + sessionIpString + '\'' +
                '}';
    }
}
