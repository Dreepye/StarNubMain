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
import starbounddata.color.GameColors;
import starnubserver.database.tables.Tags;

import java.util.List;

@DatabaseTable(tableName = "TAGS")
public class Tag {

    private final static Tags TAGS_DB = Tags.getInstance();

    /* COLUMN NAMES */
    private final String TAG_ID_COLUMN = "TAG_ID";
    private final String COLOR_COLUMN = "COLOR";
    private final String TYPE_COLUMN = "TYPE";
    private final String LEFT_BRACKET_COLUMN = "LEFT_BRACKET";
    private final String RIGHT_BRACKET_COLUMN = "RIGHT_BRACKET";
    private final String BRACKET_COLOR_COLUMN = "BRACKET_COLOR";

    @DatabaseField(id = true, dataType = DataType.STRING, unique = true, columnName = TAG_ID_COLUMN)
    private volatile String name;

    @DatabaseField(dataType = DataType.STRING, columnName = COLOR_COLUMN)
    private volatile String color;

    @DatabaseField(dataType = DataType.STRING, columnName = TYPE_COLUMN)
    private volatile String type;

    @DatabaseField(dataType = DataType.STRING, columnName = LEFT_BRACKET_COLUMN)
    private volatile String leftBracket;

    @DatabaseField(dataType = DataType.STRING, columnName = RIGHT_BRACKET_COLUMN)
    private volatile String rightBracket;

    @DatabaseField(dataType = DataType.STRING, columnName = BRACKET_COLOR_COLUMN)
    private volatile String bracketColor;

    private volatile String builtGameTag;

    private volatile String builtConsoleTag;

    public Tag(){}

    public void refreshTag() {
        TAGS_DB.refresh(this);
        setTagCache(false);
    }

    public Tag(String name, String color, String type, String leftBracket, String rightBracket, String bracketColor) {
        this.name = name;
        this.type = type;
        this.color = GameColors.Colors.validate(color, true);
        this.leftBracket = leftBracket;
        this.rightBracket = rightBracket;
        this.bracketColor = bracketColor;
        setTagCache(true);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setTagCache(true);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        setTagCache(true);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        setTagCache(true);
    }

    public String getLeftBracket() {
        return leftBracket;
    }

    public void setLeftBracket(String leftBracket) {
        this.leftBracket = leftBracket;
        setTagCache(true);
    }

    public String getRightBracket() {
        return rightBracket;
    }

    public void setRightBracket(String rightBracket) {
        this.rightBracket = rightBracket;
        setTagCache(true);
    }

    public String getBracketColor() {
        return bracketColor;
    }

    public void setBracketColor(String bracketColor) {
        this.bracketColor = bracketColor;
        setTagCache(true);
    }

    public String getBuiltGameTag() {
        return builtGameTag;
    }

    public String getBuiltConsoleTag() {
        return builtConsoleTag;
    }

    private void setTagCache(boolean createOrUpdate){
        this.builtConsoleTag = rightBracket + name + leftBracket;
        this.builtGameTag =
                bracketColor + leftBracket +
                color + name +
                bracketColor + rightBracket +
                GameColors.getInstance().getDefaultChatColor();
        if (createOrUpdate) {
            TAGS_DB.createOrUpdate(this);
        }
    }

    /* DB METHODS */

    public Tag getTagFromDbById(String tagName){
        return TAGS_DB.getById(tagName);
    }

    public List<Tag> getMatchingTagsById(String tagName){
        return TAGS_DB.getAllSimilar(TAG_ID_COLUMN, tagName);
    }

    public List<Tag> getMatchingTagsByColor(String color){
        return TAGS_DB.getAllSimilar(COLOR_COLUMN, color);
    }

    public List<Tag> getMatchingTagsByType(String type){
        return TAGS_DB.getAllSimilar(TYPE_COLUMN, type);
    }

    public List<Tag> getMatchingTagsByIdSimiliarType(String tagName, String type){
        return TAGS_DB.getMatchingColumn1AllSimilarColumn2(TAG_ID_COLUMN, tagName, TYPE_COLUMN, type);
    }

    public void deleteFromDatabase(){
        deleteFromDatabase(this);
    }

    public static void deleteFromDatabase(Tag tag){
        TAGS_DB.delete(tag);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", type='" + type + '\'' +
                ", leftBracket='" + leftBracket + '\'' +
                ", rightBracket='" + rightBracket + '\'' +
                ", bracketColor='" + bracketColor + '\'' +
                ", builtGameTag='" + builtGameTag + '\'' +
                ", builtConsoleTag='" + builtConsoleTag + '\'' +
                '}';
    }
}
