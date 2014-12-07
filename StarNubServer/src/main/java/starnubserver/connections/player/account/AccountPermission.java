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

package starnubserver.connections.player.account;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.exception.ExceptionUtils;
import starnubserver.StarNub;
import starnubserver.database.tables.AccountPermissions;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

/**
 * This class represents an account permission.
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 *
 */
@DatabaseTable(tableName = "ACCOUNT_PERMISSIONS")
public class AccountPermission implements Serializable {

    private final static AccountPermissions ACCOUNT_PERMISSIONS_DB = AccountPermissions.getInstance();

    /* COLUMN NAMES */
    private final String PERMISSION_ID_COLUMN = "PERMISSION_ID";
    private final String STARNUB_ID_COLUMN = "STARNUB_ID";
    private final String PERMISSION = "PERMISSION";

    @DatabaseField(dataType = DataType.INTEGER, generatedId =true, columnName = PERMISSION_ID_COLUMN)
    private volatile int permissionId;

    @DatabaseField(uniqueCombo = true, columnName = STARNUB_ID_COLUMN)
    private volatile Account starnubId;

    @DatabaseField(dataType = DataType.STRING, uniqueCombo = true, columnName = PERMISSION)
    private volatile String permission;

    /**
     * Constructor for database purposes
     */
    public AccountPermission(){}

    /**
     * This will create a account permission
     *
     * @param starnubId Account representing the account belong to this permission
     * @param permission String representing the permission
     * @param createEntry boolean representing if a database entry should be made
     */
    public AccountPermission(Account starnubId, String permission, boolean createEntry) {
        this.starnubId = starnubId;
        this.permission = permission;
        if (createEntry){
            ACCOUNT_PERMISSIONS_DB.createOrUpdate(this);
        }
    }

    public int getPermissionId() {
        return permissionId;
    }

    public Account getStarnubId() {
        return starnubId;
    }

    public String getPermission() {
        return permission;
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

    @Override
    public String toString() {
        return "AccountPermission{" +
                "permissionId=" + permissionId +
                ", starnubId=" + starnubId +
                ", permission='" + permission + '\'' +
                '}';
    }
}
