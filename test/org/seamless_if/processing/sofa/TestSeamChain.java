/*
 * seamframe: TestSeamChain.java
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

import nl.alterra.openmi.sdk.backbone.ValueSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.seamless_ip.ontologies.indi.IIndicatorValue;
import org.seamless_ip.ontologies.indi.IndicatorValueSimple;
import org.seamless_ip.ontologies.seamproj.Problem;

/**
 * Unit test for SeamChain.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class TestSeamChain {
    private SeamTrigger trigger;
    private ProblemProviderComponent problemLC;
    private IndicatorValueProviderComponent indicatorValueLC;
    private SeamChain chain;

    @Before
    public void setUp() {
        chain = new SeamChain("Test Model Chain");

        trigger = new SeamTrigger("trigger");
        problemLC = new ProblemProviderComponent("problemLC");
        indicatorValueLC = new IndicatorValueProviderComponent("indicatorValueLC");

        chain.addComponent(problemLC);
        chain.addComponent(indicatorValueLC);
        chain.addComponent(trigger);

        chain.createLink(problemLC, trigger, Problem.class);
        chain.createLink(indicatorValueLC, trigger, IIndicatorValue.class);
    }


    @After
    public void tearDown() {
        // void
    }

    @Test
    public void testConstruction() {
        Assert.assertSame(3, chain.getComposition().getLinkableComponents().length);
        Assert.assertSame(1, chain.getComposition().getTriggers().size());
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testCalculation() {
        chain.initialize(null);
        chain.execute();

        ValueSet<IIndicatorValue> result = (ValueSet<IIndicatorValue>) trigger.getLastCalculatedValues();
        Assert.assertSame(2, result.getCount());
        Assert.assertTrue(result.getValue(0) instanceof IndicatorValueSimple);
        Assert.assertEquals(1.0F, ((IndicatorValueSimple) result.getValue(0)).getValue(), 0.1);
        Assert.assertTrue(result.getValue(1) instanceof IndicatorValueSimple);
        Assert.assertEquals(2.0F, ((IndicatorValueSimple) result.getValue(1)).getValue(), 0.1);
    }

}
