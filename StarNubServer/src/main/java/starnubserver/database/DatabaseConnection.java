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
    private ConnectionSource connection;

    /**
     * This constructor is private - Singleton Pattern
     */
    private DatabaseConnection(){
        String connectionString = "jdbc:";
        String databaseType = (((String) (StarNub.getConfiguration().getNestedValue("database", "type"))).toLowerCase());
        if (databaseType.equals("sqlite")) {
            try {
                connectionString = connectionString+databaseType+":StarNub/Databases/StarNub.db";
                connection = new JdbcPooledConnectionSource(connectionString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (databaseType.equals("mysql")) {
            try {
                String databaseUsername = ((String) (StarNub.getConfiguration().getNestedValue("database", "mysql_user")));
                String databasePassword = ((String) (StarNub.getConfiguration().getNestedValue("database", "mysql_pass")));
                String databaseUrl = ((String) (StarNub.getConfiguration().getNestedValue("database", "mysql_url")));
                connectionString = connectionString+databaseType+"://"+databaseUrl;
                connection = new JdbcPooledConnectionSource(connectionString, databaseUsername, databasePassword);
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

    public ConnectionSource getConnection() {
        return connection;
    }

    @Override
    public String toString() {
        return "DatabaseConnection{}";
    }
}
