package server.database.tables;

import com.j256.ormlite.support.ConnectionSource;
import server.connectedentities.player.achievements.Achievement;
import server.database.TableWrapper;

import java.sql.SQLException;

public class Achievements extends TableWrapper<Achievement, Integer> {

    public Achievements(Class<Achievement> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public Achievements(ConnectionSource connectionSource, int oldVersion, Class<Achievement> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }
}
