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
import starnubserver.connections.player.generic.Tag;
import starnubserver.database.tables.AccountSettings;

import java.io.Serializable;

/**
 * This class represents a staff entry to be used with various classes
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 *
 */
@DatabaseTable(tableName = "ACCOUNT_SETTINGS")
public class Settings implements Serializable {

    private final static AccountSettings ACCOUNT_SETTINGS_DB = AccountSettings.getInstance();

    //TODO DB COLUMNS - METHODS

    /* COLUMN NAMES */
    private final static String ACCOUNT_SETTINGS_ID_COLUMN = "ACCOUNT_SETTINGS_ID";
    private final static String CHAT_PREFIX_1_COLUMN = "CHAT_PREFIX_1";
    private final static String CHAT_PREFIX_2_COLUMN = "CHAT_PREFIX_2";
    private final static String CHAT_SUFFIX_1_COLUMN = "CHAT_SUFFIX_1";
    private final static String CHAT_SUFFIX_2_COLUMN = "CHAT_SUFFIX_2";
    private final static String APPEAR_OFFLINE_COLUMN = "APPEAR_OFFLINE";

    @DatabaseField(id = true, dataType = DataType.STRING, columnName = ACCOUNT_SETTINGS_ID_COLUMN)
    private volatile String accountSettings;

    @DatabaseField(canBeNull = true, foreign = true, columnName = CHAT_PREFIX_1_COLUMN)
    private volatile Tag chatPrefix1;

    @DatabaseField(canBeNull = true, foreign = true, columnName = CHAT_PREFIX_2_COLUMN)
    private volatile Tag chatPrefix2;

    @DatabaseField(canBeNull = true, foreign = true, columnName = CHAT_SUFFIX_1_COLUMN)
    private volatile Tag chatSuffix1;

    @DatabaseField(canBeNull = true, foreign = true, columnName = CHAT_SUFFIX_2_COLUMN)
    private volatile Tag chatSuffix2;

    @DatabaseField(dataType = DataType.BOOLEAN, columnName = APPEAR_OFFLINE_COLUMN)
    private volatile boolean appearOffline;

    /**
     * Constructor for database purposes
     */
    public Settings(){}

    /**
     * This will construct an account setting using the account name as a key
     *
     * @param accountName String representing the account name these settings belong to
     * @param createEntry boolean representing if we should create this entry on construction
     */
    public Settings(String accountName, boolean createEntry) {
        this.accountSettings = accountName;
        if (createEntry) {
            ACCOUNT_SETTINGS_DB.createOrUpdate(this);
        }
    }

    public void refreshSettings(boolean tags) {
        ACCOUNT_SETTINGS_DB.refresh(this);
        if (tags) {
            chatPrefix1.refreshTag();
            chatPrefix2.refreshTag();
            chatSuffix1.refreshTag();
            chatSuffix2.refreshTag();
        }
    }

    public String getAccountSettings() {
        return accountSettings;
    }

    public Tag getChatPrefix1() {
        return chatPrefix1;
    }

    public void setChatPrefix1(Tag chatPrefix1) {
        this.chatPrefix1 = chatPrefix1;
        ACCOUNT_SETTINGS_DB.update(this);
    }

    public Tag getChatPrefix2() {
        return chatPrefix2;
    }

    public void setChatPrefix2(Tag chatPrefix2) {
        this.chatPrefix2 = chatPrefix2;
        ACCOUNT_SETTINGS_DB.update(this);
    }

    public Tag getChatSuffix1() {
        return chatSuffix1;
    }

    public void setChatSuffix1(Tag chatSuffix1) {
        this.chatSuffix1 = chatSuffix1;
        ACCOUNT_SETTINGS_DB.update(this);
    }

    public Tag getChatSuffix2() {
        return chatSuffix2;
    }

    public void setChatSuffix2(Tag chatSuffix2) {
        this.chatSuffix2 = chatSuffix2;
        ACCOUNT_SETTINGS_DB.update(this);
    }

    public boolean isAppearOffline() {
        return appearOffline;
    }

    public void makeAppearOffline() {
        this.appearOffline = true;
        ACCOUNT_SETTINGS_DB.update(this);
    }

    public void removeAppearOffline() {
        this.appearOffline = false;
        ACCOUNT_SETTINGS_DB.update(this);
    }

    public boolean isUsingTag(String prefixOrSuffix){
        return chatPrefix1.getName().equalsIgnoreCase(prefixOrSuffix) || chatPrefix2.getName().equalsIgnoreCase(prefixOrSuffix)
                || chatSuffix1.getName().equalsIgnoreCase(prefixOrSuffix) || chatSuffix2.getName().equalsIgnoreCase(prefixOrSuffix);
    }

    public void deleteFromDatabase(){
        deleteFromDatabase(this);
    }

    public static void deleteFromDatabase(Settings settings){
        ACCOUNT_SETTINGS_DB.delete(settings);
    }
}
