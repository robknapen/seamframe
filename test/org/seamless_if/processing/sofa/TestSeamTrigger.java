/*
 * seamframe: TestSeamTrigger.java
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

import nl.alterra.openmi.sdk.backbone.LinkableComponent;
import nl.alterra.openmi.sdk.backbone.ValueSet;
import nl.alterra.openmi.sdk.configuration.Composition;
import nl.alterra.openmi.sdk.configuration.SystemDeployer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmi.standard.IEvent;
import org.openmi.standard.IListener;
import org.openmi.standard.IEvent.EventType;
import org.seamless_ip.ontologies.indi.IIndicatorValue;
import org.seamless_ip.ontologies.indi.IndicatorValueSimple;
import org.seamless_ip.ontologies.seamproj.Problem;


/**
 * Unit test for SeamTrigger.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class TestSeamTrigger {
    private SeamTrigger trigger;
    private Composition composition;


    @Before
    public void setUp() {
        composition = new Composition("composition");

        trigger = new SeamTrigger("trigger");
        ProblemProviderComponent problemLC = new ProblemProviderComponent("problemLC");
        IndicatorValueProviderComponent indicatorValueLC = new IndicatorValueProviderComponent("indicatorValueLC");

        composition.addComponent(problemLC);
        composition.addComponent(indicatorValueLC);
        composition.addComponent(trigger);

        composition.createLink(
                problemLC, problemLC.getOutputExchangeItem(Problem.class.getSimpleName()),
                trigger, trigger.getInputExchangeItem(Problem.class.getSimpleName())
        );

        composition.createLink(
                indicatorValueLC, indicatorValueLC.getOutputExchangeItem(IIndicatorValue.class.getSimpleName()),
                trigger, trigger.getInputExchangeItem(IIndicatorValue.class.getSimpleName())
        );
    }


    @After
    public void tearDown() {
        // void
    }


    @Test
    public void testConstruction() {
        Assert.assertSame("trigger", trigger.getID());
        Assert.assertSame(3, composition.getLinkableComponents().length);
        Assert.assertSame(1, composition.getTriggers().size());
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testCalculation() {
        SystemDeployer deployer = new SystemDeployer("deployer");
        deployer.setComposition(composition);
        deployer.setTriggers(composition.getTriggers());
        deployer.setBlocking(true);

        final EventType[] ets = new EventType[]
                {
                        EventType.DataChanged,
                        EventType.GlobalProgress,
                        EventType.Informative,
                        EventType.Other,
                        EventType.SourceAfterGetValuesCall,
                        EventType.SourceBeforeGetValuesReturn,
                        EventType.TargetAfterGetValuesReturn
                };

        IListener listener = new IListener() {
            public EventType getAcceptedEventType(int i) {
                return ets[i];
            }

            public int getAcceptedEventTypeCount() {
                return ets.length;
            }

            public void onEvent(IEvent iEvent) {
                System.out.println("EVENT " + iEvent.getSender().toString() + "/" + iEvent.getDescription());
            }
        };

        deployer.subscribe(listener, ets);

        for (int i = 0; i < composition.getLinkableComponents().length; i++) {
            System.out.println(composition.getLinkableComponents()[i]);
            ((LinkableComponent) composition.getLinkableComponents()[i]).subscribe(listener, ets);
        }

        deployer.start();

        ValueSet<IIndicatorValue> result = (ValueSet<IIndicatorValue>) trigger.getLastCalculatedValues();
        Assert.assertSame(2, result.getCount());
        Assert.assertTrue(result.getValue(0) instanceof IndicatorValueSimple);
        Assert.assertEquals(1.0F, result.getValue(0).getValue(), 0.1);
        Assert.assertTrue(result.getValue(1) instanceof IndicatorValueSimple);
        Assert.assertEquals(2.0F, result.getValue(1).getValue(), 0.1);
    }

}
