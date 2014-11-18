package org.starnub.server.database.tables;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.starnub.server.StarNub;
import org.starnub.server.connectedentities.player.groups.Group;
import org.starnub.server.connectedentities.player.groups.GroupPermission;
import org.starnub.server.database.TableWrapper;

import java.sql.SQLException;
import java.util.List;

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

    public List<GroupPermission> getGroupPermissions(org.starnub.server.connectedentities.player.character.Character groupId){
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
