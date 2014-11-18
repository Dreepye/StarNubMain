package org.starnub.server.database.tables;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.starnub.server.StarNub;
import org.starnub.server.connectedentities.player.character.Character;
import org.starnub.server.database.TableWrapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Characters extends TableWrapper<Character, Integer>{

    public Characters(Class<Character> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public Characters(ConnectionSource connectionSource, int oldVersion, Class<Character> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {
        if (oldVersion == 0) {
//            updateTable("ALTER TABLE `ACCOUNTS` ADD COLUMN `DISABLED_REASON` VARCHAR(255);");
        }
    }

    public Character getCharacterFromNameUUIDCombo(String nameString, UUID UUID) {
        try {
            QueryBuilder<Character, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<Character, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .eq("NAME", nameString)
                    .and()
                    .eq("UUID", UUID);
            PreparedQuery<Character> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public Character getCharacterFromCleanNameCombo(String nameString) {
        Character character = null;
        try {
            QueryBuilder<Character, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<Character, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .like("CLEAN_NAME", nameString);
            PreparedQuery<Character> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public List<Character> getCharacterByName(String characterString){
        try {
            return getTableDao().queryBuilder().where()
                    .like("CLEAN_NAME", characterString)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    public List<Character> getCharactersListFromStarnubId(int starnubId) {
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
            GenericRawResults<Object[]> rawResults = getTableDao().queryRaw("select UUID from CHARACTERS where STARNUB_ID = "+ starnubId, new DataType[] { DataType.UUID });
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
}