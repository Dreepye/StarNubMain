package server.database.tables;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import server.StarNub;
import server.connectedentities.player.groups.Group;
import server.connectedentities.player.groups.GroupInheritance;
import server.database.TableWrapper;

import java.sql.SQLException;
import java.util.List;

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
