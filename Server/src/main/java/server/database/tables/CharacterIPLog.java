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

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import server.StarNub;
import server.database.TableWrapper;
import server.connectedentities.player.character.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents CharacterIP Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class CharacterIPLog extends TableWrapper<CharacterIP, Integer> {

    public CharacterIPLog(Class<CharacterIP> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public CharacterIPLog(ConnectionSource connectionSource, int oldVersion, Class<CharacterIP> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }

    public boolean isCharacterIDAndIPComboRecorded(CharacterIP characterIP){
        try {
            List<CharacterIP> characterIpLogs = getTableDao().queryForMatching(characterIP);
            if(characterIpLogs.size() >= 1){
                return true;
            }
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return false;
    }

    public ArrayList<InetAddress> getCharactersAssociatedIPListFromCharacterId(int characterId) {
        try {
            ArrayList<InetAddress> associatedIps = new ArrayList<InetAddress>();
            /* Get IPs with that match the Starnub ID */
            GenericRawResults<String[]> rawResults = getTableDao().queryRaw("select IP from CHARACTER_IP_LOG where CHARACTER_ID = "+characterId);
            /* Get results of the query */
            List<String[]> results = rawResults.getResults();
            /* Get results set one */
            for (String[] result : results) {
                for (String stringResult : result) {
                    try {
                        InetAddress inetAddress = InetAddress.getByName(stringResult);
                        if (!associatedIps.contains(inetAddress)) {
                            associatedIps.add(inetAddress);
                        }
                    } catch (UnknownHostException e) {
                            /* Just means invalid ip, add error later TODO */
                    }
                }
            }
            return associatedIps;
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    public ArrayList<InetAddress> getCharactersAssociatedIPListFromCharacterIds(ArrayList<Integer> characterIds) {
        ArrayList<InetAddress> associatedIps = new ArrayList<>();
        for (Integer characterId : characterIds) {
            try {
                /* Get IPs with that match the Starnub ID */
                GenericRawResults<String[]> rawResults = getTableDao().queryRaw("select IP from CHARACTER_IP_LOG where CHARACTER_ID = "+characterId);
                /* Get results of the query */
                List<String[]> results = rawResults.getResults();
                /* Get results set one */
                for (String[] result : results) {
                    for (String stringResult : result) {
                        try {
                            InetAddress inetAddress = InetAddress.getByName(stringResult);
                            if (!associatedIps.contains(inetAddress)) {
                                associatedIps.add(inetAddress);
                            }
                        } catch (UnknownHostException e) {
                            /* Just means invalid ip, add error later TODO */
                        }
                    }
                }
            } catch (Exception e) {
                StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            }
        }
        return associatedIps;
    }

    public List<CharacterIP> getCharactersAssociatedIPList(server.connectedentities.player.character.Character character){
        try {
            return getTableDao().queryBuilder().where()
                    .eq("CHARACTER_ID", character)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }
}
