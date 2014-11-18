package org.starnub.server.database;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class ConnectionFactory {

    public ConnectionSource setConnection(String pluginName, String databaseType, String databaseUrl, String databaseUsername, String databasePassword) {
        ConnectionSource connection = null;
        String connectionString = "jdbc:";
        if (databaseType.equals("sqlite")) {
            try {
                connectionString = connectionString+databaseType+":StarNub/Databases/"+pluginName+".db";
                connection = new JdbcPooledConnectionSource(connectionString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (databaseType.equals("mysql")) {
            try {
                connectionString = connectionString+databaseType+"://"+databaseUrl;
                connection = new JdbcPooledConnectionSource(connectionString, databaseUsername, databasePassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
