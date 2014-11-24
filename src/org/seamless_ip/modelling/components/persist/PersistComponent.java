/*
 * seamframe: PersistComponent.java
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
package org.seamless_ip.modelling.components.persist;

import java.util.HashSet;
import java.util.List;

import nl.alterra.openmi.sdk.backbone.ValueSet;
import nl.alterra.openmi.sdk.extensions.IOutputExchangeItemEx;

import org.openmi.standard.IElementSet;
import org.openmi.standard.ILink;
import org.openmi.standard.ITime;
import org.openmi.standard.IValueSet;
import org.seamless_if.domain.HibernateDomainManager;
import org.seamless_if.domain.IDomainManager;
import org.seamless_if.processing.sofa.SeamLinkableComponent;
import org.seamless_ip.ontologies.indi.IIndicatorValue;
import org.seamless_ip.ontologies.seamproj.Experiment;


public class PersistComponent extends SeamLinkableComponent {

    private static final int PERSISTER_COMPONENT_VERSION = 1;

    private IDomainManager dm;

    public PersistComponent(String ID) {
        super(ID);
        dm = HibernateDomainManager.getInstance();

        registerInputExchangeItem(IIndicatorValue.class);

        registerInputExchangeItem(Experiment.class);
        registerOutputExchangeItem(IIndicatorValue.class);

    }

    @Override
    public IValueSet getValuesHook(ITime time, ILink link) {
        IElementSet targetElementSet = link.getTargetElementSet();
        ((IOutputExchangeItemEx) this.getOutputExchangeItem(0)).setElementSet(targetElementSet);
        return super.getValuesHook(time, link);
    }

    protected void executeHook(ITime time, Class<?> T, List<String> ids) {
        executeHook(time, T);
    }

    protected void executeHook(ITime time, Class<?> T) {

        logger.info("Starting persisting");

        Experiment experiment = (Experiment) inputs.get(Experiment.class).get(0);
        Integer objectsToPersist = 0;
        setIndicatorsToExperiment(experiment, objectsToPersist);

        dm.persist(experiment);
        sendInformativeEvent(String.format("Persisted %d results-objects to DB.", objectsToPersist));

        logger.info("Persisting ready");
    }

    @SuppressWarnings("unchecked")
    protected void setIndicatorsToExperiment(Experiment experiment, Integer objectsToPersist) {
        if (inputs.containsKey(IIndicatorValue.class) && inputs.get(IIndicatorValue.class).size() > 0) {

            ValueSet<IIndicatorValue> indicators = inputs.get(IIndicatorValue.class);
            for (IIndicatorValue indicatorValue : indicators) {
                indicatorValue.setExperiment(experiment);
            }

            java.util.Set<IIndicatorValue> setIIndvalue = new HashSet<IIndicatorValue>();
            setIIndvalue.addAll(indicators);
            experiment.setIndicatorValues(setIIndvalue);
            objectsToPersist += indicators.size();
            outputs.put(IIndicatorValue.class, inputs.get(IIndicatorValue.class));
        }
    }

    @Override
    public String getComponentID() {
        return String.format("PERSISTER_V%d", PERSISTER_COMPONENT_VERSION);
    }

    @Override
    public String getModelID() {
        return getComponentID();
    }

}