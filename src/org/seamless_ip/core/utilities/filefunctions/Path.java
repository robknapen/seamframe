/*
 * seamframe: Path.java
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

import org.apache.log4j.Logger;
import org.seamless_if.processing.sofa.SeamException;

/**
 * @author Benny Jonsson
 */
public class Path {
    private static Logger logger = Logger.getLogger(Path.class);

    /**
     * @param path
     * @return Absolute path to file, add \ if dir and fix environment variable
     * @throws Exception
     */
    public static String fixPath(String path) throws Exception {
        String retFile = "";
        // If path contains % assume it's an environment variable'
        if (path.contains("%")) {
            int fromIndex = 0;
            while (fromIndex <= path.length()) {
                int startPos = path.indexOf('%', fromIndex) + 1;
                if (startPos == -1) {
                    break;
                }
                int endPos = path.indexOf('%', startPos);
                if (endPos == -1) {
                    break;
                }
                String token = path.substring(startPos, endPos);
                if (System.getenv().containsKey(token)) {
                    path = path.replace(token, System.getenv(token));
                } else {
                    SeamException seamException = new SeamException(String
                            .format("Environment variable %s missing", token));
                    logger.error(seamException.getMessage(), seamException);
                    throw seamException;
                }
                fromIndex = fromIndex + 1;
            }
            path = path.replaceAll("%", "");
        }

        if (path != null) {

            if (new File(path).isDirectory() && !path.endsWith("\\")) {
                path += File.separator;
            }

            File theFile = new File(path);

            retFile = theFile.getAbsolutePath();

            if (new File(retFile).isDirectory() && (!retFile.endsWith("\\") || !retFile.endsWith("/"))) {
                retFile += File.separator;
            }

        }

        return retFile;
	}
}
