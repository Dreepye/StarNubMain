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

package server.server.chat;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.codehome.utilities.files.AppendToFile;
import org.codehome.utilities.files.FileToList;
import org.codehome.utilities.files.RemoveLineFromFile;
import server.StarNub;
import server.connectedentities.player.session.Player;
import server.server.Connections;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public enum ChatFilter {
    INSTANCE;

    @Getter
    private HashSet<String> wordFilter;

    @Getter
    private HashSet<String> illegalNicks;

    public void setChatFilterData(){
        wordFilter = new HashSet<String>();
        illegalNicks = new HashSet<String>();
        loadWordFilter();
        loadIllegalNicks();
    }

    public void loadWordFilter(){
        List<String> diskFilterList = null;
        try {
            diskFilterList = new FileToList().readFileLinesString("StarNub/chat_filter.txt");
        } catch (Exception e) {
            StarNub.getLogger().cErrPrint("StarNub", "Unable to load \"StarNub/chat_filter.txt\". Please check your file. If the " +
                    "issue persist, please put a issue in at www.StarNub.org under StarNub.");
            return;
        }
        wordFilter.addAll(diskFilterList);
    }

    public void loadIllegalNicks(){
        List<String> diskIllegalNicksList = null;
        try {
            diskIllegalNicksList = new FileToList().readFileLinesString("StarNub/illegal_nick_names.txt");
        } catch (Exception e) {
            StarNub.getLogger().cErrPrint("StarNub", "Unable to load \"StarNub/illegal_nick_names.txt\". Please check your file. If the " +
                    "issue persist, please put a issue in at www.StarNub.org under StarNub.");
            return;
        }
        boolean addGroups = ((boolean) ((Map)StarNub.getConfiguration().getConfiguration().get("starbounddata.packets.starbounddata.packets.server starbounddata.packets.chat")).get("global_add_groups_illegal_nicks"));
        boolean addTags = ((boolean) ((Map)StarNub.getConfiguration().getConfiguration().get("starbounddata.packets.starbounddata.packets.server starbounddata.packets.chat")).get("global_add_tags_illegal_nicks"));
        for (String groupName : StarNub.getServer().getConnections().getGroupSync().getGroups().keySet()){
            if (addGroups) {
                illegalNicks.add(groupName);
            }
            if (addTags) {
                illegalNicks.add((String) ((Map) StarNub.getServer().getConnections().getGroupSync().getGroups().get(groupName)).get("group_tag"));
            }
        }
        illegalNicks.addAll(diskIllegalNicksList);
    } //Add remove group names as groups are added and removed TODO
    //TODO has spaces

    public void reloadWordFilter(){
        wordFilter.clear();
        loadWordFilter();
    }

    public void reloadIllegalNicks(){
        illegalNicks.clear();
        loadIllegalNicks();
    }


    public boolean addToWordFilter(String identifier){
        //PLACE_HOLDER - Event
        new AppendToFile().appendStringToFile("StarNub/chat_filter.txt", identifier);
        reloadWordFilter();
        return wordFilter.contains(identifier);
    }


    public boolean removeFromWordFilter(String identifier){
        //PLACE_HOLDER - Event
        new RemoveLineFromFile().removeLineFromFile("StarNub/chat_filter.txt", identifier);
        reloadWordFilter();
        return !wordFilter.contains(identifier);
    }

    public boolean addToIllegalNicks(String identifier){
        //PLACE_HOLDER - Event
        new AppendToFile().appendStringToFile("StarNub/illegal_nick_names.txt", identifier);
        reloadIllegalNicks();
        return illegalNicks.contains(identifier);
    }


    public boolean removeFromIllegalnicks(String identifier){
        //PLACE_HOLDER - Event
        new RemoveLineFromFile().removeLineFromFile("StarNub/illegal_nick_names.txt", identifier);
        reloadIllegalNicks();
        return !illegalNicks.contains(identifier);
    }


    public String cleanNameAccordingToPermissionsAnyIdentifier(Object playerIdentifier, String s){
        Player player = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(playerIdentifier);
        if (player == null) {
            return null;
        }
        return cleanNameAccordingToPermissions(player, s);
    }

    public String cleanNameAccordingToPermissions(Player playerSession, String s){
        Connections con = StarNub.getServer().getConnections();
        return stringCleaner(
                playerSession,
                s,
                con.hasPermission(playerSession, "starnubinternals.name.color", true),
                con.hasPermission(playerSession, "starnubinternals.name.specialcharacters", true),
                con.hasPermission(playerSession, "starnubinternals.name.spaces++", true),
                con.hasPermission(playerSession, "starnubinternals.name.singlespaces", true),
                con.hasPermission(playerSession, "starnubinternals.name.repeatcharacters", true),
                con.hasPermission(playerSession, "starnubinternals.name.illegalnicks", true),
                con.hasPermission(playerSession, "starnubinternals.name.wordfilter", true),
                con.getPermissionVariableInteger(playerSession, "starnubinternals.namelength")
        );
    }


    public String stringCleaner(Player player, String s, boolean rColor, boolean rSpecialChar, boolean rMore1Space, boolean rSingleSpaces, boolean rRepeatChar, boolean rIllegalNick, boolean rWorldFilter, int nameLength) {
        if (!rColor) {
            s = removeColors(s);
        }
        if (!rSpecialChar){
            s = removeSpecialCharacters(s);
        }
        if (!rMore1Space){
            s = removeDoubleLeadingTrailingSpaces(s);
        }
        if (!rSingleSpaces){
            s = removeSpaces(s);
        }
        if (!rRepeatChar){
            s = removeMultipleCharacter(s);
        }
        if (!rIllegalNick){
            if (illegalNicks.size() > 0) {
                for (String wordToSearch : illegalNicks) {
                    if (wordToSearch != null) {
                        s = nickFilter(player, s, wordToSearch);
                    }
                }
            }
        }
        if (!rWorldFilter) {
            if (wordFilter.size() > 0) {
                for (String wordToSearch : wordFilter) {
                    if (wordToSearch != null) {
                        s = nickFilter(player, s, wordToSearch);
                    }
                }
            }
        }
        if (!(nameLength <= -100000)) {
            if (hasColors(s)) {
                String newName = "";
                int nameLen = 0;
                for (int i = 0; i < s.length() && nameLen < nameLength; i++) {
                    char letter = s.charAt(i);
                    if (letter == '^') {
                        while (letter != ';') {
                            newName = newName + Character.toString(letter);
                            i++;
                            letter = s.charAt(i);
                        }
                        newName = newName + Character.toString(letter);
                    } else {
                        newName = newName + Character.toString(letter);
                        nameLen++;
                    }
                }
                s = newName;
            } else {
                s = s.substring(0, nameLength);
            }
        }
        if (s.length() == 0) {
            s = "IllegalName";
        }
        return s;
    }

    private String nickFilter(Player player, String s, String wordToSearch){
        return wordSearchReplacement(player, s, wordToSearch, wordLengthPercentage(wordToSearch), false, "");
    }

    public String chatFilter(Player player, String s){
        if (wordFilter.size() > 0) {
            for (String wordToSearch : wordFilter) {
                if (wordToSearch != null) {
                    s = wordSearchReplacement(player, s, wordToSearch, wordLengthPercentage(wordToSearch), true, "*");
                }
            }
        }
        return s;
    }

    public String cleanNameComplete(String s) {
        return removeSpaces(removeSpecialCharacters(removeColors(s)));
    }

    public boolean hasColors(String s) {
        return patternMatcher(Pattern.compile("\\^.+?;"), s);
    }

    public String removeColors(String s) {
        return s.replaceAll("\\^.+?;", "");
    }

    public boolean hasSpecialCharacters(String s) {
        return patternMatcher(Pattern.compile("[^a-zA-Z0-9\\s+]"), s);
    }

    public String removeSpecialCharacters(String s){
        if (hasColors(s)) {
            String newName = "";
            for (int i = 0; i < s.length(); i++) {
                char letter = s.charAt(i);
                    if (letter == '^') {
                        while (letter != ';') {
                            newName = newName + Character.toString(letter);
                            i++;
                            letter = s.charAt(i);
                        }
                        newName = newName + Character.toString(letter);
                    } else {
                        if (!hasSpecialCharacters(Character.toString(letter)))
                            newName = newName + Character.toString(letter);
                    }
                }
            return newName;
        } else {
            return s.replaceAll("[^a-zA-Z0-9\\s+]", "");
        }
    }

    public boolean hasDoubleSpaced(String s) {
        return patternMatcher(Pattern.compile(" +"), s);
    }

    public String removeDoubleLeadingTrailingSpaces(String s) {
        return StringUtils.normalizeSpace(s);
    }

    public String removeSpaces(String s){
        return s.replace(" ","");
    }

    public boolean hasMultipleCharacter(String s) {
        return patternMatcher(Pattern.compile("(?i)(.{2})(\\1)+"), s);
    }

    public String removeMultipleCharacter(String s) {
        return s.replaceAll("(?i)(.{2})(\\1)+", "$1$1");
    }

    public String wordSearchReplacement(Player player, String s, String wordToSearch, double percentToMatch, boolean replaceWholeWord, String replacementChar){
        double highEstMatch = 0;
        boolean firstTime = true;
        while (highEstMatch > percentToMatch || firstTime) {
            firstTime = false;
            int start = 0;
            int end = wordToSearch.length();
            int s2Len = s.length();
            int highStart = 0;
            int highEnd = 0;
            highEstMatch = 0;
            if (end <= s2Len) {
                boolean replace = false;
                while (end <= s2Len) {
                    int stringsToChange = StringUtils.getLevenshteinDistance(wordToSearch.toLowerCase(), s.substring(start, end).toLowerCase());
                    double percentMatched = (100 - ((stringsToChange * 100) / end));
                    if (percentMatched >= percentToMatch && highEstMatch < percentMatched) {
                        highEstMatch = percentMatched / end;
                        highStart = start;
                        highEnd = end;
                        replace = true;
                    }
                    start++;
                    end++;
                }
                String rS = "";
                if (replaceWholeWord) {
                    rS = StringUtils.repeat(replacementChar, s.substring(highStart, highEnd).length());
                }
                //DEBUG Print Replace with event
                if (replace) {
                    StarNub.getLogger().cWarnPrint("StarNub", "A word was filtered: Word Used: " + s.substring(highStart, highEnd) + ". Word Filtered On: " + wordToSearch + ". Player Name: " + StarNub.getMessageSender().cPlayerNameBuilderFinal(player, true, true));
                    s = s.replace(s.substring(highStart, highEnd), rS);
                }
            }
        }
        return s;
    }

    private double wordLengthPercentage(String s){
       if (s.length() <= 4) {
           return 100.00;
       } else if (s.length() == 5) {
           return 100.00;
       } else if (s.length() >= 6) {
           return 100.00;
       }
       return 100.00;
    }

    private boolean patternMatcher(Pattern pattern, String s){
        return pattern.matcher(s).find();
    }

    public double stringCapitalizationCheck(String message){
        int count = 0;
        for (int idx = 0; idx < message.length(); idx++) {
            if (Character.isUpperCase(message.charAt(idx))) {
                count++;
            }
        }
        return percentageCalculation(count, message.length());
    }

    public double percentageCalculation(double resultNumber, double baseNumber){
        return (resultNumber*100)/baseNumber;
    }

    public double percentageAlikeCalculation (String s, String s2) throws ArithmeticException {
        return 100-((StringUtils.getLevenshteinDistance(s, s2)*100)) / ((s.length()+s2.length())/2);
    }


}
