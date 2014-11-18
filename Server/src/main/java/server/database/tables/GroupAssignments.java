package server.database.tables;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import server.StarNub;
import server.connectedentities.player.account.Account;
import server.connectedentities.player.groups.Group;
import server.connectedentities.player.groups.GroupAssignment;
import server.database.TableWrapper;

import java.sql.SQLException;

public class GroupAssignments extends TableWrapper<GroupAssignment, Integer> {

    public GroupAssignments(Class<GroupAssignment> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public GroupAssignments(ConnectionSource connectionSource, int oldVersion, Class<GroupAssignment> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }


    public GroupAssignment getGroupAssignments (Account account, Group group) {
        GroupAssignment groupAssignment = null;
        try {
            QueryBuilder<GroupAssignment, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<GroupAssignment, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .eq("ACCOUNT_SETTINGS_ID", account)
                    .and()
                    .eq("GROUP", group);
            PreparedQuery<GroupAssignment> preparedQuery = queryBuilder.prepare();
            groupAssignment = getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return groupAssignment;
    }
}
