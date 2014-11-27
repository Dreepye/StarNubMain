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

package starnub.connections.player.account;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import starnub.connections.player.character.Character;

@DatabaseTable(tableName = "CHARACTER_IGNORES")
public class CharacterIgnore {

    /**
     * Represents a character ignores id
     */
    @DatabaseField(generatedId =true, dataType = DataType.INTEGER, columnName = "CHARACTER_IGNORES_ID")
    private int characterIgnoresId;

    /**
     * Represents the starnubId that has ignored players
     */

    @DatabaseField(foreign = true, uniqueCombo = true, foreignAutoRefresh = true,  columnName = "ACCOUNT_SETTING_ID")
    private Settings accountSetting;

    /**
     * Represents the characterId that is ignored
     */

    @DatabaseField(foreign = true, uniqueCombo = true, foreignAutoRefresh = true,  columnName = "CHARACTER_ID")
    private starnub.connections.player.character.Character characterId;

    /**
     * Constructor for database purposes
     */
    public CharacterIgnore(){}

    public int getCharacterIgnoresId() {
        return characterIgnoresId;
    }

    public Settings getAccountSetting() {
        return accountSetting;
    }

    public Character getCharacterId() {
        return characterId;
    }

    /**
     * Constructor used in adding, removing or updating an accounts ignored character list
     * @param accountSetting int representing main group that is inheriting a group
     * @param characterId int representing the group that is being inherited
     */
    public CharacterIgnore(Settings accountSetting, Character characterId) {
        this.accountSetting = accountSetting;
        this.characterId = characterId;
    }

}
