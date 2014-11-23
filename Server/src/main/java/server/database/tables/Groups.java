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
import server.connectedentities.player.groups.Group;
import server.connectedentities.player.groups.Tag;
import server.database.TableWrapper;

import java.sql.SQLException;


/**
 * Represents Groups Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Groups extends TableWrapper<Group, String> {

    public Groups(Class<Group> typeParameterDBClass, Class<String> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public Groups(ConnectionSource connectionSource, int oldVersion, Class<Group> typeParameterDBClass, Class<String> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }

    public Group getGroupByName(String groupName) {
        try {
            QueryBuilder<Group, String> queryBuilder =
                    getTableDao().queryBuilder();
            Where<Group, String> where = queryBuilder.where();
            queryBuilder.where()
                    .like("GROUP_NAME", groupName);
            PreparedQuery<Group> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public Group getGroupByTag(Tag groupTag) {
        try {
            QueryBuilder<Group, String> queryBuilder =
                    getTableDao().queryBuilder();
            Where<Group, String> where = queryBuilder.where();
            queryBuilder.where()
                    .eq("TAG", groupTag);
            PreparedQuery<Group> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

}
