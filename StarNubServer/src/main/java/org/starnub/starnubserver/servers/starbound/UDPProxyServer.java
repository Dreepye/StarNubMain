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
* this CodeHome Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.starnub.starnubserver.servers.starbound;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.starnub.starnubserver.StarNub;

import java.net.*;

/**
* This class will simply pass UDP traffic to the Starbound server. This is
* is solely meant to forward Query Traffic.
* <p>
*
* @author Daniel (Underbalanced) (www.StarNub.org)
* @since 1.0
*
*/
final class UDPProxyServer implements Runnable {

    private final int starnubPort;
    private final String starboundAddress;
    private final int starboundPort;
    private boolean stopping = false;

    public UDPProxyServer(int starnubPort, String starboundAddress,  int starboundPort) {
        this.starnubPort = starnubPort;
        this.starboundAddress = starboundAddress;
        this.starboundPort = starboundPort;
    }

    public boolean isStopping() {
        return stopping;
    }

    public void setStopping(boolean stopping) {
        this.stopping = stopping;
    }

    public void run() {
        InetAddress IPAddress;
        DatagramSocket ds;
        try {
            ds = new DatagramSocket(starnubPort);
            IPAddress = InetAddress.getByName(starboundAddress);
        } catch (SocketException | UnknownHostException e1) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e1));
            e1.printStackTrace();
            return;
        }

        byte[] request = new byte[1024];
        byte[] reply = new byte[4096];

        while (!stopping) {
            try {
                DatagramPacket from_client = new DatagramPacket(request, request.length);
                ds.receive(from_client);

                byte[] real_request = new byte[from_client.getLength()];
                System.arraycopy(request, 0, real_request, 0, from_client.getLength());

                DatagramPacket sendPacket = new DatagramPacket(real_request, real_request.length, IPAddress, starboundPort);
                ds.send(sendPacket);

                DatagramPacket from_server = new DatagramPacket(reply, reply.length);
                ds.receive(from_server);
                byte[] real_reply = new byte[from_server.getLength()];
                System.arraycopy(reply, 0, real_reply, 0, from_server.getLength());

                InetAddress address = from_client.getAddress();
                int port = from_client.getPort();
                DatagramPacket to_client = new DatagramPacket(real_reply, real_reply.length, address, port);
                ds.send(to_client);
            } catch (Exception e) {
                StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
                return;
            }
        }
    }
}