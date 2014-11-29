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

public class GetFileSize {

    public static double getFileSize (String fileString, String fileSizeMeasure) {
        File file =new File(fileString);
        return getFileSize(file, fileSizeMeasure);
    }

    public static double getFileSize (File file, String fileSizeMeasure) {
        double bytes = 0;
        if (file.exists()) {
            bytes = file.length();
        } else {
            System.out.println("File does not exists!");
        }
        return fileSizeMeasure(bytes, fileSizeMeasure);
    }

    private static double fileSizeMeasure (double bytes, String fileSizeMeasure) {
        double newBytes = 0;

        switch (fileSizeMeasure) {
            case "kilobytes": newBytes = (bytes / 1024); break;
            case "megabytes": newBytes = (bytes / 1048576); break;
            case "gigabytes": newBytes = (bytes / 1073741824); break;
            case "terabytes": newBytes = (bytes / 1073741824); break;
        }
        return newBytes;
    }
}
