package org.starnub.server.database.tables;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehome.utilities.crypto.PasswordHash;
import org.starnub.server.StarNub;
import org.starnub.server.connectedentities.player.account.Account;
import org.starnub.server.database.TableWrapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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