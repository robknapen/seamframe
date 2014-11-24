/*
 * seamframe: ExperimentComponent.java
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

package org.seamless_ip.modelling.components.experiment;

import java.util.List;

import nl.alterra.openmi.sdk.backbone.Event;
import nl.alterra.openmi.sdk.backbone.ValueSet;

import org.openmi.standard.ITime;
import org.openmi.standard.IEvent.EventType;
import org.seamless_if.processing.sofa.SeamException;
import org.seamless_if.processing.sofa.SeamLinkableComponent;
import org.seamless_ip.ontologies.agrirule.AgromanagementConfiguration;
import org.seamless_ip.ontologies.capri.CutfactorSubsidies;
import org.seamless_ip.ontologies.capri.EquilibriumPrice;
import org.seamless_ip.ontologies.seamproj.BiophysicalSimulation;
import org.seamless_ip.ontologies.seamproj.Experiment;
import org.seamless_ip.ontologies.seamproj.Outlook;
import org.seamless_ip.ontologies.seamproj.PolicyAssessment;
import org.seamless_ip.ontologies.seamproj.Problem;

public class ExperimentComponent extends SeamLinkableComponent {
    private static final String NOT_LOAD_OUTPUT = "ExperimentComponent could not load output set from DB for concept % and experiment";
    private static final int EXPERIMENT_COMPONENT_VERSION = 1;

    public ExperimentComponent(String ID) {
        super(ID);

        super.setDescription("This component takes an Experiment...");

        registerInputExchangeItem(Experiment.class);

        registerOutputExchangeItem(BiophysicalSimulation.class);
        // registerOutputExchangeItem(IIndicatorValue.class);
        registerOutputExchangeItem(Problem.class);

        registerOutputExchangeItem(CutfactorSubsidies.class);
        registerOutputExchangeItem(EquilibriumPrice.class);
        registerOutputExchangeItem(PolicyAssessment.class);
        registerOutputExchangeItem(AgromanagementConfiguration.class);
        registerOutputExchangeItem(Outlook.class);

    }


    protected void executeHook(ITime time, Class<?> T, List<String> ids) {
        executeHook(time, T);
    }

    @SuppressWarnings("unchecked")
    protected void executeHook(ITime time, Class<?> T) {
        // ValueSet<Experiment> vsE = (ValueSet<Experiment>)
        // inputs.get(Experiment.class);
        // Experiment aE = vsE.iterator().next();
        ValueSet<Experiment> experimentValueSet = inputs.get(Experiment.class);
        Experiment aE = experimentValueSet.iterator().next();
        if (aE.getPublished()) {
            Event event = new Event();
            event.setType(EventType.Other);
            event.setDescription("ABORT: this experiment was published and should not be re-run");
            sendEvent(event);
        }
        if (aE.getBaselineExperiment() == null || aE.getBaseYearExperiment() == null) {
            throw new SeamException("baseline experiment or baseyear experiment not set for Experiment with id = " + aE.getId() + " and title = " + aE.getTitle());
        }

        // Problem
        ValueSet<Problem> aProblemValueSet = new ValueSet<Problem>();
        aProblemValueSet.add(aE.getOfProblem());
        if (aProblemValueSet.isEmpty()) {
            throw new SeamException(String.format(NOT_LOAD_OUTPUT + aE.getId(), Problem.class));
        }
        outputs.put(Problem.class, aProblemValueSet);

        try {
//			add the biophysical simulations of policy, baseyear and baseline experiment
            ValueSet<BiophysicalSimulation> biophysicalSimulationvalueSet = new ValueSet<BiophysicalSimulation>();
            biophysicalSimulationvalueSet.add(aE.getBiophysicalSimulation());
            biophysicalSimulationvalueSet.add(aE.getBaselineExperiment().getBiophysicalSimulation());
            biophysicalSimulationvalueSet.add(aE.getBaseYearExperiment().getBiophysicalSimulation());
            if (biophysicalSimulationvalueSet.isEmpty()) {
                throw new SeamException(String.format(NOT_LOAD_OUTPUT + aE.getId(), BiophysicalSimulation.class));
            }
            outputs.put(BiophysicalSimulation.class, biophysicalSimulationvalueSet);
        } catch (Exception e) {
            logger.error(BiophysicalSimulation.class.getName() + " : " + e.getMessage());
            throw new SeamException("problem retrieving biophysical simulations for experiment with id = " + aE.getId(), e);
        }

        // We can't get miljons of indicatorvalues
//		try {
//			outputs.put(IIndicatorValue.class, new ValueSet<IIndicatorValue>(aE
//					.getIndicatorValues()));
//
//		} catch (Exception e) {
//			logger.error(IIndicatorValue.class.getName() + " : "
//					+ e.getMessage());
//		}
        try {
//			return the cut-factor subsidies of the baseline experiment: these are used by some models for calibrating the baseline
            if (aE.getBaselineExperiment().getPolicyAssessment().getCutFactorSubsidies() == null || aE.getBaselineExperiment().getPolicyAssessment().getCutFactorSubsidies().isEmpty()) {
                throw new SeamException(String.format(NOT_LOAD_OUTPUT + aE.getId(), CutfactorSubsidies.class));
            }
            outputs.put(CutfactorSubsidies.class,
                    new ValueSet<CutfactorSubsidies>(aE.getBaselineExperiment().getPolicyAssessment().getCutFactorSubsidies()));
        } catch (Exception e) {
            logger.error(CutfactorSubsidies.class.getName() + " : "
                    + e.getMessage());
            throw new SeamException("problem retrieving cut-factor subsidies for experiment with id = " + aE.getId(), e);
        }
        try {
//			return the equilibrium prices of the baseline experiment: these are used by some models for calibrating to the baseline.
            if (aE.getBaselineExperiment().getPolicyAssessment().getEquilibriumPrices() == null || aE.getBaselineExperiment().getPolicyAssessment().getEquilibriumPrices().isEmpty()) {
                throw new SeamException(String.format(NOT_LOAD_OUTPUT + aE.getId(), EquilibriumPrice.class));
            }
            outputs.put(EquilibriumPrice.class, new ValueSet<EquilibriumPrice>(
                    aE.getBaselineExperiment().getPolicyAssessment().getEquilibriumPrices()));

        } catch (Exception e) {
            logger.error(EquilibriumPrice.class.getName() + " : "
                    + e.getMessage());
            throw new SeamException("problem retrieving Equilibrium Prices for experiment with id = " + aE.getId(), e);
        }

        try {
//			add the policy assessments of policy, baseyear and baseline experiment
            ValueSet<PolicyAssessment> setPolicyAssessment = new ValueSet<PolicyAssessment>();
            setPolicyAssessment.add(aE.getPolicyAssessment());
            setPolicyAssessment.add(aE.getBaselineExperiment().getPolicyAssessment());
            setPolicyAssessment.add(aE.getBaseYearExperiment().getPolicyAssessment());
            if (setPolicyAssessment.isEmpty()) {
                throw new SeamException(String.format(NOT_LOAD_OUTPUT + aE.getId(), PolicyAssessment.class));
            }
            outputs.put(PolicyAssessment.class, setPolicyAssessment);

        } catch (Exception e) {
            logger.error(PolicyAssessment.class.getName() + " : " + e.getMessage());
            throw new SeamException("problem retrieving policy assessments for experiment with id = " + aE.getId(), e);
        }

        try {
            ValueSet<AgromanagementConfiguration> setAgroManConfigs = new ValueSet<AgromanagementConfiguration>();
            setAgroManConfigs.add(aE.getBiophysicalSimulation().getContext().getAgromanagementConfiguration());
            if (setAgroManConfigs.isEmpty()) {
                throw new SeamException(String.format(NOT_LOAD_OUTPUT + aE.getId(), AgromanagementConfiguration.class));
            }
            outputs.put(AgromanagementConfiguration.class, setAgroManConfigs);

        } catch (Exception e) {
            logger.error(AgromanagementConfiguration.class.getName() + " : " + e.getMessage());
            throw new SeamException("problem retrieving agro-management configurations for experiment with id = " + aE.getId(), e);
        }


        try {
            ValueSet<Outlook> setOutlook = new ValueSet<Outlook>();
            setOutlook.add(aE.getBiophysicalSimulation().getOutlook());
            if (setOutlook.isEmpty()) {
                throw new SeamException(String.format(NOT_LOAD_OUTPUT + aE.getId(), Outlook.class));
            }
            outputs.put(Outlook.class, setOutlook);

        } catch (Exception e) {
            logger.error(Outlook.class.getName() + " : "
                    + e.getMessage());
            throw new SeamException("problem retrieving outlooks for experiment with id = " + aE.getId(), e);
        }

    }

    // Only for testing
//	public void TestExecute() {
//		this.executeHook();
//	}

    // Only for testing
//	public void setFakeInput(Experiment Ei) {
//		ValueSet<Experiment> aEVS = new ValueSet<Experiment>(Ei);
//
//		inputs.put(Experiment.class, aEVS);
//		this.executeHook();
//	}


    @Override
    public String getComponentID() {
        return String.format("EXPERIMENT_V%d", EXPERIMENT_COMPONENT_VERSION);
    }


    @Override
    public String getModelID() {
        return getComponentID();
    }


}
