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

/**
 * Represents the default colors set by the Server Owners configuration, contained with are
 * the {@link starbounddata.types.color.GameColors} enumeration and methods as well as
 * the {@link starbounddata.types.color.GameColors} class used to create colors representations
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
    private final String STARBOUND_CHAT_DEFAULT = Colors.validate("#FFFF00");

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
        this.defaultNameColor = Colors.validate(defaultNameColor);
        this.defaultChatColor = Colors.validate(defaultChatColor);
        this.defaultServerNameColor = Colors.validate(defaultServerNameColor);
        this.defaultServerChatColor = Colors.validate(defaultServerChatColor);
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
}
