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

/**
 * Represents StarNubs Query Class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarboundQuery {

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will query the address and port with a specific socket time out.
     * It will return the Starbound version number
     *
     * @param ipAddress String representing the address to TCP Query
     * @param port int representing the port to query
     * @param timeout int representing the timeout in seconds
     * @return int representing the starbound version number
     * @throws IOException various exceptions can be thrown (SocketTimeOut, ConnectionRefused, Ect...)
     */
    public static int query(String ipAddress, int port, int timeout) throws IOException {
        Socket socket = new Socket(ipAddress, port);
        socket.setSoTimeout(timeout);
        byte[] bytes;
        try (InputStream in = socket.getInputStream()) {
            boolean reading = true;
            bytes = new byte[6];
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




