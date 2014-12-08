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
import com.j256.ormlite.table.DatabaseTable;
import starnubserver.database.tables.AccountPermissions;

import java.io.Serializable;
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
    private final static String PERMISSION_ID_COLUMN = "PERMISSION_ID";
    private final static String STARNUB_ID_COLUMN = "STARNUB_ID";
    private final static String PERMISSION_COLUMN = "PERMISSION_COLUMN";

    @DatabaseField(dataType = DataType.INTEGER, generatedId =true, columnName = PERMISSION_ID_COLUMN)
    private volatile int permissionId;

    @DatabaseField(uniqueCombo = true, columnName = STARNUB_ID_COLUMN)
    private volatile Account account;

    @DatabaseField(dataType = DataType.STRING, uniqueCombo = true, columnName = PERMISSION_COLUMN)
    private volatile String permission;

    /**
     * Constructor for database purposes
     */
    public AccountPermission(){}

    /**
     * This will create a account permission
     *
     * @param account Account representing the account belong to this permission
     * @param permission String representing the permission
     * @param createEntry boolean representing if a database entry should be made
     */
    public AccountPermission(Account account, String permission, boolean createEntry) {
        this.account = account;
        this.permission = permission;
        if (createEntry){
            ACCOUNT_PERMISSIONS_DB.createOrUpdate(this);
        }
    }

    public int getPermissionId() {
        return permissionId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    /* DB Methods */

    public AccountPermission getAccountPermissionByGroupFirstMatch () {
        return getAccountPermissionByAccountFirstMatch(this.account, this.permission);
    }

    public static AccountPermission getAccountPermissionByAccountFirstMatch (Account account, String permission) {
        return ACCOUNT_PERMISSIONS_DB.getMatchingColumn1FirstSimilarColumn2(STARNUB_ID_COLUMN, account, PERMISSION_ID_COLUMN, permission);
    }

    public List<AccountPermission> getAccountPermissionsByAccount(){
        return getAccountPermissionsByAccount(this.account);
    }

    public static List<AccountPermission> getAccountPermissionsByAccount(Account account){
        return ACCOUNT_PERMISSIONS_DB.getAllExact(STARNUB_ID_COLUMN, account);
    }

    public List<AccountPermission> getAccountPermissionsByPermission(){
        return getAccountPermissionsByPermission(this.permission);
    }

    public static List<AccountPermission> getAccountPermissionsByPermission(String permission){
        return ACCOUNT_PERMISSIONS_DB.getAllExact(PERMISSION_COLUMN, permission);
    }

    public void deleteFromDatabase(){
        deleteFromDatabase(this);
    }

    public static void deleteFromDatabase(AccountPermission accountPermission){
        ACCOUNT_PERMISSIONS_DB.delete(accountPermission);
    }

    @Override
    public String toString() {
        return "AccountPermission{" +
                "permissionId=" + permissionId +
                ", account=" + account +
                ", permission='" + permission + '\'' +
                '}';
    }
}
