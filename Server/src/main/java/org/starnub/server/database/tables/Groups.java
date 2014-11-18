package org.starnub.server.database.tables;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.starnub.server.StarNub;
import org.starnub.server.connectedentities.player.groups.Group;
import org.starnub.server.connectedentities.player.groups.Tag;
import org.starnub.server.database.TableWrapper;

import java.sql.SQLException;

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
