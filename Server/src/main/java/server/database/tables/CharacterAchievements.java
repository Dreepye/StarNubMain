package server.database.tables;

import com.j256.ormlite.support.ConnectionSource;
import server.connectedentities.player.achievements.CharacterAchievement;
import server.database.TableWrapper;

import java.sql.SQLException;

public class CharacterAchievements extends TableWrapper<CharacterAchievement, Integer> {

    public CharacterAchievements(Class<CharacterAchievement> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public CharacterAchievements(ConnectionSource connectionSource, int oldVersion, Class<CharacterAchievement> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }
}