package org.starnub.server.database.tables;

import com.j256.ormlite.support.ConnectionSource;
import org.starnub.server.connectedentities.player.session.Player;
import org.starnub.server.database.TableWrapper;

import java.sql.SQLException;

public class PlayerSessionLog extends TableWrapper<Player, Integer> {

    public PlayerSessionLog(Class<Player> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public PlayerSessionLog(ConnectionSource connectionSource, int oldVersion, Class<Player> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }
}
