package org.starnub.server.database.tables;

import com.j256.ormlite.support.ConnectionSource;
import org.starnub.server.connectedentities.player.session.Restrictions;
import org.starnub.server.database.TableWrapper;

import java.sql.SQLException;

public class PlayerSessionRestrictions extends TableWrapper<Restrictions, Integer> {

    public PlayerSessionRestrictions(Class<Restrictions> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public PlayerSessionRestrictions(ConnectionSource connectionSource, int oldVersion, Class<Restrictions> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {
//        updateTable("ALTER TABLE `PLAYER_RESTRICTIONS` ADD COLUMN `NAME` VARCHAR(255);");
    }
}