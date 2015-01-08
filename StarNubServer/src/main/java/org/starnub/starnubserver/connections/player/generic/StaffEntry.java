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

package org.starnub.starnubserver.connections.player.generic;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.starnub.starnubserver.connections.player.account.Account;
import org.starnub.starnubserver.database.tables.StaffEntries;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents a staff entry to be used with various classes
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 *
 */
@DatabaseTable(tableName = "STAFF_ENTRIES")
public class StaffEntry implements Serializable{

    private final static StaffEntries STAFF_ENTRIES_DB = StaffEntries.getInstance();

    /* COLUMN NAMES */
    private final static String STAFF_ENTRY_ID_COLUMN = "STAFF_ENTRY_ID";
    private final static String STARNUB_ID_COLUMN = "STARNUB_ID";
    private final static String DESCRIPTION_COLUMN = "DESCRIPTION";

    @DatabaseField(dataType = DataType.INTEGER, generatedId =true, columnName = STAFF_ENTRY_ID_COLUMN)
    private volatile int staffEntryId;

    @DatabaseField(foreign = true, columnName = STARNUB_ID_COLUMN)
    private volatile Account account;

    @DatabaseField(dataType = DataType.STRING, columnName = DESCRIPTION_COLUMN)
    private volatile String description;

    /**
     * Constructor for database purposes
     */
    public StaffEntry(){}

    /**
     * This will construct a staff entry which can be used with various classes
     *
     * @param account Account representing the staff members account
     * @param description String representing the description for this staff entry
     * @param createEntry boolean representing if a database entry should be made
     */
    public StaffEntry(Account account, String description, boolean createEntry) {
        this.account = account;
        this.description = description;
        if (createEntry){
            STAFF_ENTRIES_DB.createOrUpdate(this);
        }
    }

    /* DB Methods */

    public List<StaffEntry> getStaffEntryByAccount(){
        return getStaffEntryByAccount(this.account);
    }

    public static List<StaffEntry> getStaffEntryByAccount(Account account){
        return STAFF_ENTRIES_DB.getAllExact(STARNUB_ID_COLUMN, account);
    }

    public List<StaffEntry> getStaffEntryByStaffEntry(){
        return getStaffEntryByStaffEntry(this);
    }

    public static List<StaffEntry> getStaffEntryByStaffEntry(StaffEntry staffEntry){
        return STAFF_ENTRIES_DB.getAllExact(STAFF_ENTRY_ID_COLUMN, staffEntry);
    }

    public void deleteFromDatabase(){
        deleteFromDatabase(this);
    }

    public static void deleteFromDatabase(StaffEntry accountPermission){
        STAFF_ENTRIES_DB.delete(accountPermission);
    }

    @Override
    public String toString() {
        return "StaffEntry{" +
                "staffEntryId=" + staffEntryId +
                ", account=" + account +
                ", description='" + description + '\'' +
                '}';
    }
}
