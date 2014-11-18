package org.starnub.server.database.tables;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.starnub.server.StarNub;
import org.starnub.server.connectedentities.player.account.CharacterIgnore;
import org.starnub.server.connectedentities.player.account.Settings;
import org.starnub.server.database.TableWrapper;

import java.sql.SQLException;

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

    public CharacterIgnore getIgnoredCharacter(Settings settings, org.starnub.server.connectedentities.player.character.Character character) {
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
