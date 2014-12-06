/*
 * Copyright (C) 2014 www.StarNub.org - Underbalanced
 *
 * This file is part of org.starnub a Java Wrapper for Starbound.
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

package starnubserver.connections.player.generic;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import starnubserver.StarNub;
import starnubserver.connections.player.account.Account;

/**
 * This class represents a staff entry to be used with various classes
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 *
 */
@DatabaseTable(tableName = "STAFF_ENTRIES")
public class StaffEntry {

    @DatabaseField(dataType = DataType.INTEGER, generatedId =true, columnName = "ENTRY_ID")
    private volatile int staffEntryId;

    @DatabaseField(foreign = true, columnName = "ACCOUNT")
    private volatile Account staffAccount;

    @DatabaseField(dataType = DataType.STRING, columnName = "DESCRIPTION")
    private volatile String staffDescription;

    /**
     * Constructor for database purposes
     */
    public StaffEntry(){}

    /**
     * This will construct a staff entry which can be used with various classes
     *
     * @param staffAccount Account representing the staff members account
     * @param staffDescription String representing the description for this staff entry
     * @param createEntry boolean representing if a database entry should be made
     */
    public StaffEntry(Account staffAccount, String staffDescription, boolean createEntry) {
        this.staffAccount = staffAccount;
        this.staffDescription = staffDescription;
        if (createEntry){
            StarNub.getDatabaseTables().getStaffEntries().createOrUpdate(this);
        }
    }

    @Override
    public String toString() {
        return "StaffEntry{" +
                "staffEntryId=" + staffEntryId +
                ", staffAccount=" + staffAccount +
                ", staffDescription='" + staffDescription + '\'' +
                '}';
    }
}
