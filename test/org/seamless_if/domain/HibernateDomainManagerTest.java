/*
 * seamframe: HibernateDomainManagerTest.java
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
 * ================================================================================
 */
package org.seamless_if.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.seamless_ip.ontologies.crop.Crop;

/**
 * Unit test for the HibernateDomainManager class.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class HibernateDomainManagerTest {

    // use a class variable domain manager for testing
    private static IDomainManager dm;


    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("Starting Hibernate session for seamfaces, this might take a while...");
        try {
            // Set up a domain manager for testing. For now use the SEAMLESS related  
            dm = HibernateDomainManager.initialize(
                    "seamfaces",
                    "jdbc:postgresql://data.seamless-if.org/seamdb_0902",
                    "seamless",
                    "s34md4t4",
                    true
                    );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @AfterClass
    public static void tearDownClass() throws Exception {
        System.out.println("Closing domain manager");
        dm.close();
    }


    @Test
    public void testInitialize() {
        // check if dm is initialized
        assertNotNull(dm);        
    }


    @Test
    public void testRetrieveObject() {
        System.out.println("Retrieving Crop instance with id 1");
        Object obj = dm.retrieve(Crop.class, 1L);
        assertNotNull(obj);
        assertTrue(obj instanceof Crop);
        assertTrue(((Crop) obj).getId().equals(1L));
    }


    @Test
    public void testStoreAndDeleteObject() {
        Crop crop = new Crop();
        assertNull(crop.getId());

        // store the new crop
        System.out.println("Storing new test crop instance");
        dm.persist(crop);

        // should have gotten an id now
        Long cropId = crop.getId();
        assertNotNull(cropId);
        System.out.println("Test crop instance id = " + cropId);

        // retrieve the crop instance, should be equal
        Crop dbCrop = dm.retrieve(Crop.class, cropId);
        assertEquals(crop, dbCrop);
        dbCrop = null;

        System.out.println("Deleting test crop instance with id " + cropId);
        dm.delete(crop);
        crop = null;
        assertNull(dm.retrieve(Crop.class, cropId));
    }

}
