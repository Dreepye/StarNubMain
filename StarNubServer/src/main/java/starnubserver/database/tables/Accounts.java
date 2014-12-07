/*
 *
 *  Copyright (C) 2014 www.StarNub.org - Underbalanced
 *
 *  This utilities.file is part of org.starnub a Java Wrapper for Starbound.
 *
 *  This above mentioned StarNub software is free software:
 *  you can redistribute it and/or modify it under the terms
 *  of the GNU General Public License as published by the Free
 *  Software Foundation, either version  3 of the License, or
 *  any later version. This above mentioned CodeHome software
 *  is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 *  the GNU General Public License for more details. You should
 *  have received a copy of the GNU General Public License in
 *  this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package starnubserver.database.tables;

import com.j256.ormlite.support.ConnectionSource;
import starnubserver.connections.player.account.Account;
import starnubserver.database.DatabaseConnection;
import starnubserver.database.TableWrapper;

import java.sql.SQLException;

/**
 * Represents Accounts Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Accounts extends TableWrapper<Account, Integer> {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final Accounts instance = new Accounts();

    /**
     * This constructor is private - Singleton Pattern
     */
    private Accounts(){
        super(DatabaseConnection.getConnection(), 0, Account.class, Integer.class);
    }

    public static Accounts getInstance() {
        return instance;
    }


    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException{
        if (oldVersion == 0) {
//            updateTable("ALTER TABLE `ACCOUNTS` ADD COLUMN `DISABLED` BOOLEAN;");
//            updateTable("ALTER TABLE `ACCOUNTS` ADD COLUMN `DISABLED_DATE` BIGINT;");
//            updateTable("ALTER TABLE `ACCOUNTS` ADD COLUMN `DISABLED_REASON` VARCHAR(255);");
//            updateTable("ALTER TABLE `ACCOUNTS` ADD COLUMN `DISABLED_EXPIRE_DATE` BIGINT;");
//            updateTable("ALTER TABLE `ACCOUNTS` ADD COLUMN `IMPOSER_NAME` VARCHAR(255);");
//            updateTable("ALTER TABLE `ACCOUNTS` ADD COLUMN `IMPOSER_STARNUB_ID` INT;");
        }
    }
}