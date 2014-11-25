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

package server.connections.player.account;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ACCOUNT_PERMISSIONS")
public class AccountPermission {

    /**
     * Represents a character permission id
     */
    @DatabaseField(generatedId =true, columnName = "PERMISSION_ID")
    private int permissionId;

    /**
     * Represents a starnubId that the permission is attached to
     */

    @DatabaseField(dataType = DataType.INTEGER, uniqueCombo = true, columnName = "STARNUB_ID")
    private int starnubId;

    /**
     * Represents a permission which is assigned to the starnubdId
     */

    @DatabaseField(dataType = DataType.STRING, uniqueCombo = true, columnName = "PERMISSION")
    private String permission;

    /**
     * Constructor for database purposes
     */
    public AccountPermission(){}

    public int getPermissionId() {
        return permissionId;
    }

    public int getStarnubId() {
        return starnubId;
    }

    public String getPermission() {
        return permission;
    }

    /**
     * Constructor used in adding, removing or updating a permission for an account
     * @param starnubId
     * @param permission
     */
    public AccountPermission(int starnubId, String permission) {
        this.starnubId = starnubId;
        this.permission = permission;
    }
}
