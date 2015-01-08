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

package org.starnub.utilities.strings;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Represents a static StringUtilities these will provide methods to manipulate strings
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class StringUtilities {

    /**
     * This will remove colors, special characters and spaces
     *
     * @param s String s the string to be cleaned
     * @return String the cleaned string
     */
    public static String completeClean(String s){
        return removeSpaces(removeSpecialCharacters(removeColors(s)));
    }

    /**
     * This will check for ^ and ; with characters between
     *
     * @param s String the string to be checked
     * @return boolean returns true if these characters and characters between have been detected
     */
    public static boolean hasColors(String s) {
        return patternMatcher(Pattern.compile("\\^.+?;"), s);
    }

    /**
     * This will check for ^ and ; with characters between characters and remove those characters and anything between
     *
     * @param s String the string be cleaned
     * @return String the cleaned string
     */
    public static String removeColors(String s) {
        return s.replaceAll("\\^.+?;", "");
    }

    /**
     * This will check for special characters
     *
     * @param s String the string to be checked
     * @return boolean returns true if special characters exist
     */
    public static boolean hasSpecialCharacters(String s) {
        return patternMatcher(Pattern.compile("[^a-zA-Z0-9\\s+]"), s);
    }

    /**
     * This will remove special characters but not color tags
     *
     * @param s String the string to be cleaned
     * @return String the cleaned string
     */
    public static String removeSpecialCharacters(String s){
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

    /**
     * This will check for double spaces
     *
     * @param s String the string to be checked
     * @return boolean returns true if double spaces exist
     */
    public static boolean hasDoubleSpaced(String s) {
        return patternMatcher(Pattern.compile(" +"), s);
    }

    /**
     * This will remove leading, trailing and double spaces
     *
     * @param s String the string to be cleaned
     * @return String the cleaned string
     */
    public static String removeDoubleLeadingTrailingSpaces(String s) {
        return StringUtils.normalizeSpace(s);
    }

    /**
     * This will remove single spaces
     *
     * @param s String the string to be cleaned
     * @return String the cleaned string
     */
    public static String removeSpaces(String s){
        return s.replace(" ","");
    }

    /**
     * This will check for repeating characters
     *
     * @param s String the string to be checked
     * @return boolean returns true if repeating characters exist
     */
    public static boolean hasRepeatingCharacters(String s) {
        return patternMatcher(Pattern.compile("(?i)(.{2})(\\1)+"), s);
    }

    /**
     * This will remove repeating characters and replace them with 1 or 2 characters
     *
     * @param s String the string to be cleaned
     * @return String the cleaned string
     */
    public static String removeRepeatingCharacters(String s) {
        return s.replaceAll("(?i)(.{2})(\\1)+", "$1$1");
    }

    public static String exactWordReplacement(String stringToSearch, String wordToSearch, String replacement){
        int indexOfWord;
        boolean replaced = false;
        while ((indexOfWord = stringToSearch.toLowerCase().indexOf(wordToSearch.toLowerCase())) > 0) {
            String replacementWord = "";
            int wLen = wordToSearch.length();
            if(!replaced) {
                if (!replacement.isEmpty()) {
                    if (replacement.length() == 1) {
                        for (int i = 0; i < wLen; i++) {
                            replacementWord = replacementWord + replacement;
                        }
                    } else {
                        replacementWord = replacement;
                    }
                    replaced = true;
                }
            }
            stringToSearch = stringToSearch.replaceAll(stringToSearch.substring(indexOfWord, indexOfWord + wLen), replacementWord);
        }
        return stringToSearch;
    }


    /**
     *
     * This will search for words and replace them if they match within a certain percentage
     *
     * @param s String to be searched
     * @param wordToSearch String the word to be matched
     * @param percentToMatch double the percent to match
     * @param replaceWholeWord boolean replace the word with characters (true) or not (false)
     * @param replacementChar String the character to replace the word with
     * @return String the cleaned string
     */
    public static String wordSearchReplacement(String s, String wordToSearch, double percentToMatch, boolean replaceWholeWord, String replacementChar){
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
            }
        }
        return s;
    }


    private static double wordLengthPercentage(String s){
        if (s.length() <= 4) {
            return 100.00;
        } else if (s.length() == 5) {
            return 100.00;
        } else if (s.length() >= 6) {
            return 100.00;
        }
        return 100.00;
    }

    /**
     * This will match a pattern
     *
     * @param pattern Pattern the pattern to be matched
     * @param s String the string to search
     * @return boolean if a pattern match was found
     */
    private static boolean patternMatcher(Pattern pattern, String s){
        return pattern.matcher(s).find();
    }

    /**
     * This will check the percentage of capital letters
     *
     * @param message String message to check
     * @return double percentage of capital letters
     */
    public static String stringCapitalizationCheckAndChange(String message, double percentage){
        int count = 0;
        for (int idx = 0; idx < message.length(); idx++) {
            if (Character.isUpperCase(message.charAt(idx))) {
                count++;
            }
        }
        double calculationAlike = percentageCalculation(count, message.length());
        if (calculationAlike > percentage){
            message = message.toLowerCase();
        }
        return message;
    }

    /**
     * This will check the percentage of capital letters
     *
     * @param message String message to check
     * @return double percentage of capital letters
     */
    public static double stringCapitalizationCheck(String message){
        int count = 0;
        for (int idx = 0; idx < message.length(); idx++) {
            if (Character.isUpperCase(message.charAt(idx))) {
                count++;
            }
        }
        return percentageCalculation(count, message.length());
    }

    /**
     * Will perform a percentage calculation
     *
     * @param resultNumber double result
     * @param baseNumber doublt base number
     * @return double percentage
     */
    public static double percentageCalculation(double resultNumber, double baseNumber){
        return (resultNumber*100)/baseNumber;
    }

    /**
     * This will compare the percentage similarity of two words
     *
     * @param s String string to be compared against
     * @param s2 String string to compare
     * @return double the percentage of similarity
     * @throws ArithmeticException if issue calculating
     */
    public static double similarityCalculation(String s, String s2) throws ArithmeticException {
        return 100-((StringUtils.getLevenshteinDistance(s, s2)*100)) / ((s.length()+s2.length())/2);
    }

    /**
     * This will trim a character from the end of a string and add a character of your choice
     *
     * @param s String representing the string to trim
     * @return String the cleaned string
     */
    public static String trimCommaForPeriod(String s){
        return trimFromString(s, ",", ".");
    }

    /**
     * This will trim a character from the end of a string and add a character of your choice
     *
     * @param s String representing the string to trim
     * @return String the cleaned string
     */
    public static String trimFromString(String s, String trim, String replace) {
        try {
            return s.substring(0, s.lastIndexOf(trim)) + replace;
        } catch (StringIndexOutOfBoundsException e) {
            /* Do nothing no players are online */
        }
        return s;
    }
}
