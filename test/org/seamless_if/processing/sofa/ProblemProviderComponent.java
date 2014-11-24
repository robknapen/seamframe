/*
 * seamframe: ProblemProviderComponent.java
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

import java.util.HashSet;
import java.util.List;

import nl.alterra.openmi.sdk.backbone.ValueSet;

import org.openmi.standard.ITime;
import org.seamless_ip.ontologies.indi.EndorsedIndicator;
import org.seamless_ip.ontologies.seamproj.Model;
import org.seamless_ip.ontologies.seamproj.Problem;

/**
 * Test SeamLinkableComponent that has an output for the Problem class
 * and returns a test Problem instance.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class ProblemProviderComponent extends SeamLinkableComponent {
    private Problem problem;
    private ValueSet<Problem> outProblem;

    private final static Model model = new Model();

    static {
        model.setId(2L);
        model.setName("ProblemProviderComponent");
        model.setVersion("V1");
        model.setDescription("Simple model providing a sample Problem instance for testing purposes only.");
    }


    public static Model getModel() {
        return model;
    }


    @SuppressWarnings("unchecked")
    public ProblemProviderComponent(String ID) {
        // create without use of DomainManager
        super(ID);

        outProblem = registerOutputExchangeItem(Problem.class);

        problem = new Problem();
        problem.setId(1L);
        problem.setDescription("Sample Problem instance for testing purposes only.");

        // add a selection of indicators
        HashSet indicators = new HashSet();

        EndorsedIndicator indicator1 = new EndorsedIndicator();
        indicator1.setId(1L);
        indicator1.setModel(IndicatorValueProviderComponent.getModel());
        indicators.add(indicator1);

        problem.setIndicators(indicators);
    }


    @Override
    protected void executeHook(ITime time, Class<?> T) throws SeamException {
        // fake update of the Problem output
        outProblem.clear();
        outProblem.add(problem);
    }


    @Override
    protected void executeHook(ITime time, Class<?> T, List<String> ids) throws SeamException {
        // not calculating for specific IDs
        executeHook(time, T);
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
