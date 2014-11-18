package server.database.tables;

import com.j256.ormlite.support.ConnectionSource;
import server.connectedentities.player.account.Settings;
import server.database.TableWrapper;

import java.sql.SQLException;

public class AccountSettings extends TableWrapper<Settings, String> {

    public AccountSettings(Class<Settings> typeParameterDBClass, Class<String> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public AccountSettings(ConnectionSource connectionSource, int oldVersion, Class<Settings> typeParameterDBClass, Class<String> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }
}
