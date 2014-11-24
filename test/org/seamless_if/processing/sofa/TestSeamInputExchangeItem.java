/*
 * seamframe: TestSeamInputExchangeItem.java
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
package org.seamless_if.processing.sofa;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmi.standard.IQuantity.ValueType;
import org.seamless_ip.ontologies.farm.RepresentativeFarm;
import org.seamless_ip.ontologies.seamproj.Problem;

/**
 * Unit test for SeamInputExchangeItem.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class TestSeamInputExchangeItem {
    private SeamInputExchangeItem<Problem> inputProblem;
    private SeamInputExchangeItem<RepresentativeFarm> inputFarmType;


    @Before
    public void setUp() {
        inputProblem = new SeamInputExchangeItem<Problem>(null, Problem.class);
        inputFarmType = new SeamInputExchangeItem<RepresentativeFarm>(null, RepresentativeFarm.class);
    }

    @After
    public void tearDown() {
        // void
    }


    @Test
    public void testConstruction() {
        Assert.assertSame(Problem.class, inputProblem.getOntologyType());
        Assert.assertEquals(Problem.class.getSimpleName(), inputProblem.getID());
        Assert.assertEquals(ValueType.Scalar, inputProblem.getValueSetType());
    }


    @Test
    public void testToString() {
        Assert.assertEquals("SeamExchangeItem 'Problem' for type 'class org.seamless_ip.ontologies.seamproj.Problem' of component 'None'", inputProblem.toString());
    }


    @Test
    public void testEquals() {
        Assert.assertTrue(inputProblem.equals(inputProblem));
        Assert.assertTrue(inputProblem.equals(new SeamInputExchangeItem<Problem>(null, Problem.class)));
        Assert.assertFalse(inputProblem.equals(inputFarmType));
    }


    @Test
    public void testIsConnectableWith() {
        SeamOutputExchangeItem<Problem> outputProblem = new SeamOutputExchangeItem<Problem>(null, Problem.class);

        Assert.assertTrue(inputProblem.isConnectableWith(outputProblem));
        Assert.assertFalse(inputFarmType.isConnectableWith(outputProblem));
    }
}
