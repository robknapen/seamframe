/*
 * seamframe: IndicatorValueProviderComponent.java
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

import java.util.List;

import nl.alterra.openmi.sdk.backbone.ValueSet;

import org.openmi.standard.ITime;
import org.seamless_ip.ontologies.indi.EndorsedIndicator;
import org.seamless_ip.ontologies.indi.IIndicatorValue;
import org.seamless_ip.ontologies.indi.IndicatorValueSimple;
import org.seamless_ip.ontologies.seamproj.Model;

/**
 * Test SeamLinkableComponent that has an output for IIndicatorValue class
 * and returns some test indicator values.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class IndicatorValueProviderComponent extends SeamLinkableComponent {
    private ValueSet<IIndicatorValue> output;

    private IndicatorValueSimple value1;
    private IndicatorValueSimple value2;
    private IndicatorValueSimple value3;

    private final static Model model = new Model();

    static {
        model.setId(1L);
        model.setName("IndicatorValueProviderComponent");
        model.setVersion("V1");
        model.setDescription("Simple model providing some indicator values for testing purposes only.");
    }


    public static Model getModel() {
        return model;
    }


    @SuppressWarnings("unchecked")
    public IndicatorValueProviderComponent(String ID) {
        // create without use of DomainManager
        super(ID);

        output = registerOutputExchangeItem(IIndicatorValue.class);

        EndorsedIndicator indicator1 = new EndorsedIndicator();
        indicator1.setId(1L);
        indicator1.setDescription("Sample Indicator instance (1) for testing purposes only.");
        indicator1.setModel(model);

        value1 = new IndicatorValueSimple();
        value1.setId(1L);
        value1.setValue(1.0F);
        value1.setIndicator(indicator1);

        value2 = new IndicatorValueSimple();
        value2.setId(2L);
        value2.setValue(2.0F);
        value2.setIndicator(indicator1);

        EndorsedIndicator indicator2 = new EndorsedIndicator();
        indicator2.setId(2L);
        indicator2.setDescription("Sample Indicator instance (2) for testing purposes only.");
        indicator2.setModel(model);

        value3 = new IndicatorValueSimple();
        value3.setId(3L);
        value3.setValue(3.0F);
        value3.setIndicator(indicator2);
    }


    @Override
    protected void executeHook(ITime time, Class<?> T) throws SeamException {
        if (T.equals(IIndicatorValue.class)) {
            output.clear();
            output.add(value1);
            output.add(value2);
            output.add(value3);
        }
    }


    @Override
    protected void executeHook(ITime time, Class<?> T, List<String> ids) throws SeamException {
        if (T.equals(IIndicatorValue.class)) {
            output.clear();
            if (ids.contains(value1.getIndicator().getId().toString()))
                output.add(value1);
            if (ids.contains(value2.getIndicator().getId().toString()))
                output.add(value2);
            if (ids.contains(value3.getIndicator().getId().toString()))
                output.add(value3);
        }
    }


    @Override
    public String getComponentID() {
        return getModelID();
    }


    @Override
    public String getModelID() {
        return model.getName() + "_" + model.getVersion();
    }

}
