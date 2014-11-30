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

package starnub.database.tables;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import starnub.StarNub;
import starnub.connections.player.account.Account;
import starnub.database.TableWrapper;
import utilities.crypto.PasswordHash;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents Accounts Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Accounts extends TableWrapper<Account, Integer> {

    public Accounts(Class<Account> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public Accounts(ConnectionSource connectionSource, int oldVersion, Class<Account> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
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

    public Account getAccountByName(String accountName) {
        try {
            QueryBuilder<Account, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<Account, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .like("ACCOUNT_NAME", accountName);
            PreparedQuery<Account> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public Account getAccount(String accountName, String password){
        List<Account> accountList = new ArrayList<>();
        try {
            accountList = getTableDao().queryBuilder().where()
                    .like("ACCOUNT_NAME", accountName)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        if (accountList.size() == 0) {
            return null;
        }
        PasswordHash passwordHash = new PasswordHash();
        boolean matchingHashPass = false;
        for (Account account : accountList) {
            try {
                matchingHashPass = passwordHash.check(password, account.getAccountPassword());
            } catch (Exception e) {
                StarNub.getLogger().cErrPrint("StarNub", "StarNub had a critical error trying to determine if a password hash from " +
                        "an account matched the input.");
            }
            if (matchingHashPass) {
                return account;
            }
        }
        return null;
    }



}