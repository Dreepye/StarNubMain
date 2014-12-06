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
import starnubserver.database.tables.Tags;

@DatabaseTable(tableName = "TAGS")
public class Tag {

    private final static Tags TAGS_DB = Tags.getInstance();

    @DatabaseField(id = true, dataType = DataType.STRING, unique = true, columnName = "TAG")
    private volatile String name;


    @DatabaseField(dataType = DataType.STRING, columnName = "TAG_COLOR")
    private volatile String color;


    @DatabaseField(dataType = DataType.STRING, columnName = "TYPE")
    private volatile String typeOfTag;

    public Tag(){}

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getTypeOfTag() {
        return typeOfTag;
    }

    public Tag(String typeOfTag, String name, String color) {
        this.typeOfTag = typeOfTag;
        this.name = name;
//        this.color = StarNub.getMessageSender().getGameColors().validateColor(color);
        TAGS_DB.createOrUpdate(this);
    }

    public void setTypeOfTag(String typeOfTag) {
        this.typeOfTag = typeOfTag;
        TAGS_DB.update(this);
    }

    public void setName(String tag) {
        this.name = tag;
        TAGS_DB.update(this);
    }

    public void setColor(String color) {
        this.color = color;
        TAGS_DB.update(this);
    }

    public String getCleanTagNoBrackets(){
        return buildTag(false, false);
    }

    public String getCleanTag(){
        return buildTag(true, false);
    }

    public String getColorTagNoBrackets(){
        return buildTag(false, true);
    }

    public String getColorTag(){
        return buildTag(true, true);
    }

    private String buildTag(boolean brackets, boolean color){
        String tagString = name;
        if (color) {
           tagString = this.color + tagString;
        }
        if (brackets) {
//            Map conf = (Map) StarNub.getConfiguration().getConfiguration().get("groups");
//            String start = (String) conf.get("bracket_start");
//            String end = (String) conf.get("bracket_end");
//            if (color) {
////                String bracketColor = StarNub.getMessageSender().getGameColors().getBracketColor();
//                start = bracketColor + start;
//                end = bracketColor + end;
//            }
//            tagString = start + tagString + end;
        }
        return tagString;
    }
}
