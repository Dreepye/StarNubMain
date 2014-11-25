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
import server.connections.player.groups.Group;
import server.connections.player.groups.GroupPermission;
import server.database.TableWrapper;

import java.sql.SQLException;
import java.util.List;

/**
 * Represents GroupPermissions Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class GroupPermissions extends TableWrapper<GroupPermission, Integer> {

    public GroupPermissions(Class<GroupPermission> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public GroupPermissions(ConnectionSource connectionSource, int oldVersion, Class<GroupPermission> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }

    public GroupPermission getGroupPermission (Group group, String permission) {
        GroupPermission groupAssignment = null;
        try {
            QueryBuilder<GroupPermission, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<GroupPermission, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .eq("GROUP", group)
                    .and()
                    .eq("PERMISSION", permission);
            PreparedQuery<GroupPermission> preparedQuery = queryBuilder.prepare();
            groupAssignment = getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return groupAssignment;
    }

    public List<GroupPermission> getGroupPermissions(server.connections.player.character.Character groupId){
        try {
            return getTableDao().queryBuilder().where()
                    .eq("GROUP", groupId)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    public List<GroupPermission> getGroupPermissions(Group groupId){
        try {
            return getTableDao().queryBuilder().where()
                    .eq("GROUP", groupId)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }
}
