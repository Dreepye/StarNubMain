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

package starnubdata.network.ssl;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;

public class SSLContextProvider {

    private static final String pkcs12Base64 = ""; // use base64 encoded string from steps above
    private static SSLContext sslContext = null;

    public static SSLContext get() {
        if(sslContext==null) {
            synchronized (SSLContextProvider.class) {
                if(sslContext==null) {
                    try {
                        sslContext = SSLContext.getInstance("TLS");
                        KeyStore ks = KeyStore.getInstance("PKCS12");
                        ks.load(new ByteArrayInputStream(Base64.decode(pkcs12Base64)), "secret".toCharArray());
                        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                        kmf.init(ks, "secret".toCharArray());
                        sslContext.init(kmf.getKeyManagers(), null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sslContext;
    }
}
