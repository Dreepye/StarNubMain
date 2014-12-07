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

package starnubserver.connections.player.account;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import starnubserver.StarNub;
import starnubserver.connections.player.groups.Group;
import starnubserver.connections.player.groups.GroupAssignment;
import starnubserver.connections.player.groups.Tag;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.database.tables.AccountSettings;
import starnubserver.database.tables.Groups;

import java.io.Serializable;

/**
 * This class represents a staff entry to be used with various classes
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 *
 */
@DatabaseTable(tableName = "ACCOUNT_SETTINGS")
public class Settings implements Serializable {

    private final static AccountSettings ACCOUNT_SETTINGS_DB = AccountSettings.getInstance();

    /* COLUMN NAMES */
    private final String ACCOUNT_SETTINGS_ID_COLUMN = "ACCOUNT_SETTINGS_ID";
    private final String CHAT_PREFIX_1_COLUMN = "CHAT_PREFIX_1";
    private final String CHAT_PREFIX_2_COLUMN = "CHAT_PREFIX_2";
    private final String CHAT_SUFFIX_1_COLUMN = "CHAT_SUFFIX_1";
    private final String CHAT_SUFFIX_2_COLUMN = "CHAT_SUFFIX_2";
    private final String APPEAR_OFFLINE_COLUMN = "APPEAR_OFFLINE";

    @DatabaseField(id = true, dataType = DataType.STRING, columnName = ACCOUNT_SETTINGS_ID_COLUMN)
    private volatile String accountSettings;

    @DatabaseField(canBeNull = true, foreign = true, columnName = CHAT_PREFIX_1_COLUMN)
    private volatile Tag chatPrefix1;


    @DatabaseField(canBeNull = true, foreign = true, columnName = CHAT_PREFIX_2_COLUMN)
    private volatile Tag chatPrefix2;


    @DatabaseField(canBeNull = true, foreign = true, columnName = CHAT_SUFFIX_1_COLUMN)
    private volatile Tag chatSuffix1;


    @DatabaseField(canBeNull = true, foreign = true, columnName = CHAT_SUFFIX_2_COLUMN)
    private volatile Tag chatSuffix2;


    @DatabaseField(dataType = DataType.BOOLEAN, columnName = APPEAR_OFFLINE_COLUMN)
    private volatile boolean appearOffline;

    public Settings(){}

    public Settings(String accountSettings) {
        this.accountSettings = accountSettings;
        ACCOUNT_SETTINGS_DB.createIfNotExist(this);
    }

    public void refreshSettings(boolean tags) {
        ACCOUNT_SETTINGS_DB.refresh(this);
        if (tags) {
            chatPrefix1.refreshTag();
            chatPrefix2.refreshTag();
            chatSuffix1.refreshTag();
            chatSuffix2.refreshTag();
        }
    }

    public String getAccountSettings() {
        return accountSettings;
    }

    public Tag getChatPrefix1() {
        return chatPrefix1;
    }

    public void setChatPrefix1(Tag chatPrefix1) {
        this.chatPrefix1 = chatPrefix1;
        ACCOUNT_SETTINGS_DB.update(this);
    }

    public Tag getChatPrefix2() {
        return chatPrefix2;
    }

    public void setChatPrefix2(Tag chatPrefix2) {
        this.chatPrefix2 = chatPrefix2;
        ACCOUNT_SETTINGS_DB.update(this);
    }

    public Tag getChatSuffix1() {
        return chatSuffix1;
    }

    public void setChatSuffix1(Tag chatSuffix1) {
        this.chatSuffix1 = chatSuffix1;
        ACCOUNT_SETTINGS_DB.update(this);
    }

    public Tag getChatSuffix2() {
        return chatSuffix2;
    }

    public void setChatSuffix2(Tag chatSuffix2) {
        this.chatSuffix2 = chatSuffix2;
        ACCOUNT_SETTINGS_DB.update(this);
    }

    public boolean isAppearOffline() {
        return appearOffline;
    }

    public void makeAppearOffline() {
        this.appearOffline = true;
        ACCOUNT_SETTINGS_DB.update(this);
    }

    public void removeAppearOffline() {
        this.appearOffline = false;
        ACCOUNT_SETTINGS_DB.update(this);
    }

    public void availableTags(Object sender, Object playerIdentifier){
        PlayerSession playerSessionSession = null;
//        = StarNub.getStarboundServer().getConnectionss().getOnlinePlayerByAnyIdentifier(playerIdentifier);
        if (playerSessionSession == null) {
//            StarNub.getMessageSender().playerMessage("StarNub", sender, "We could not find the player or they are not online when looking up available Tags.");
            return;
        }
        if (playerSessionSession.getPlayerCharacter().getAccount() == null) {
//            StarNub.getMessageSender().playerMessage("StarNub", sender, "You must have an account to see available Tags.");
            return;
        }
        String availableTags = "Available Tags: ";
        for (GroupAssignment groupAssignment : playerSessionSession.getPlayerCharacter().getAccount().getGroups()) {
            Group group = groupAssignment.getGroup();
            String groupTag = group.getTag().getName();
            if (sender instanceof PlayerSession) {
                groupTag = group.getTag().getColor()+groupTag;
                availableTags = availableTags + buildTagFinal(groupTag) + ", ";
            } else {
                availableTags = availableTags + buildTagNoColor(groupTag) + ", ";
            }
        }
        try {
            availableTags = availableTags.substring(0, availableTags.lastIndexOf(",")) + ".";
        } catch (StringIndexOutOfBoundsException e) {
            /* Do nothing no players are online */
        }
        if (sender instanceof PlayerSession) {
//            StarNub.getMessageSender().playerMessage("StarNub", sender, availableTags);
        } else {
            StarNub.getLogger().cInfoPrint("StarNub", availableTags);
        }
    }

    public void setPreffixOrSuffix(Object sender, Object playerIdentifier, boolean prefix, int position, String prefixOrSuffix){
        PlayerSession playerSessionSession =null;
//        = StarNub.getStarboundServer().getConnectionss().getOnlinePlayerByAnyIdentifier(playerIdentifier);
        if (playerSessionSession == null) {
//            StarNub.getMessageSender().playerMessage("StarNub", sender, "We could not find the player or they are not online when trying to set Tags.");
            return;
        }
        if (playerSessionSession.getPlayerCharacter().getAccount() == null) {
//            StarNub.getMessageSender().playerMessage("StarNub", sender, "You must have an account to see available Tags.");
            return;
        }
        if (prefixOrSuffix.contains("]")) {
            prefixOrSuffix =  prefixOrSuffix.replace("]", "");
        }
        if (prefixOrSuffix.contains("[")) {
            prefixOrSuffix =  prefixOrSuffix.replace("[", "");
        }
        for (GroupAssignment groupAssignment : playerSessionSession.getPlayerCharacter().getAccount().getGroups()) {
            Group group = groupAssignment.getGroup();
            String groupTag = group.getTag().getName();
            if (groupTag.equalsIgnoreCase(prefixOrSuffix)) {
                Tag tagSelected = group.getTag();
                if (isUsingTag(groupTag)) {
//                    StarNub.getMessageSender().playerMessage("StarNub", sender, "You already have this Tag set in one of your Tag slots.");
                }
                if (prefix) {
                    if (position == 1) {
                        this.chatPrefix1 = tagSelected;
//                        StarNub.getMessageSender().playerMessage("StarNub", sender, "Your 1st Prefix is now set to \""+buildTagFinal(groupTag)+"\".");
                        return;
                    } else if (position == 2 ) {
                        this.chatPrefix2 = tagSelected;
//                        StarNub.getMessageSender().playerMessage("StarNub", sender, "Your 2nd Prefix is now set to \""+buildTagFinal(groupTag)+"\".");
                        return;
                    }
                } else {
                    if (position == 1) {
                        this.chatSuffix1 = tagSelected;
//                        StarNub.getMessageSender().playerMessage("StarNub", sender, "Your 1st Suffix is now set to \""+buildTagFinal(groupTag)+"\".");
                        return;
                    } else if (position == 2 ) {
                        this.chatSuffix2 = tagSelected;
//                        StarNub.getMessageSender().playerMessage("StarNub", sender, "Your 2nd Suffix is now set to \""+buildTagFinal(groupTag)+"\".");
                        return;
                    }
                }
            }
        }
//        StarNub.getMessageSender().playerMessage("StarNub", sender, "It appears we could not find the Tag you were looking for, check your spelling or available Tags.");
    }

    public boolean isUsingTag(String prefixOrSuffix){
        return chatPrefix1.getName().equalsIgnoreCase(prefixOrSuffix) || chatPrefix2.getName().equalsIgnoreCase(prefixOrSuffix)
                || chatSuffix1.getName().equalsIgnoreCase(prefixOrSuffix) || chatSuffix2.getName().equalsIgnoreCase(prefixOrSuffix);
    }

    public Group getGroupFromTag(Tag tag){
        return Groups.getInstance().getGroupByTag(tag);
    }



    public void setSpecificTagSpecificSlot(){


    }


}
