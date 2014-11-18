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
* this CodeHome Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package server.server;

import org.apache.commons.lang3.exception.ExceptionUtils;
import server.StarNub;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Map;

/**
* This class will simply pass UDP traffic to starbounddata.packets.starbounddata.packets.server. This is
* is solely meant to forward Query Traffic.
* <p>
* Credit goes to Netty.io (Asynchronous API) examples.
* <p>
*
* @author Daniel (Underbalanced) (www.StarNub.org)
* @since 1.0
*
*/
final class UDPProxyServer implements Runnable {

    private final int snServerPort = (int) ((Map) StarNub.getConfiguration().getConfiguration().get("starnub settings")).get("starnub_port");
    private final String sbRemoteHost = "127.0.0.1";
    private final int sbRemotePort = (int) ((Map)StarNub.getConfiguration().getConfiguration().get("starnub settings")).get("starbound_port");
    private DatagramSocket ds;

    /**
     * This runnable will simply wait for incoming UDP starbounddata.packets
     * and forward them to the Starbound Server.
     */
    public void run() {
        try {
            ds = new DatagramSocket(snServerPort);
        } catch (SocketException e1) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e1));
            return;
        }

		/* Buffers for receive request and return the real starbounddata.packets.starbounddata.packets.server answer. */
		/* If we receive more then 1024 bytes from the client they will be discard. */
        byte[] request = new byte[1024];
        byte[] reply = new byte[4096];

        /* Loop until starbounddata.packets.starbounddata.packets.server is shutting down */
        while (!StarNub.getTask().isShuttingDown()) {
            try {

				/* (1) receive data from client */
                DatagramPacket from_client = new DatagramPacket(request, request.length);
                ds.receive(from_client);
				/* Need to create another buffer with the size
				 * of bytes that we really received from client */
                byte[] real_request = new byte[from_client.getLength()];
                for (int i = 0; i < from_client.getLength(); i++) real_request[i] = request[i];

				/* (2) sending the received data to the starbounddata.packets.starbounddata.packets.server */
                InetAddress IPAddress = InetAddress.getByName(sbRemoteHost);
                DatagramPacket sendPacket =
                        new DatagramPacket(real_request, real_request.length, IPAddress, sbRemotePort);
                ds.send(sendPacket);

				/* (3) reading the starbounddata.packets.starbounddata.packets.server answer */
                DatagramPacket from_server = new DatagramPacket(reply, reply.length);
                ds.receive(from_server);
                byte[] real_reply = new byte[from_server.getLength()];
                for (int i = 0; i < from_server.getLength(); i++) real_reply[i] = reply[i];

				/* (4) returning that answer to the client */
                InetAddress address = from_client.getAddress();
                int port = from_client.getPort();
                DatagramPacket to_client =
                        new DatagramPacket(real_reply, real_reply.length, address, port);
                ds.send(to_client);
            } catch (Exception e) {
                StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
                return;
            }
        }
    }
}