/*
 * seamframe: Zip.java
 * ==============================================================================
 * This work has been carried out as part of the SEAMLESS Integrated Framework
 * project, EU 6th Framework Programme, contract no. 010036-2 and/or as part
 * of the SEAMLESS association.
 *
 * Copyright (c) 2009 The SEAMLESS Association.
 *
 * For more information: http://www.seamlessassociation.org;
 * email: info@seamless-if.org
 *
 * The contents of this file is subject to the SEAMLESS Association License for
 * software infrastructure and model components Version 1.1 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at http://www.seamlessassociation.org/License.htm
 *
 * Software distributed under the License is distributed on an "AS IS"  basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific governing rights and limitations.
 *
 * The Initial Developers of the Original Code are:
 *  - Ioannis Athanasiadis; IDSIA Dalle Molle Institute for Artificial Intelligence
 *  - Sander Janssen; Alterra, Wageningen UR
 *  - Benny Johnsson; Lund University
 *  - Rob Knapen; Alterra, Wageningen UR
 *  - Hongtao Li; IDSIA Dalle Molle Institute for Artificial Intelligence
 *  - Michiel Rop; Alterra, Wageningen UR / ilionX
 *  - Lorenzo Ruinelli; IDSIA Dalle Molle Institute for Artificial Intelligence
 *
 * ================================================================================
 * Contributor(s): N/A
 * ================================================================================
 */
package org.seamless_ip.core.utilities.filefunctions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @author Benny Jonsson
 */
public class Zip {
    public static void zipDirectory(String dir, String zipfile) throws IllegalArgumentException, IOException {
        zipDirectory(dir, zipfile, null);
    }

    public static void zipDirectory(String dir, String zipfile, String comment)
            throws IllegalArgumentException, IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));

        zipDirectory(dir, dir, zipfile, out);
        if (comment != null) {
            out.setComment(comment);
        }
        out.flush();
        out.close();
    }

    // FIXME There is a bug in this code...
    /**
     * Zip the contents of the directory, and save it in the zipfile
     */
    private static void zipDirectory(String baseDir, String dir,
                                     String zipfile, ZipOutputStream out) throws IOException,
            IllegalArgumentException {
        // Check that the directory is a directory, and get its contents
        File d = new File(dir);
        if (!d.isDirectory()) {
            throw new IllegalArgumentException("Not a directory:  " + dir);
        }

        String[] entries = d.list();


        for (int i = 0; i < entries.length; i++) {
            File f = new File(d, entries[i]);
            if (f.isDirectory()) {
                String filePath = f.getPath();
                boolean hasFiles = false;
                File[] listFiles = f.listFiles();
                for (File file : listFiles) {
                    if (file.isFile()) {
                        hasFiles = true;
                        break;
                    }
                }
                if (!hasFiles) {
                    // Workaround to zip empty folder
                    File dotFile = new File(f.getPath() + "\\.emptyDir");
                    dotFile.createNewFile();
                }

                zipDirectory(baseDir, filePath, zipfile, out);
                continue;
            }
            FileInputStream in = new FileInputStream(f); // Stream to read
            // file
            ZipEntry entry = new ZipEntry(f.getPath().substring(
                    baseDir.length() + 1)); // Make a ZipEntry
            out.putNextEntry(entry); // Store entry
            byte[] buffer = new byte[4096]; // Create a buffer for copying
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            in.close();

        }
    }
	
}
