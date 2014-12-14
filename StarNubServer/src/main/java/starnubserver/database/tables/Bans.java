/*
 * Copyright (C) 2014 www.StarNub.org - Underbalanced
 *
 * This file is part of org.starnub a Java Wrapper for Starbound.
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

package starnubserver.database.tables;

import com.j256.ormlite.support.ConnectionSource;
import starnubserver.connections.player.generic.Ban;
import starnubserver.database.DatabaseConnection;
import starnubserver.database.TableWrapper;

import java.sql.SQLException;


/**
 * Represents Bans Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Bans extends TableWrapper<Ban, Integer> {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final Bans instance = new Bans();

    /**
     * This constructor is private - Singleton Pattern
     */
    private Bans(){
        super(DatabaseConnection.getInstance().getStarnubConnection(), 0, Ban.class, Integer.class);
    }

    public static Bans getInstance() {
        return instance;
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }
}
