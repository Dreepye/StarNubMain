package server.database.tables;

import com.j256.ormlite.support.ConnectionSource;
import server.connectedentities.player.session.Player;
import server.database.TableWrapper;

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
