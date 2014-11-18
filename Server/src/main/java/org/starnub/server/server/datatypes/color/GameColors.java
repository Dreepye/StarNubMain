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

package org.starnub.server.server.datatypes.color;

import lombok.Getter;
import org.codehome.utilities.files.YamlLoader;
import org.starnub.server.StarNub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents StarNubs GameColor functions that
 * provide some ease of use for colors for API users.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public final class GameColors {

    @Getter
    private ConcurrentHashMap<String, String> colorMap;
    @Getter
    private volatile String defaultNameColor;
    @Getter
    private volatile String defaultChatColor;
    @Getter
    private volatile String defaultServerNameColor;
    @Getter
    private volatile String defaultServerChatColor;
    @Getter
    private volatile String bracketColor;

    //TODO shadow

    /**
     * Default constructor
     */
    public GameColors(){
    }

    /**
     * This represents the way we set the color HashMap up
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used to set up plain word colors to hex values.
     * <p>
     */
    public void setColors () {
        colorMap = new ConcurrentHashMap<String, String>();
        YamlLoader yamlLoader = new YamlLoader();
        Map<String, Object> colorMap = yamlLoader.resourceYamlLoader("starbound/hex_colors.yml");
        for (String color : colorMap.keySet()) {
            this.colorMap.put(color, (String) colorMap.get(color));
        }
        reloadDefaultColor();
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will refresh the default colors in this class
     * <p>
     */
    public void reloadDefaultColor () {
        defaultNameColor = validateColor((String) ((Map)StarNub.getConfiguration().getConfiguration().get("server chat")).get("global_name_color"));
        defaultChatColor = validateColor((String) ((Map)StarNub.getConfiguration().getConfiguration().get("server chat")).get("global_message_name_color"));
        defaultServerNameColor = validateColor((String) ((Map)StarNub.getConfiguration().getConfiguration().get("server chat")).get("server_name_color"));
        defaultServerChatColor = validateColor((String) ((Map)StarNub.getConfiguration().getConfiguration().get("server chat")).get("server_message_color"));
        bracketColor = validateColor((String) ((Map)StarNub.getConfiguration().getConfiguration().get("groups")).get("bracket_color"));
    }

    /**
     * This method should be used when using colors
     * in StarNub, it will verify the color is correctly
     * formatted or look the plain text color up and get
     * the hex format
     *
     * @param color String color to be checked "red", "FF0000" , "^#FF0000;"
     * @return
     */

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will validate any colors to make sure its properly formatted. If something goes wrong
     * and the color cannot be validated, the default color will be returned.
     * <p>
     * @param color String color to be checked "red", "FF0000" , "^#FF0000;"
     * @return String with the proper color format
     */
    public String validateColor(String color) {
        String validColor;
        if (color.contains("^") || color.contains("#") || color.contains(";")) {
            validColor = color.toLowerCase();
            if (!validColor.contains("^")){
                validColor = "^"+validColor;
            }
            if (!validColor.contains("#")){
                validColor = validColor.replace("^", "^#");
            }
            if (!validColor.contains(";")){
                validColor = validColor+";";
            }
        } else {
            return getHexValue(color.toLowerCase());
        }
        return validColor;
    }

    /**
     * Will return the verb color in hex form.
     * If it does not exist, the default color from
     * the StarNub users config will be selected.
     *
     * @param color String color that is supported for conversion
     * @return String containing the hex value for the color
     */
    private String getHexValue (String color) {
        if (!color.contains("#")) {
            if (colorMap.containsKey(color)){
                return "^"+colorMap.get(color)+";";
            } else {
                StarNub.getLogger().cErrPrint("StarNub", "Something tried to use a color that is not mapped to a hex String.");
                return defaultChatColor;
            }
        } else {
            StarNub.getLogger().cErrPrint("StarNub", "Something is attempting to use GameColor.getHexValue for a already converted color.");
        }
        return defaultChatColor;
    }
}
