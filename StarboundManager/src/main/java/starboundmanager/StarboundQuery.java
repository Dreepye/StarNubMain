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

package starboundmanager;

import utilities.bytes.BytesInteger;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class StarboundQuery {

    public static int query(String address, int port, int timeout) throws IOException {
        Socket socket = new Socket(address, port);
        socket.setSoTimeout(timeout);
        byte[] bytes;
        try (InputStream in = socket.getInputStream()) {
            boolean reading = true;
            bytes = new byte[10];
            while (reading) {
                in.read(bytes);
                if (in.available() == 0) {
                    reading = false;
                }
            }
        } finally {
            socket.close();
        }
        return BytesInteger.getInt(bytes, 2);
    }
}




