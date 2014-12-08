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

package starnubserver.connections.player.groups;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import starnubserver.database.tables.GroupInheritances;

import java.util.List;

@DatabaseTable(tableName = "GROUP_INHERITANCE")
public class GroupInheritance {

    private final static GroupInheritances GROUP_INHERITANCES_DB = GroupInheritances.getInstance();

    /* COLUMN NAMES */
    private final static String GROUP_INHERITANCE_ID_COLUMN = "GROUP_INHERITANCE_ID";
    private final static String GROUP_ID_COLUMN = "GROUP_ID";
    private final static String INHERITED_GROUP_ID_COLUMN = "INHERITED_GROUP_ID";

    @DatabaseField(generatedId =true, dataType = DataType.INTEGER, columnName = GROUP_INHERITANCE_ID_COLUMN)
    private volatile int groupInheritanceId;

    @DatabaseField(foreign = true, columnName = GROUP_ID_COLUMN)
    private volatile Group group;

    @DatabaseField(foreign = true, columnName = INHERITED_GROUP_ID_COLUMN)
    private volatile Group inheritedGroup;

    /**
     * Constructor for database purposes
     */
    public GroupInheritance(){}

    /**
     * Constructor used in adding, removing or updating a group inheritance
     * @param group int representing main group that is inheriting a group
     * @param inheritedGroup int representing the group that is being inherited
     * @param createEntry boolean representing if a database entry should be made
     */
    public GroupInheritance(Group group, Group inheritedGroup, boolean createEntry) {
        this.group = group;
        this.inheritedGroup = inheritedGroup;
        if(createEntry){
            GROUP_INHERITANCES_DB.createOrUpdate(this);
        }
    }

    public int getGroupInheritanceId() {
        return groupInheritanceId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
        GROUP_INHERITANCES_DB.update(this);
    }

    public Group getInheritedGroup() {
        return inheritedGroup;
    }

    public void setInheritedGroup(Group inheritedGroup) {
        this.inheritedGroup = inheritedGroup;
        GROUP_INHERITANCES_DB.update(this);
    }

    /* DB METHODS */

    public GroupInheritance getGroupInheritanceByGroupFirstMatch() {
        return getGroupInheritanceByGroupFirstMatch(this.group, this.inheritedGroup);
    }

    public static GroupInheritance getGroupInheritanceByGroupFirstMatch(Group group, Group inheritedGroup) {
        return GROUP_INHERITANCES_DB.getMatchingColumn1FirstSimilarColumn2(GROUP_ID_COLUMN, group, INHERITED_GROUP_ID_COLUMN, inheritedGroup);
    }

    public List<GroupInheritance> getGroupInheritanceByGroup(){
        return getGroupInheritanceByGroup(this.group);
    }

    public static List<GroupInheritance> getGroupInheritanceByGroup(Group group){
        return GROUP_INHERITANCES_DB.getAllExact(GROUP_ID_COLUMN, group);
    }

    public List<GroupInheritance> getGroupInheritanceByInheritedGroup(){
        return getGroupInheritanceByInheritedGroup(this.inheritedGroup);
    }

    public static List<GroupInheritance> getGroupInheritanceByInheritedGroup(Group inheritedGroup){
        return GROUP_INHERITANCES_DB.getAllExact(INHERITED_GROUP_ID_COLUMN, inheritedGroup);
    }

    public void deleteFromDatabase(){
        deleteFromDatabase(this);
    }

    public static void deleteFromDatabase(GroupInheritance groupInheritance){
        GROUP_INHERITANCES_DB.delete(groupInheritance);
    }

    @Override
    public String toString() {
        return "GroupInheritance{" +
                "groupInheritanceId=" + groupInheritanceId +
                ", group=" + group +
                ", inheritedGroup=" + inheritedGroup +
                '}';
    }
}
