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

package centralserver;


import network.StarNubMessageServerInitializer;
import utilities.connectivity.server.TCPServer;

public final class CentralServer {

    private static TCPServer tcpServer = new TCPServer("StarNub - Central - Connection Thread", "StarNub - Central - Worker Thread");


    //Event Router, with threading
    //Server. channels
    //Serial Loading
    //Serial Binding


    //client will get hash, check hash on numbers, then open another connection to verify count before starting

    public static void main(String[] args) {
        try {
//            SelfSignedCertificate ssc = new SelfSignedCertificate();
//            SslContext sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
            tcpServer.start(666, new StarNubMessageServerInitializer());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
