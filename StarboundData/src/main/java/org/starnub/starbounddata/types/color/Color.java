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

package org.starnub.starbounddata.types.color;

/**
 * Represents a Starbound Color
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Color {

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

