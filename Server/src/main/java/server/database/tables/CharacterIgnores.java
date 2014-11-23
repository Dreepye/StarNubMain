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

package server.database.tables;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import server.StarNub;
import server.connectedentities.player.account.CharacterIgnore;
import server.connectedentities.player.account.Settings;
import server.database.TableWrapper;

import java.sql.SQLException;

/**
 * Represents CharacterIgnores Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class CharacterIgnores extends TableWrapper<CharacterIgnore, Integer> {

    public CharacterIgnores(Class<CharacterIgnore> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public CharacterIgnores(ConnectionSource connectionSource, int oldVersion, Class<CharacterIgnore> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }

    public CharacterIgnore getIgnoredCharacter(Settings settings, server.connectedentities.player.character.Character character) {
        CharacterIgnore characterIgnore = null;
        try {
            QueryBuilder<CharacterIgnore, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<CharacterIgnore, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .eq("ACCOUNT_SETTINGS_ID", settings)
                    .and()
                    .eq("CHARACTER_ID", character);
            PreparedQuery<CharacterIgnore> preparedQuery = queryBuilder.prepare();
            characterIgnore = getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return characterIgnore;
    }
}
