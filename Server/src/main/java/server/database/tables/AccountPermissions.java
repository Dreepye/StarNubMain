package server.database.tables;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import server.StarNub;
import server.connectedentities.player.account.AccountPermission;
import server.database.TableWrapper;

import java.sql.SQLException;
import java.util.List;

public class AccountPermissions extends TableWrapper<AccountPermission, Integer> {

    public AccountPermissions(Class<AccountPermission> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public AccountPermissions(ConnectionSource connectionSource, int oldVersion, Class<AccountPermission> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }

    public List<AccountPermission> getAccountPermissions(int starnubId){
        try {
            return getTableDao().queryBuilder().where()
                    .eq("STARNUB_ID", starnubId)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    public AccountPermission getAccountPermission(int starnubId, String permissionString) {
        AccountPermission permission = null;
        try {
            QueryBuilder<AccountPermission, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<AccountPermission, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .eq("STARNUB_ID", starnubId)
                    .and()
                    .like("PERMISSION", permissionString);
            PreparedQuery<AccountPermission> preparedQuery = queryBuilder.prepare();
            permission = getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return permission;
    }
}
