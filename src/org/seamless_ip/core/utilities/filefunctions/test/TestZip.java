/*
 * seamframe: TestZip.java
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
package org.seamless_ip.core.utilities.filefunctions.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.seamless_ip.core.utilities.filefunctions.DeleteDir;
import org.seamless_ip.core.utilities.filefunctions.Zip;

/**
 * Testing the zip class
 *
 * @author Benny Jonsson
 */
public class TestZip {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }


    private String dir;
    private String zip;

    @Before
    public void setUp() throws Exception {
        dir = String.format("%s\\TestZip", System.getenv("temp"));
        zip = String.format("%s\\TestZip.zip", System.getenv("temp"));
        File file;
        String subDirs = dir;
        for (int i = 0; i < 5; i++) {
            file = new File(subDirs);
            file.mkdir();
            if (i % 2 == 0) {
                new File(subDirs + "\\afile.txt").createNewFile();
            }
            subDirs += "\\" + i;
        }
    }

    @After
    public void tearDown() throws Exception {
        DeleteDir.deleteDir(dir);
        new File(zip).delete();
    }

    @Test
    public void zipDirectory() {

        try {
            Zip.zipDirectory(dir, zip);
        } catch (IllegalArgumentException e) {
            Assert.fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }

        assertTrue(String.format("Zip file %s not created", zip), new File(zip).isFile());
    }


}
