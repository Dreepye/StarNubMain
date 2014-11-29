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

package utilities.file.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JarResourceToDisk {

    public void fileUnpack(String resourceStringName, String fileString, boolean overWrite) throws IOException {

        //TODO size change overwrite, time based overwrite
        ClassLoader cl = JarResourceToDisk.class.getClassLoader();
        File target = new File(fileString);

        InputStream in = cl.getResourceAsStream(resourceStringName);
        if (in == null) {
            return;
        }

        if (target.exists() && !overWrite) {
            return;
        }

        FileOutputStream out = new FileOutputStream(target);

        byte[] buf = new byte[8*1024];
        int len;
        while((len = in.read(buf)) != -1)
        {
            out.write(buf,0,len);
        }
        out.close();
        in.close();
    }
}
