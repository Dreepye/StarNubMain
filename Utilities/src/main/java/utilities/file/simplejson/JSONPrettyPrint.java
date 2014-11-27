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

package utilities.file.simplejson;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class will give an indented output, the idea is to have a pretty print that looks like:
 * <p>
 * {
 * "k3":["lv1","lv2"],
 * "k1":"v1",
 * "k2":
 * {
 * "mk1":"mv1",
 * "mk2":["lv1","lv2"]
 * }
 * }
 * <p>
 * And at the same time respect the format of the application (SimpleJSON) and its performance.
 *
 */
public class JSONPrettyPrint extends HashMap<Object, Object> implements Map<Object, Object>, JSONAware, JSONStreamAware {
    /**
     * Automatically generated serial version UID
     */
    private static final long serialVersionUID = -9168577804652055206L;
    private static int curly_brackets = 0;

    /**
     * Encode a map into JSON text and write pretty print it.
     * If this map is also a JSONAware or JSONStreamAware, JSONAware or JSONStreamAware specific behaviors will be ignored at this top level.
     *
     * @param map
     * @param out
     * @see utilities.file.simplejson.JSONValue#writePrettyJSONString(Object, java.io.Writer)
     */
    /*
	 * The logic behind the indent is to count the brackets {} to be able to break line and to tab correctly
	 */
    public static void writeJSONString(Map<?, ?> map, Writer out) throws IOException {
        if (map == null) {
            out.write("null");
            return;
        }

        boolean first = true;
        String newLine = System.getProperty("line.separator");

        Iterator<?> iter = map.entrySet().iterator();

        if (curly_brackets != 0) {
            out.write(newLine);

            for (int i = 0; i < curly_brackets; i++)
                out.write('\t');
        }

        out.write('{');
        curly_brackets++;

        out.write(newLine);

        while (iter.hasNext()) {
            if (first)
                first = false;
            else {
                out.write(',');
                out.write(newLine);
            }

            for (int i = 0; i < curly_brackets; i++)
                out.write('\t');

            @SuppressWarnings("rawtypes")
            Entry entry = (Entry) iter.next();
            out.write('\"');
            out.write(escape(String.valueOf(entry.getKey())));
            out.write('\"');
            out.write(':');

            JSONValue.writePrettyJSONString(entry.getValue(), out);
        }

        out.write(newLine);
        curly_brackets--;

        for (int i = 0; i < curly_brackets; i++)
            out.write('\t');

        out.write('}');
    }

    /**
     * Convert a map to formatted JSON text. The result is a JSON object.
     * If this map is also a JSONAware, JSONAware specific behaviors will be omitted at this top level.
     *
     * @param map
     * @return JSON text, or "null" if map is null.
     * @see utilities.file.simplejson.JSONValue#toPrettyJSONString(Object)
     */
    public static String toJSONString(Map<?, ?> map) {
        if (map == null)
            return "null";

        StringBuffer sb = new StringBuffer();
        boolean first = true;
        Iterator<?> iter = map.entrySet().iterator();

        if (curly_brackets != 0) {
            sb.append('\n');

            for (int i = 0; i < curly_brackets; i++)
                sb.append('\t');
        }

        sb.append('{');
        curly_brackets++;

        sb.append('\n');

        while (iter.hasNext()) {
            if (first)
                first = false;
            else {
                sb.append(',');
                sb.append('\n');
            }

            for (int i = 0; i < curly_brackets; i++)
                sb.append('\t');

            @SuppressWarnings("rawtypes")
            Entry entry = (Entry) iter.next();
            toJSONString(String.valueOf(entry.getKey()), entry.getValue(), sb);
        }

        sb.append('\n');
        curly_brackets--;

        for (int i = 0; i < curly_brackets; i++)
            sb.append('\t');

        sb.append('}');
        return sb.toString();
    }

    private static String toJSONString(String key, Object value, StringBuffer sb) {
        sb.append('\"');
        if (key == null)
            sb.append("null");
        else
            JSONValue.escape(key, sb);
        sb.append('\"').append(':');

        sb.append(JSONValue.toPrettyJSONString(value));

        return sb.toString();
    }

    public static String toString(String key, Object value) {
        StringBuffer sb = new StringBuffer();
        toJSONString(key, value, sb);
        return sb.toString();
    }

    /**
     * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
     * It's the same as JSONValue.escape() only for compatibility here.
     *
     * @param s
     * @return
     * @see org.codehome.utilities.simplejson.JSONValue#escape(String)
     */
    public static String escape(String s) {
        return JSONValue.escape(s);
    }

    public void writeJSONString(Writer out) throws IOException {
        writeJSONString(this, out);
    }

    public String toJSONString() {
        return toJSONString(this);
    }

    public String toString() {
        return toJSONString();
    }
}
