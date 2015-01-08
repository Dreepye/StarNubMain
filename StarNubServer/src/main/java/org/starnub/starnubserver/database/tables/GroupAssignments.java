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

package org.starnub.starnubserver.database.tables;

import com.j256.ormlite.support.ConnectionSource;
import org.starnub.starnubserver.connections.player.groups.GroupAssignment;
import org.starnub.starnubserver.database.DatabaseConnection;
import org.starnub.starnubserver.connections.player.groups.GroupAssignment;
import org.starnub.starnubserver.database.DatabaseConnection;
import org.starnub.starnubserver.database.TableWrapper;

import java.sql.SQLException;

/**
 * Represents GroupAssignments Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class GroupAssignments extends TableWrapper<GroupAssignment, Integer> {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final GroupAssignments instance = new GroupAssignments();

    /**
     * This constructor is private - Singleton Pattern
     */
    private GroupAssignments(){
        super(DatabaseConnection.getInstance().getStarnubConnection(), 0, GroupAssignment.class, Integer.class);
    }

    public static GroupAssignments getInstance() {
        return instance;
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }
}
