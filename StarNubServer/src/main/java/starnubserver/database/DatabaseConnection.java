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

package starnubserver.database;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import starnubserver.StarNub;

import java.sql.SQLException;

/**
 * Represents StarNubs Database Tables
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class DatabaseConnection {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final DatabaseConnection instance = new DatabaseConnection();

    private ConnectionSource starnubConnection;
    private ConnectionSource commonMySQLConnection;
    private ConnectionSource commonMySQLiteConnection;

    /**
     * This constructor is private - Singleton Pattern
     */
    private DatabaseConnection(){
        String databaseType = (((String) (StarNub.getConfiguration().getNestedValue("databases", "starnub", "type"))).toLowerCase());
        if (databaseType.equals("sqlite")) {
            try {
                starnubConnection = getNewPluginSQLLiteConnection("StarNub");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (databaseType.equals("mysql")) {
            try {
                starnubConnection = getStarNubMySQLConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @return DateAndTimes Singleton Instance
     */
    public static DatabaseConnection getInstance() {
        return instance;
    }

    public ConnectionSource getStarnubConnection() {
        return starnubConnection;
    }

    public ConnectionSource getCommonMySQLiteConnection() throws SQLException {
        if (commonMySQLiteConnection == null){
            commonMySQLiteConnection = getNewPluginSQLLiteConnection("CommonDatabase");
        }
        return commonMySQLiteConnection;
    }

    public ConnectionSource getNewPluginSQLLiteConnection(String name) throws SQLException {
        return getSQLLiteConnection("Databases/" + name + ".db");
    }

    private ConnectionSource getSQLLiteConnection(String path) throws SQLException {
        String connectionString = "jdbc:sqlite:StarNub/" + path;
        return new JdbcPooledConnectionSource(connectionString);
    }

    public ConnectionSource getCommonPluginMySQLConnection() throws SQLException {
        boolean canCommon = ((String) (StarNub.getConfiguration().getNestedValue("databases", "common", "type"))).equalsIgnoreCase("mysql");
        if (canCommon) {
            if (commonMySQLConnection == null) {
                String databaseUsername = ((String) (StarNub.getConfiguration().getNestedValue("databases", "common", "mysql_user")));
                String databasePassword = ((String) (StarNub.getConfiguration().getNestedValue("databases", "common", "mysql_pass")));
                String databaseUrl = ((String) (StarNub.getConfiguration().getNestedValue("databases", "common", "mysql_url")));
                commonMySQLConnection = getNewMySQLConnection(databaseUrl, databaseUsername, databasePassword);
            } else {
                return commonMySQLConnection;
            }
        }
        return null;
    }

    private ConnectionSource getStarNubMySQLConnection() throws SQLException {
            String databaseUsername = ((String) (StarNub.getConfiguration().getNestedValue("databases", "starnub", "mysql_user")));
            String databasePassword = ((String) (StarNub.getConfiguration().getNestedValue("databases", "starnub", "mysql_pass")));
            String databaseUrl = ((String) (StarNub.getConfiguration().getNestedValue("databases", "starnub", "mysql_url")));
            return getNewMySQLConnection(databaseUrl, databaseUsername, databasePassword);
    }

    public ConnectionSource getNewMySQLConnection(String url, String user, String password) throws SQLException {
        String connectionString = "jdbc:mysql://" + url;
        return new JdbcPooledConnectionSource(connectionString, user, password);
    }

    public boolean isCommonMySQL(){
        return commonMySQLConnection !=null;
    }

    public boolean isCommonSQLite(){
        return commonMySQLiteConnection !=null;
    }

    @Override
    public String toString() {
        return "DatabaseConnection{}";
    }
}
