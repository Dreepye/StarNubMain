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

package org.starnub.starnubserver.connections.player.groups;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.starnub.starnubserver.connections.player.account.Account;
import org.starnub.starnubserver.connections.player.account.Account;
import org.starnub.starnubserver.database.tables.GroupAssignments;

import java.util.List;

@DatabaseTable(tableName = "GROUP_ASSIGNMENTS")
public class GroupAssignment {

    private final static GroupAssignments GROUP_ASSIGNMENTS_DB = GroupAssignments.getInstance();

    /* COLUMN NAMES */
    private final static String GROUP_ASSIGNMENT_ID_COLUMN = "GROUP_ASSIGNMENT_ID";
    private final static String STARNUB_ID_COLUMN = "STARNUB_ID";
    private final static String GROUP_ID_COLUMN = "GROUP_ID";

    @DatabaseField(generatedId =true, dataType = DataType.INTEGER, columnName = GROUP_ASSIGNMENT_ID_COLUMN)
    private volatile int groupAssignmentId;

    @DatabaseField(foreign = true, uniqueCombo = true, foreignAutoRefresh = true, columnName = STARNUB_ID_COLUMN)
    private volatile Account account;

    @DatabaseField(foreign = true, uniqueCombo = true, foreignAutoRefresh = true, columnName = GROUP_ID_COLUMN)
    private volatile Group group;

    /**
     * Constructor for database purposes
     */
    public GroupAssignment(){}

    /**
     * Constructor used in adding, removing or updating a group assignment for a account
     * @param account int representing the account id
     * @param group int representing the group id
     * @param createEntry boolean representing if a database entry should be made
     */
    public GroupAssignment(Account account, Group group, boolean createEntry) {
        this.account = account;
        this.group = group;
        if(createEntry){
            GroupAssignment groupAssignment = GroupAssignment.getGroupAssigmentByGroupFirstMatch(account, group);
            if (groupAssignment == null) {
                GROUP_ASSIGNMENTS_DB.create(this);
            }
        }
    }

    public int getGroupAssignmentId() {
        return groupAssignmentId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
        GROUP_ASSIGNMENTS_DB.update(this);
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
        GROUP_ASSIGNMENTS_DB.update(this);
    }

    /* DB METHODS */

    public GroupAssignment getGroupAssigmentByGroupFirstMatch() {
        return getGroupAssigmentByGroupFirstMatch(this.account, this.group);
    }

    public static GroupAssignment getGroupAssigmentByGroupFirstMatch(Account account, Group group) {
        return GROUP_ASSIGNMENTS_DB.getMatchingColumn1FirstSimilarColumn2(STARNUB_ID_COLUMN, account, GROUP_ID_COLUMN, group);
    }

    public List<GroupAssignment> getGroupAssigmentByAccount(){
        return getGroupAssignmentByAccount(this.account);
    }

    public static List<GroupAssignment> getGroupAssignmentByAccount(Account account){
        return GROUP_ASSIGNMENTS_DB.getAllExact(STARNUB_ID_COLUMN, account);
    }

    public List<GroupAssignment> getGroupAssigmentByGroup(){
        return getGroupAssignmentByGroup(this.group);
    }

    public static List<GroupAssignment> getGroupAssignmentByGroup(Group group){
        return GROUP_ASSIGNMENTS_DB.getAllExact(GROUP_ID_COLUMN, group);
    }

    public void deleteFromDatabase(){
        deleteFromDatabase(this);
    }

    public static void deleteFromDatabase(GroupAssignment groupAssignment){
        GROUP_ASSIGNMENTS_DB.delete(groupAssignment);
    }

    @Override
    public String toString() {
        return "GroupAssignment{" +
                "groupAssignmentId=" + groupAssignmentId +
                ", account=" + account +
                ", group=" + group +
                '}';
    }
}
