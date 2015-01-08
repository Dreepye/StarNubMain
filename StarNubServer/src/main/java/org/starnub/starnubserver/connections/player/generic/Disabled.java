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
import org.joda.time.DateTime;
import org.starnub.starnubserver.connections.player.account.Account;
import org.starnub.starnubserver.database.tables.Disables;
import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.connections.player.account.Account;
import org.starnub.starnubserver.database.tables.Disables;
import org.starnub.starnubserver.events.events.StarNubEvent;

/**
 * This class represents a disabled to be used with various classes
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 *
 */
@DatabaseTable(tableName = "DISABLES")
public class Disabled {

    private final static Disables DISABLES_DB = Disables.getInstance();

    //TODO DB COLUMNS - METHODS

    @DatabaseField(dataType = DataType.STRING, columnName = "ACCOUNT_NAME")
    private String accountNameId;

    @DatabaseField(foreign = true, columnName = "STARNUB_ID")
    private volatile Account account;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "DATE")
    private volatile DateTime date;

    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "EXPIRE_DATE")
    private volatile DateTime dateExpires;

    @DatabaseField(foreign = true, columnName = "STAFF_ENTRY")
    private volatile StaffEntry staffEntry;

    /**
     * Constructor for database purposes
     */
    public Disabled() {}

    /**
     * This will create a Disabled
     *
     * @param account Account representing the account
     * @param date DateTime representing the date of disable
     * @param dateExpires DateTime representing the date this disable expires
     * @param staffEntry StaffEntry representing information on this entry
     * @param createEntry boolean representing if we should create this entry on construction
     */
    public Disabled(Account account,  DateTime date, DateTime dateExpires, StaffEntry staffEntry, boolean createEntry) {
        this.accountNameId = account.getAccountName();
        this.account = account;
        this.date = date;
        this.dateExpires = dateExpires;
        this.staffEntry = staffEntry;
        if (createEntry){
            DISABLES_DB.createOrUpdate(this);
        }
    }

    public String getAccountNameId() {
        return accountNameId;
    }

    public void setAccountNameId(String accountNameId) {
        this.accountNameId = accountNameId;
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

    public DateTime getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(DateTime dateExpires) {
        this.dateExpires = dateExpires;
    }

    public StaffEntry getStaffEntry() {
        return staffEntry;
    }

    public void setStaffEntry(StaffEntry staffEntry) {
        this.staffEntry = staffEntry;
    }

    public void addDisable() {
        DISABLES_DB.createOrUpdate(this);
        StarNub.getLogger().cInfoPrint("StarNub", "Account was disabled. Account Name:  " + accountNameId + ".");
        new StarNubEvent("StarNub_Account_Disabled", this);
    }

    public void removeDisable() {
        new DisabledHistory(this, true);
        DISABLES_DB.delete(this);
        StarNub.getLogger().cInfoPrint("StarNub", "Account was re-instated. Account Name:  " + accountNameId + ".");
        new StarNubEvent("StarNub_Account_Enabled", this);
    }

    @Override
    public String toString() {
        return "Disabled{" +
                "accountNameId='" + accountNameId + '\'' +
                ", account=" + account +
                ", date=" + date +
                ", dateExpires=" + dateExpires +
                ", staffEntry=" + staffEntry +
                '}';
    }
}
