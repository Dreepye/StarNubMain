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
import org.joda.time.DateTime;
import starnubserver.connections.player.account.Account;
import starnubserver.database.tables.DisablesHistory;
import starnubserver.events.events.StarNubEvent;

/**
 * This class represents a disabled history
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 *
 */
@DatabaseTable(tableName = "DISABLES_HISTORY")
public class DisabledHistory {

    private final static DisablesHistory DISABLES_HISTORY_DB = DisablesHistory.getInstance();

    //TODO DB COLUMNS - METHODS

    @DatabaseField(dataType = DataType.INTEGER, generatedId =true, columnName = "DISABLED_ID")
    private int disabledHistoryId;

    @DatabaseField(foreign = true, columnName = "STARNUB_ID")
    private volatile Account account;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "DATE")
    private volatile DateTime date;

    @DatabaseField(foreign = true, columnName = "STAFF_ENTRY")
    private volatile StaffEntry staffEntry;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "DATE_REMOVED")
    private volatile DateTime dateRemoved;

    /**
     * Constructor for database purposes
     */
    public DisabledHistory() {}

    /**
     * This will create a Disabled History from a Disabled
     *
     * @param disabled Disabled which is to be placed in the disabled history
     * @param createEntry boolean representing if we should create this entry on construction
     */
    public DisabledHistory(Disabled disabled, boolean createEntry) {
        this.account = disabled.getAccount();
        this.date = disabled.getDate();
        this.staffEntry = disabled.getStaffEntry();
        this.dateRemoved = DateTime.now();
        if (createEntry){
            DISABLES_HISTORY_DB.createOrUpdate(this);
            new StarNubEvent("StarNub_Account_Disabled_History_Updated", this);
        }
    }

    public int getDisabledHistoryId() {
        return disabledHistoryId;
    }

    public void setDisabledHistoryId(int disabledHistoryId) {
        this.disabledHistoryId = disabledHistoryId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public StaffEntry getStaffEntry() {
        return staffEntry;
    }

    public void setStaffEntry(StaffEntry staffEntry) {
        this.staffEntry = staffEntry;
    }

    public DateTime getDateRemoved() {
        return dateRemoved;
    }

    public void setDateRemoved(DateTime dateRemoved) {
        this.dateRemoved = dateRemoved;
    }

    @Override
    public String toString() {
        return "DisabledHistory{" +
                "disabledHistoryId=" + disabledHistoryId +
                ", account=" + account +
                ", date=" + date +
                ", staffEntry=" + staffEntry +
                ", dateRemoved=" + dateRemoved +
                '}';
    }
}
