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
package starnubserver.connections.player.achievements;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import starnubserver.connections.player.character.PlayerCharacter;

@DatabaseTable(tableName = "CHARACTER_ACHIEVEMENTS")
public class CharacterAchievement {

    /**
     * Represent a characterAchievementId
    */
    @DatabaseField(generatedId = true, columnName = "CHARACTER_ACHIEVEMENT_ID")
    private volatile int characterAcheivmentId;

    /**
     * Representa characterId and the associated achievements
     */

    @DatabaseField(foreign = true, columnName = "CHARACTER_ID")
    private volatile PlayerCharacter playerCharacterId;

    /**
     * Represents this Players IP ina string mainly used for the database
     */

    @DatabaseField(foreign = true, columnName = "ACHIEVEMENT_ID")
    private volatile Achievement achievementId;

    /**
     * Constructor for database purposes
     */
    public CharacterAchievement(){}

    public int getCharacterAcheivmentId() {
        return characterAcheivmentId;
    }

    public PlayerCharacter getPlayerCharacterId() {
        return playerCharacterId;
    }

    public Achievement getAchievementId() {
        return achievementId;
    }

    /**
     * Constructor used in adding, removing or updating a achievement record
     * @param playerCharacterId int representing the characters id
     * @param achievementId int representing the achievement id
     */
    public CharacterAchievement(PlayerCharacter playerCharacterId, Achievement achievementId) {
        this.playerCharacterId = playerCharacterId;
        this.achievementId = achievementId;
    }
}
