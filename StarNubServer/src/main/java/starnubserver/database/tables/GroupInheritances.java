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

package starnubserver.database.tables;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import starnubserver.StarNub;
import starnubserver.connections.player.groups.Group;
import starnubserver.connections.player.groups.GroupInheritance;
import starnubserver.database.TableWrapper;

import java.sql.SQLException;
import java.util.List;

/**
 * Represents GroupInheritance Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class GroupInheritances extends TableWrapper<GroupInheritance, Integer> {

    public GroupInheritances(Class<GroupInheritance> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public GroupInheritances(ConnectionSource connectionSource, int oldVersion, Class<GroupInheritance> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }

    public GroupInheritance getGroupInheritance (Group group, Group inherited) {
        GroupInheritance groupAssignment = null;
        try {
            QueryBuilder<GroupInheritance, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<GroupInheritance, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .eq("MAIN_GROUP", group)
                    .and()
                    .eq("INHERITED_GROUP", inherited);
            PreparedQuery<GroupInheritance> preparedQuery = queryBuilder.prepare();
            groupAssignment = getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return groupAssignment;
    }
    public List<GroupInheritance> getGroupInheritance(Group mainGroupId){
        try {
            return getTableDao().queryBuilder().where()
                    .eq("MAIN_GROUP", mainGroupId)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    public void deleteGroupInheritance(Group mainGroupId){
        try {
            DeleteBuilder<GroupInheritance, Integer> deleteBuilder =
                    getTableDao().deleteBuilder();
            deleteBuilder.where().eq("MAIN_GROUP", mainGroupId);
            deleteBuilder.delete();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
    }

}
