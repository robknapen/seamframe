/*
 * seamframe: NameGenerator.java
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

package org.seamless_ip.core.utilities.unique;

import java.rmi.server.UID;

import org.apache.log4j.Logger;

/**
 * A Unique name generator used to create names that can be used
 * eighter as URI's (no special char) or even in GAMS variables
 * (short names)
 *
 * @author david
 * @author Ioannis N. Athanasiadis
 */
public class NameGenerator {

    private String baseName;
    private Logger log = Logger.getLogger("org.seamless_ip.core.utilities.unique.NameGenerator");

    public NameGenerator() {
        this("i");
    }

    public NameGenerator(String baseName) {
        this.baseName = baseName;
    }

    static int num = 0;

    /**
     * Generated a short unique name (used for uri's)
     *
     * @return
     */
    public String generateName() {
        DataStore dw = new DataStore();
        UID id = new UID();
        try {
            id.write(dw);
        } catch (Exception e) {
            log.info(e);
        }

        return baseName + dw.toString();
    }

    private static Object mutex = new Object();
    private static int shortName = 0;

    /**
     * Shorter name but not unique over multiple runs
     *
     * @return
     */
    public String generateShortName() {
        int lng;
        synchronized (mutex) {
            lng = shortName++;
        }
        return "G" + Integer.toHexString(lng);
    }
}
