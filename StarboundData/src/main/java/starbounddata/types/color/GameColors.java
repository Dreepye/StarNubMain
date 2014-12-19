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

package starbounddata.types.color;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the default colors set by the Server Owners configuration, contained with are
 * the {@link starbounddata.types.color.GameColors.Colors} enumeration and methods as well as
 * the {@link starbounddata.types.color.GameColors.Colors.Color} class used to create colors representations
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public final class GameColors {

    /**
     * Singleton pattern for instantiation of only one instance of this class
     */
    private static final GameColors instance = new GameColors();

    private volatile String defaultNameColor;
    private volatile String defaultChatColor;
    private volatile String defaultServerNameColor;
    private volatile String defaultServerChatColor;
    private final String STARBOUND_CHAT_DEFAULT = Colors.validate("#FFFF00", true, true);

    /**
     * Must be private to ensure integrity of the singleton pattern
     */
    private GameColors() {
    }

    /**
     * @return GameColors the only instance of GameColors because of the Singleton Pattern used
     */
    public static GameColors getInstance() {
        return instance;
    }

    public String getDefaultNameColor() {
        return defaultNameColor != null ? defaultNameColor : STARBOUND_CHAT_DEFAULT;
    }

    public String getDefaultChatColor() {
        return defaultChatColor != null ? defaultChatColor : STARBOUND_CHAT_DEFAULT;
    }

    public String getDefaultServerNameColor() {
        return defaultServerNameColor != null ? defaultServerNameColor : STARBOUND_CHAT_DEFAULT;
    }

    public String getDefaultServerChatColor() {
        return defaultServerChatColor != null ? defaultServerChatColor : STARBOUND_CHAT_DEFAULT;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will refresh the default colors in this class
     * <p>
     */
    public void setColors(String defaultNameColor, String defaultChatColor, String defaultServerNameColor, String defaultServerChatColor, String bracketColor) {
        this.defaultNameColor = Colors.validate(defaultNameColor, true, true);
        this.defaultChatColor = Colors.validate(defaultChatColor, true, true);
        this.defaultServerNameColor = Colors.validate(defaultServerNameColor, true, true);
        this.defaultServerChatColor = Colors.validate(defaultServerChatColor, true, true);
    }

    @Override
    public String toString() {
        return "GameColors{" +
                "defaultNameColor='" + defaultNameColor + '\'' +
                ", defaultChatColor='" + defaultChatColor + '\'' +
                ", defaultServerNameColor='" + defaultServerNameColor + '\'' +
                ", defaultServerChatColor='" + defaultServerChatColor + '\'' +
                ", STARBOUND_CHAT_DEFAULT='" + STARBOUND_CHAT_DEFAULT + '\'' +
                '}';
    }

    ///////////////////     REPRESENTS THE COLORS ENUM     ///////////////////

    /**
     * Represents Starbound colors represented in Verb Name, Hex, and StarNub shortcut annotation, this enumeration also contains
     * the {@link starbounddata.types.color.GameColors.Colors.Color} class
     * <p>
     *
     * @author Daniel (Underbalanced) (www.StarNub.org)
     * @since 1.0 Beta
     */
    public enum Colors {
        ALICEBLUE("F0F8FF", "AB"),
        ANTIQUEWHITE("FAEBD7", "AW"),
        AQUA("00FFFF", "A"),
        AQUAMARINE("7FFFD4", "AM"),
        AZURE("F0FFFF", "AZ"),
        BEIGE("F5F5DC", "BE"),
        BISQUE("FFE4C4", "BI"),
        BLACK("000000", "BL"),
        BLANCHEDALMOND("FFEBCD", "BA"),
        BLUE("0000FF", "B"),
        BLUEVIOLET("8A2BE2", "BV"),
        BROWN("A52A2A", "BR"),
        BURLYWOOD("DEB887", "BW"),
        CADETBLUE("5F9EA0", "CB"),
        CHARTREUSE("7FFF00", "CR"),
        CHOCOLATE("D2691E", "CH"),
        CORAL("FF7F50", "CO"),
        CORNFLOWERBLUE("6495ED", "CFB"),
        CORNSILK("FFF8DC", "CS"),
        CRIMSON("DC143C", "C"),
        CYAN("00FFFF", "CY"),
        DARKBLUE("00008B", "DB"),
        DARKCYAN("008B8B", "DCY"),
        DARKGOLDENROD("B8860B", "DGOR"),
        DARKGRAY("A9A9A9", "DGR"),
        DARKGREEN("006400", "DG"),
        DARKKHAKI("BDB76B", "DK"),
        DARKMAGENTA("8B008B", "DM"),
        DARKOLIVEGREEN("556B2F", "DOG"),
        DARKORANGE("FF8C00", "DO"),
        DARKORCHID("9932CC", "DOR"),
        DARKRED("8B0000", "DR"),
        DARKSALMON("E9967A", "DS"),
        DARKSEAGREEN("8FBC8F", "DSG"),
        DARKSLATEBLUE("483D8B", "DSLB"),
        DARKSLATEGRAY("2F4F4F", "DSLGR"),
        DARKTURQUOISE("00CED1", "DT"),
        DARKVIOLET("9400D3", "DV"),
        DEEPPINK("FF1493", "DP"),
        DEEPSKYBLUE("00BFFF", "DSB"),
        DIMGRAY("696969", "DIGR"),
        DODGERBLUE("1E90FF", "DOB"),
        FIREBRICK("B22222", "FB"),
        FLORALWHITE("FFFAF0", "FW"),
        FORESTGREEN("228B22", "FG"),
        FUCHSIA("FF00FF", "F"),
        GAINSBORO("DCDCDC", "GB"),
        GHOSTWHITE("F8F8FF", "GW"),
        GOLD("FFD700", "GO"),
        GOLDENROD("DAA520", "GOR"),
        GRAY("808080", "GR"),
        GREEN("008000", "G"),
        GREENYELLOW("ADFF2F", "GY"),
        HONEYDEW("F0FFF0", "HD"),
        HOTPINK("FF69B4", "HP"),
        INDIANRED("CD5C5C", "IR"),
        INDIGO("4B0082", "IN"),
        IVORY("FFFFF0", "I"),
        KHAKI("F0E68C", "K"),
        LAVENDER("E6E6FA", "LA"),
        LAVENDERBLUSH("FFF0F5", "LAB"),
        LAWNGREEN("7CFC00", "LAG"),
        LEMONCHIFFON("FFFACD", "LCH"),
        LIGHTBLUE("ADD8E6", "LB"),
        LIGHTCORAL("F08080", "LC"),
        LIGHTCYAN("E0FFFF", "LCY"),
        LIGHTGOLDENRODYELLOW("FAFAD2", "LGOR"),
        LIGHTGRAY("D3D3D3", "LGR"),
        LIGHTGREEN("90EE90", "LG"),
        LIGHTPINK("FFB6C1", "LP"),
        LIGHTSALMON("FFA07A", "LS"),
        LIGHTSEAGREEN("20B2AA", "LSG"),
        LIGHTSKYBLUE("87CEFA", "LSB"),
        LIGHTSLATEGRAY("778899", "LSGR"),
        LIGHTSTEELBLUE("B0C4DE", "LSTB"),
        LIGHTYELLOW("FFFFE0", "LY"),
        LIME("00FF00", "L"),
        LIMEGREEN("32CD32", "LIG"),
        LINEN("FAF0E6", "LI"),
        MAGENTA("FF00FF", "MA"),
        MAROON("800000", "MAR"),
        MEDIUMAQUAMARINE("66CDAA", "MEA"),
        MEDIUMBLUE("0000CD", "MEB"),
        MEDIUMORCHID("BA55D3", "MEO"),
        MEDIUMPURPLE("9370DB", "MEP"),
        MEDIUMSEAGREEN("3CB371", "MESG"),
        MEDIUMSLATEBLUE("7B68EE", "MESB"),
        MEDIUMSPRINGGREEN("00FA9A", "MESPG"),
        MEDIUMTURQUOISE("48D1CC", "METU"),
        MEDIUMVIOLETRED("C71585", "MVR"),
        MIDNIGHTBLUE("191970", "MB"),
        MINTCREAM("F5FFFA", "MC"),
        MISTYROSE("FFE4E1", "MR"),
        MOCCASIN("FFE4B5", "MO"),
        NAVAJOWHITE("FFDEAD", "NW"),
        NAVY("000080", "N"),
        OLDLACE("FDF5E6", "OLL"),
        OLIVE("808000", "OL"),
        OLIVEDRAB("6B8E23", "OD"),
        ORANGE("FFA500", "O"),
        ORANGERED("FF4500", "OR"),
        ORCHID("DA70D6", "ORC"),
        PALEGOLDENROD("EEE8AA", "PGOR"),
        PALEGREEN("98FB98", "PG"),
        PALETURQUOISE("AFEEEE", "PT"),
        PALEVIOLETRED("DB7093", "PVR"),
        PAPAYAWHIP("FFEFD5", "PW"),
        PEACHPUFF("FFDAB9", "PP"),
        PERU("CD853F", "PE"),
        PINK("FFC0CB", "P"),
        PLUM("DDA0DD", "PL"),
        POWDERBLUE("B0E0E6", "PB"),
        PURPLE("800080", "PU"),
        RED("FF0000", "R"),
        ROSYBROWN("BC8F8F", "RBR"),
        ROYALBLUE("4169E1", "RB"),
        SADDLEBROWN("8B4513", "SABR"),
        SALMON("FA8072", "SA"),
        SANDYBROWN("F4A460", "SBR"),
        SEAGREEN("2E8B57", "SEG"),
        SEASHELL("FFF5EE", "SS"),
        SIENNA("A0522D", "SIL"),
        SILVER("C0C0C0", "S"),
        SKYBLUE("87CEEB", "SKB"),
        SLATEBLUE("6A5ACD", "SLB"),
        SLATEGRAY("708090", "SLGR"),
        SNOW("FFFAFA", "SN"),
        SPRINGGREEN("00FF7F", "SG"),
        STEELBLUE("4682B4", "SB"),
        TAN("D2B48C", "TA"),
        TEAL("008080", "T"),
        THISTLE("D8BFD8", "TH"),
        TOMATO("FF6347", "TO"),
        TURQUOISE("40E0D0", "TU"),
        VIOLET("EE82EE", "V"),
        WHEAT("F5DEB3", "WH"),
        WHITE("FFFFFF", "W"),
        WHITESMOKE("F5F5F5", "WS"),
        YELLOW("FFFF00", "Y"),
        YELLOWGREEN("9ACD32", "YG");

        private final String hexValue;
        private final String shortcut;
        private final static HashMap<String, String> QUICK_SHORTCUT_HEX = Colors.buildQuickShortcut();
        private final static HashMap<String, String> QUICK_HEX_COLOR = Colors.buildQuickHex();
        private final static HashMap<String, String> QUICK_COLOR_HEX = Colors.buildQuickColor();

        Colors(String hexValue, String shortcut) {
            this.hexValue = hexValue;
            this.shortcut = shortcut;
        }

        public String getHexValue() {
            return hexValue;
        }

        public String getShortcut() {
            return shortcut;
        }

        private static HashMap<String, String> buildQuickShortcut(){
            HashMap<String, String> quickShortcut = new HashMap<>();
            for (Colors c : Colors.values()){
                String cHexValue = c.getHexValue().toLowerCase();
                String cShortcut = c.getShortcut().toLowerCase();
                quickShortcut.put(cShortcut, cHexValue);
            }
            return quickShortcut;
        }

        private static HashMap<String, String> buildQuickHex(){
            HashMap<String, String> quickShortcut = new HashMap<>();
            for (Colors c : Colors.values()){
                String cHexValue = c.getHexValue().toLowerCase();
                String cColor = c.toString().toLowerCase();
                quickShortcut.put(cHexValue, cColor);
            }
            return quickShortcut;
        }

        private static HashMap<String, String> buildQuickColor(){
            HashMap<String, String> quickShortcut = new HashMap<>();
            for (Colors c : Colors.values()){
                String cColor = c.toString().toLowerCase();
                String cHexValue = c.getHexValue().toLowerCase();
                quickShortcut.put(cColor, cHexValue);
            }
            return quickShortcut;
        }

        /**
         * Recommended: For Plugin Developers & Anyone else.
         * <p>
         * Uses: This method will validate a color or hex value returning it if it exist in game
         * <p>
         *
         * @param string String representing the string to be validated
         * @param format boolean do you want to have it formatted for in game color display
         * @return String valid color string, will return null if color does not exist
         */
        public static String validate(String string, boolean format, boolean returnHex) {
            string = string.toLowerCase();
            int index = 0;
            if (string.contains("#")){
                index = string.indexOf("#") + 1;
            } else if (string.contains("^")){
                index = string.indexOf("^") + 1;
            }
            String substring = "";
            if (string.contains(";")) {
                substring = string.substring(index, string.lastIndexOf(";"));
            } else {
                substring = string.substring(index);
            }
            int stringLength = substring.length();
            if ((stringLength == 3 || stringLength == 6) && string.matches(".*\\d.*")) {
                if (QUICK_HEX_COLOR.containsKey(substring)) {
                    return format(substring, true);
                } else {
                    return GameColors.getInstance().getDefaultChatColor();
                }
            } else {
                if (QUICK_COLOR_HEX.containsKey(substring)){
                    return format(substring, false);
                } else {
                    return GameColors.getInstance().getDefaultChatColor();
                }
            }
        }

        /**
         * Recommended: For Plugin Developers & Anyone else.
         * <p>
         * Uses: This method will look up a color value based on the hex value
         * <p>
         *
         * @param hex    String representing the hex value to be looked up
         * @param format boolean do you want to have it formatted for in game color display
         * @return String representing the color value
         */
        public static String fromHex(String hex, boolean format, boolean returnHex) {
            hex = cleanString(hex);
            for (Colors c : Colors.values()) {
                if (c.hexValue.equalsIgnoreCase(hex)) {
                    if (!returnHex) {
                        return format ? format(c.toString(), false) : c.toString();
                    } else {
                        return format ? format(c.getHexValue(), true) : c.getHexValue();
                    }
                }
            }
            return GameColors.getInstance().getDefaultChatColor();
        }

        /**
         * Recommended: For Plugin Developers & Anyone else.
         * <p>
         * Uses: This method will return a hex value based on a color name
         * <p>
         *
         * @param color  String representing the color name
         * @param format boolean do you want to have it formatted for in game color display
         * @return String representing the hex value of the color
         */
        public static String fromColor(String color, boolean format, boolean returnHex) {
            color = cleanString(color);
            for (Colors c : Colors.values()) {
                if (c.toString().equalsIgnoreCase(color)) {
                    if (!returnHex) {
                        return format ? format(c.toString(), false) : c.toString();
                    } else {
                        return format ? format(c.getHexValue(), true) : c.getHexValue();
                    }
                }
            }
            return GameColors.getInstance().getDefaultChatColor();
        }

        /**
         * Recommended: For Plugin Developers & Anyone else.
         * <p>
         * Uses: This method will remove the color special characters from a string "^, # and ;"
         * <p>
         *
         * @param string String representing the string to be cleaned
         * @return String representing the cleaned up string
         */
        public static String cleanString(String string) {
            if (string.contains("^") || string.contains("#") || string.contains(";")) {
                return string.replaceAll("\\^|#|;", "");
            } else {
                return string;
            }
        }

        /**
         * Recommended: For Plugin Developers & Anyone else.
         * <p>
         * Uses: This method will format a string for in game use
         * <p>
         *
         * @param string String repenting the value to be formatted for in game
         * @param hex    boolean representing if the value is a hex value or not
         * @return String representing the string formatted for in game
         */
        public static String format(String string, boolean hex) {
            return hex ? "^#" + string + ";" : "^" + string + ";";
        }

        /**
         * Recommended: For Plugin Developers & Anyone else.
         * <p>
         * Uses: This method will replace shortcuts with the hex color tag
         * <p>
         *
         * @param string String representing the whole entire message to be scanned for color shortcuts
         * @return String repsenting the whole entire message with the shortcut colors replaced with hex colors for game display
         */
        public static String shortcutReplacement(String string) {
            Pattern p = Pattern.compile("\\{.\\}");
            Matcher m = p.matcher(string);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String shortcut = m.group().toLowerCase();
                String replacement = Colors.fromShortcut(shortcut, true, true);
                m.appendReplacement(sb, replacement);
            }
            return m.appendTail(sb).toString();
        }

        /**
         * Recommended: For Plugin Developers & Anyone else.
         * <p>
         * Uses: This method will return a color or hex based on a shortcut.
         * <p>
         *
         * @param shortcut String shortcut to be looked up
         * @param format   boolean should this be formatted for in game color display
         * @param hex      boolean do you want to have it returned as a hex value
         * @return String representing the looked up value
         */
        public static String fromShortcut(String shortcut, boolean format, boolean hex) {
            for (Colors c : Colors.values()) {
                if (c.shortcut.equalsIgnoreCase(shortcut)) {
                    if (hex) {
                        return format ? format(c.getHexValue(), true) : c.getHexValue();
                    } else {
                        return format ? format(c.toString(), false) : c.toString();
                    }
                }
            }
            return GameColors.getInstance().getDefaultChatColor();
        }

        /**
         * Recommended: For Plugin Developers & Anyone else.
         * <p>
         * Uses: This method will replace shortcuts with the hex color tag
         * <p>
         *
         * @param string String representing the whole entire message to be scanned for color shortcuts
         * @return String representing the whole entire message with the shortcut colors replaced with hex colors for game display
         */
        public static String speedyShortcutReplacement(String string) {
            Pattern p = Pattern.compile("\\{.\\}");
            Matcher m = p.matcher(string);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String shortcut = m.group().toLowerCase();
                String replacement = QUICK_SHORTCUT_HEX.get(shortcut);
                m.appendReplacement(sb, replacement);
            }
            return m.appendTail(sb).toString();
        }

        /**
         * Recommended: For Plugin Developers & Anyone else.
         * <p>
         * Uses: This method will return a color or hex based on a shortcut.
         * <p>
         *
         * @param shortcut String shortcut to be looked up
         * @return String representing the looked up value
         */
        public static String speedyFromShortcut(String shortcut) {
            for (Colors c : Colors.values()) {
                if (c.shortcut.equalsIgnoreCase(shortcut)) {
                    return "^#" + c.getHexValue() + ";";
                }
            }
            return GameColors.getInstance().getDefaultChatColor();
        }




        /**
         * Recommended: For Plugin Developers & Anyone else.
         * <p>
         * Uses: This method will create a HashSet of {@link starbounddata.types.color.GameColors.Colors.Color}'s to be returned to the calling code
         * <p>
         * Note: {@link starbounddata.types.color.GameColors.Colors.Color} represents a Starbound Color by Name, Hex and StarNub shortcut
         *
         * @return HashSet representing all of the {@link starbounddata.types.color.GameColors.Colors.Color}'s available
         */
        public static HashSet<Color> colorList() {
            HashSet<Color> colors = new HashSet<Color>();
            for (Colors c : Colors.values()) {
                colors.add(buildColor(c));
            }
            return colors;
        }

        /**
         * Recommended: For connections StarNub usage.
         * <p>
         * Uses: This method will create a new Color using the enumeration {@link starbounddata.types.color.GameColors.Colors.Color}
         * <p>
         * Note: {@link starbounddata.types.color.GameColors.Colors.Color} represents a Starbound Color by Name, Hex and StarNub shortcut
         *
         * @param c Enumeration representing the color
         * @return Color representing the build color
         */
        private static Color buildColor(Colors c) {
            return new Color(c.toString(), c.getHexValue(), c.getShortcut());
        }

        /**
         * Recommended: For Plugin Developers & Anyone else.
         * <p>
         * Uses: This method will create a HashSet of {@link starbounddata.types.color.GameColors.Colors.Color}'s to be returned to the calling code based on a provided search term.
         * The method will return anything contains the searched text, Example: findColors("Blue") or findColors("bLuE") would return (Aliceblue, blue, blueviolet,
         * cadetblue, darkslateblue, deepskyblue, dodgerblue, lightskyblue, lightsteelblue, midnightblue, powderblue, royalblue, skyblue, slateblue, steelblue). This
         * search is case insensitive.
         * <p>
         * Note: {@link starbounddata.types.color.GameColors.Colors.Color} represents a Starbound Color by Name, Hex and StarNub shortcut
         *
         * @param searchTerm String representing the color or colors to be searched
         * @return HashSet representing all of the found {@link starbounddata.types.color.GameColors.Colors.Color}'s
         */
        public static HashSet<Color> findColors(String searchTerm) {
            HashSet<Color> colors = new HashSet<Color>();
            searchTerm = cleanString(searchTerm).toLowerCase();
            for (Colors c : Colors.values()) {
                String cColor = c.toString().toLowerCase();
                if (cColor.contains(searchTerm)) {
                    colors.add(buildColor(c));
                }
            }
            return colors;
        }

        ///////////////////     REPRESENTS THE COLOR CLASS     ///////////////////

        /**
         * Represents a Starbound Color
         * <p>
         *
         * @author Daniel (Underbalanced) (www.StarNub.org)
         * @since 1.0 Beta
         */
        public static class Color {

            private final String COLOR_NAME;
            private final String HEX_STRING;
            private final String SHORTCUT;

            public Color(String COLOR_NAME, String HEX_STRING, String SHORTCUT) {
                this.COLOR_NAME = COLOR_NAME;
                this.HEX_STRING = HEX_STRING;
                this.SHORTCUT = SHORTCUT;
            }

            public String getCOLOR_NAME() {
                return COLOR_NAME;
            }

            public String getHEX_STRING() {
                return HEX_STRING;
            }

            public String getSHORTCUT() {
                return SHORTCUT;
            }

            /**
             * Recommended: For Plugin Developers & Anyone else.
             * <p>
             * Uses: This returns the color name formatted as such without the quotes "^COLOR;"
             * <p>
             *
             * @return String representing the color name in, in game display format that can be used in chat or plugins
             */
            public String formatName() {
                return "^" + COLOR_NAME + ";";
            }

            /**
             * Recommended: For Plugin Developers & Anyone else.
             * <p>
             * Uses: This returns the color hex formatted as such without the quotes "^HEX_STRING;"
             * <p>
             *
             * @return String representing the hex string of the color in, in game display format that can be used in chat or plugins
             */
            public String formatHex() {
                return "^#" + HEX_STRING + ";";
            }

            /**
             * Recommended: For Plugin Developers & Anyone else.
             * <p>
             * Uses: This returns the color shortcut formatted as such without the quotes "{SHORTCUT}"
             * <p>
             *
             * @return String representing the color shortcut that can be used in chat or plugins
             */
            public String formatShortcut() {
                return "{" + SHORTCUT + "}";
            }

            @Override
            public String toString() {
                return "Color{" +
                        "COLOR_NAME='" + COLOR_NAME + '\'' +
                        ", HEX_STRING='" + HEX_STRING + '\'' +
                        ", SHORTCUT='" + SHORTCUT + '\'' +
                        '}';
            }
        }
    }
}
