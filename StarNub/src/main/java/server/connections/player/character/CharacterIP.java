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

package server.connections.player.character;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.StringUtils;

import java.lang.*;
import java.lang.Character;
import java.net.InetAddress;

@DatabaseTable(tableName = "CHARACTER_IP_LOG")
public class CharacterIP {

    /**
     * Represents a an ID for a character id and IP pair
     */

    @DatabaseField(generatedId = true, columnName = "IP_LOG_ID")
    private int characterIDIPPairid;

    /**
     * Represents the character that was seen with a specific IP
     */

    @DatabaseField(foreign = true, canBeNull = false, uniqueCombo = true, foreignAutoRefresh = true, columnName = "CHARACTER_ID")
    private java.lang.Character character;

    /**
     * Represents this Characters IP in a string used for the database storage, cannot store InetAddress
     */

    @DatabaseField(dataType = DataType.STRING, canBeNull = false,  uniqueCombo = true, columnName = "IP")
    private String sessionIpString;

    /**
     * Constructor for database purposes
     */
    public CharacterIP(){}

    public int getCharacterIDIPPairid() {
        return characterIDIPPairid;
    }

    public Character getCharacter() {
        return character;
    }

    public String getSessionIpString() {
        return sessionIpString;
    }

    /**
     * Constructor used in adding, removing or updating a object in the database table character_ip_log
     * @param character PlayerCharacter of the character that was seen with this ip
     * @param sessionIpString String representing the IP Address as a string
     */
    public CharacterIP(java.lang.Character character, String sessionIpString) {
        this.character = character;
        this.sessionIpString = sessionIpString;
    }

    /**
     * Constructor used in adding, removing or updating a object in the database table character_ip_log
     * @param character PlayerCharacter of the character that was seen with this ip
     * @param sessionIp InetAddress representing the IP Address
     */
    public CharacterIP(java.lang.Character character, InetAddress sessionIp) {
        this.character = character;
        this.sessionIpString = StringUtils.remove(sessionIp.toString(), "/");
    }
}
