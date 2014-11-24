/*
 * seamframe: DmComponent.java
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

package org.seamless_ip.modelling.components.dm;

import java.util.List;
import java.util.Set;

import nl.alterra.openmi.sdk.backbone.Argument;
import nl.alterra.openmi.sdk.backbone.ValueSet;

import org.openmi.standard.IArgument;
import org.openmi.standard.ITime;
import org.seamless_if.domain.HibernateDomainManager;
import org.seamless_if.domain.IDomainManager;
import org.seamless_if.processing.sofa.SeamException;
import org.seamless_if.processing.sofa.SeamLinkableComponent;
import org.seamless_ip.ontologies.capri.ActivityGroup;
import org.seamless_ip.ontologies.capri.Country;
import org.seamless_ip.ontologies.capri.CountryAggregate;
import org.seamless_ip.ontologies.capri.CutfactorSubsidies;
import org.seamless_ip.ontologies.capri.EquilibriumPrice;
import org.seamless_ip.ontologies.capri.IProductionItem;
import org.seamless_ip.ontologies.capri.PremiumGroup;
import org.seamless_ip.ontologies.capri.PriceElasticity;
import org.seamless_ip.ontologies.crop.CropProduct;
import org.seamless_ip.ontologies.crop.ProductGroup;
import org.seamless_ip.ontologies.crop.ProductType;
import org.seamless_ip.ontologies.crop.SimpleCropGroup;
import org.seamless_ip.ontologies.farm.IRegion;
import org.seamless_ip.ontologies.farm.TransitionProbability;
import org.seamless_ip.ontologies.farmopt.OptimalFarmBehaviour;
import org.seamless_ip.ontologies.farmopt.ProductionActivityPerFSSIMFarm;
import org.seamless_ip.ontologies.indi.IIndicator;
import org.seamless_ip.ontologies.indi.IIndicatorValue;
import org.seamless_ip.ontologies.indi.IndicatorValueActivity;
import org.seamless_ip.ontologies.indi.IndicatorValueActivityGroupCountry;
import org.seamless_ip.ontologies.indi.IndicatorValueActivityGroupCountryAggregate;
import org.seamless_ip.ontologies.indi.IndicatorValueActivityGroupNUTSRegion;
import org.seamless_ip.ontologies.indi.IndicatorValueBetweenCountryAggregates;
import org.seamless_ip.ontologies.indi.IndicatorValueCountry;
import org.seamless_ip.ontologies.indi.IndicatorValueCountryAggregate;
import org.seamless_ip.ontologies.indi.IndicatorValueCrop;
import org.seamless_ip.ontologies.indi.IndicatorValueFarm;
import org.seamless_ip.ontologies.indi.IndicatorValueFarmAgriEnvironmentalZone;
import org.seamless_ip.ontologies.indi.IndicatorValueInputGroupCountry;
import org.seamless_ip.ontologies.indi.IndicatorValueInputGroupCountryAggregate;
import org.seamless_ip.ontologies.indi.IndicatorValueInputGroupNUTSRegion;
import org.seamless_ip.ontologies.indi.IndicatorValueNUTSRegion;
import org.seamless_ip.ontologies.indi.IndicatorValueProductGroupCountry;
import org.seamless_ip.ontologies.indi.IndicatorValueProductGroupCountryAggregate;
import org.seamless_ip.ontologies.indi.IndicatorValueProductGroupNUTSRegion;
import org.seamless_ip.ontologies.livestock.IFeedStuff;
import org.seamless_ip.ontologies.seamproj.Experiment;
import org.seamless_ip.ontologies.seamproj.Problem;


public class DmComponent extends SeamLinkableComponent {

    private static final String OUTPUT_NOT_LOADED = "DMcomponent could not load set of output items from DB for concept %s";

    private static final int DM_COMPONENT_VERSION = 1;

    private IDomainManager dm;

    private boolean deleteOldResults = false;

    public static final Argument OVERRIDE_RESULTS = new Argument("OVERRIDE_RESULTS", "", false, "Argument to indicate that old results should be deleted from the Experiment");


    public DmComponent(String ID) {
        super(ID);

        dm = HibernateDomainManager.getInstance();

        registerOutputExchangeItem(Experiment.class);
        registerOutputExchangeItem(IRegion.class);
        registerOutputExchangeItem(ActivityGroup.class);
        registerOutputExchangeItem(ProductGroup.class);
        registerOutputExchangeItem(IFeedStuff.class);

        registerOutputExchangeItem(CountryAggregate.class);
        registerOutputExchangeItem(Country.class);
        registerOutputExchangeItem(SimpleCropGroup.class);
        registerOutputExchangeItem(ProductType.class);
        registerOutputExchangeItem(CropProduct.class);

        // Moved to ExperimentComponent
        // registerOutputExchangeItem(PolicyOption.class);

        registerOutputExchangeItem(Problem.class);


        registerOutputExchangeItem(IProductionItem.class);

        registerOutputExchangeItem(PremiumGroup.class);
        registerOutputExchangeItem(IIndicator.class);
        registerOutputExchangeItem(TransitionProbability.class);
    }

    protected void executeHook(ITime time, Class<?> T, List<String> ids) {
        executeHook(time, T);
    }

    protected void executeHook(ITime time, Class<?> T) {
        try {

            // IProductionItem
            ValueSet<IProductionItem> aIPI = new ValueSet<IProductionItem>();
            aIPI.addAll(dm.retrieveAll(IProductionItem.class));
            outputs.put(IProductionItem.class, aIPI);

            // Experiment
            ValueSet<Experiment> aExperiment = new ValueSet<Experiment>();
            Experiment theExperiment = dm.retrieve(Experiment.class, getExperimentID());

            // Clear old values
            if (isDeleteOldResults()) {
                Set<IIndicatorValue> indvals = theExperiment.getIndicatorValues();
                //hard delete of indicator values from tables:
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueCrop.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueFarm.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueActivity.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueActivityGroupCountry.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueActivityGroupCountryAggregate.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueActivityGroupNUTSRegion.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueBetweenCountryAggregates.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueCountry.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueCountryAggregate.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueFarmAgriEnvironmentalZone.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueInputGroupCountry.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueInputGroupNUTSRegion.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueInputGroupCountryAggregate.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueProductGroupCountry.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueProductGroupCountryAggregate.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueProductGroupNUTSRegion.class);
                deleteIndicatorValuesFromTable(theExperiment, IndicatorValueNUTSRegion.class);


                theExperiment.getIndicatorValues().removeAll(indvals);
                Set<CutfactorSubsidies> cutFacts = theExperiment.getPolicyAssessment().getCutFactorSubsidies();
                theExperiment.getPolicyAssessment().getCutFactorSubsidies().removeAll(cutFacts);
                Set<EquilibriumPrice> equiPrices = theExperiment.getPolicyAssessment().getEquilibriumPrices();
                theExperiment.getPolicyAssessment().getEquilibriumPrices().removeAll(equiPrices);
                Set<OptimalFarmBehaviour> optfarms = theExperiment.getPolicyAssessment().getFssimFarmIndicators();
                theExperiment.getPolicyAssessment().getFssimFarmIndicators().removeAll(optfarms);
                Set<ProductionActivityPerFSSIMFarm> prodCoefs = theExperiment.getBiophysicalSimulation().getCalculatedProductionCoefficients();
                theExperiment.getBiophysicalSimulation().getCalculatedProductionCoefficients().removeAll(prodCoefs);
                Set<PriceElasticity> priceElas = theExperiment.getPolicyAssessment().getPriceElasticities();
                theExperiment.getPolicyAssessment().getPriceElasticities().removeAll(priceElas);
                dm.update(theExperiment);

                sendInformativeEvent(String.format("Existing results deleted for experiment with id = '%s ' and title '%s'; new ones can be added", theExperiment.getId(), theExperiment.getTitle()));
                setDeleteOldResults(false);
            }
            aExperiment.add(theExperiment);
            if (aExperiment.isEmpty()) {
                throw new SeamException(String.format(OUTPUT_NOT_LOADED, Experiment.class));
            }
            outputs.put(Experiment.class, aExperiment);
            // Nuts regions
            ValueSet<IRegion> aIregion = new ValueSet<IRegion>();
            aIregion.addAll(dm.retrieveAll(IRegion.class));
            if (aIregion.isEmpty()) {
                throw new SeamException(String.format(OUTPUT_NOT_LOADED, IRegion.class));
            }
            outputs.put(IRegion.class, aIregion);


            // Activity group
            ValueSet<ActivityGroup> valueSetActivityGroup = new ValueSet<ActivityGroup>();
            valueSetActivityGroup.addAll(dm.retrieveAll(ActivityGroup.class));
            if (valueSetActivityGroup.isEmpty()) {
                throw new SeamException(String.format(OUTPUT_NOT_LOADED, ActivityGroup.class));
            }
            outputs.put(ActivityGroup.class, valueSetActivityGroup);

            // premium group
            ValueSet<PremiumGroup> valueSetIPremiumGroup = new ValueSet<PremiumGroup>();
            valueSetIPremiumGroup.addAll(dm.retrieveAll(PremiumGroup.class));
            if (valueSetIPremiumGroup.isEmpty()) {
                throw new SeamException(String.format(OUTPUT_NOT_LOADED, PremiumGroup.class));
            }
            outputs.put(PremiumGroup.class, valueSetIPremiumGroup);

            //Product groups
            ValueSet<ProductGroup> valueSetProductGroup = new ValueSet<ProductGroup>();
            valueSetProductGroup.addAll(dm.retrieveAll(ProductGroup.class));
            if (valueSetProductGroup.isEmpty()) {
                throw new SeamException(String.format(OUTPUT_NOT_LOADED, ProductGroup.class));
            }
            outputs.put(ProductGroup.class, valueSetProductGroup);

            //Country aggregates
            ValueSet<CountryAggregate> aCountryAggregate = new ValueSet<CountryAggregate>();
            aCountryAggregate.addAll(dm.retrieveAll(CountryAggregate.class));
            if (aCountryAggregate.isEmpty()) {
                throw new SeamException(String.format(OUTPUT_NOT_LOADED, CountryAggregate.class));
            }
            outputs.put(CountryAggregate.class, aCountryAggregate);

            //GrassFeeds
            ValueSet<IFeedStuff> valueSetGrassFeeds = new ValueSet<IFeedStuff>();
            valueSetGrassFeeds.addAll(dm.retrieveAll(IFeedStuff.class));
            if (valueSetGrassFeeds.isEmpty()) {
                throw new SeamException(String.format(OUTPUT_NOT_LOADED, IFeedStuff.class));
            }
            outputs.put(IFeedStuff.class, valueSetGrassFeeds);

            //SimpleCropGroup
            ValueSet<SimpleCropGroup> valueSetSimpleCropGroup = new ValueSet<SimpleCropGroup>();
            valueSetSimpleCropGroup.addAll(dm.retrieveAll(SimpleCropGroup.class));
            if (valueSetSimpleCropGroup.isEmpty()) {
                throw new SeamException(String.format(OUTPUT_NOT_LOADED, SimpleCropGroup.class));
            }
            outputs.put(SimpleCropGroup.class, valueSetSimpleCropGroup);

            //Product type
            ValueSet<ProductType> valueSetProductType = new ValueSet<ProductType>();
            valueSetProductType.addAll(dm.retrieveAll(ProductType.class));
            if (valueSetProductType.isEmpty()) {
                throw new SeamException(String.format(OUTPUT_NOT_LOADED, ProductType.class));
            }
            outputs.put(ProductType.class, valueSetProductType);

            // Country
            ValueSet<Country> valueSetCountry = new ValueSet<Country>();
            valueSetCountry.addAll(dm.retrieveAll(Country.class));
            if (valueSetCountry.isEmpty()) {
                throw new SeamException(String.format(OUTPUT_NOT_LOADED, Country.class));
            }
            outputs.put(Country.class, valueSetCountry);

            // CropProduct
            ValueSet<CropProduct> valueSetCropproduct = new ValueSet<CropProduct>();
            valueSetCropproduct.addAll(dm.retrieveAll(CropProduct.class));
//			valueSetCropproduct.add(dm.retrieve(CropProduct.class, 1505l));
//			valueSetCropproduct.add(dm.retrieve(CropProduct.class, 1506l));
//			valueSetCropproduct.add(dm.retrieve(CropProduct.class, 1507l));
//			valueSetCropproduct.add(dm.retrieve(CropProduct.class, 1604l));
            if (valueSetCropproduct.isEmpty()) {
                throw new SeamException(String.format(OUTPUT_NOT_LOADED, CropProduct.class));
            }
            outputs.put(CropProduct.class, valueSetCropproduct);

            // EndorsedIndicator
            ValueSet<IIndicator> aEndorsedIndicator = new ValueSet<IIndicator>();
            aEndorsedIndicator.addAll(dm.retrieveAll(IIndicator.class));
            if (aEndorsedIndicator.isEmpty()) {
                throw new SeamException(String.format(OUTPUT_NOT_LOADED, IIndicator.class));
            }
            outputs.put(IIndicator.class, aEndorsedIndicator);


            ValueSet<TransitionProbability> aTransitionProbability = new ValueSet<TransitionProbability>();
            aTransitionProbability.addAll(dm.retrieveAll(TransitionProbability.class));
            if (aTransitionProbability.isEmpty()) {
                throw new SeamException(String.format(OUTPUT_NOT_LOADED, ActivityGroup.class));
            }
            outputs.put(TransitionProbability.class, aTransitionProbability);


        } catch (RuntimeException e) {
            SeamException seamException = new SeamException(e);
            logger.error(e.getMessage(), e);
            throw seamException;
        }


    }

    private void deleteIndicatorValuesFromTable(Experiment theExperiment, Class<? extends IIndicatorValue> indicatorValueType) {
        int result = dm.executeDeleteQuery("Delete from " + indicatorValueType.getSimpleName() + " c where c.Experiment = " + theExperiment.getId());
        if (result < 1) {
            logger.error("Could not delete rows from table " + indicatorValueType.getSimpleName() + " for experiment with id = " + theExperiment.getId());
        }
    }

    @Override
    public String getComponentID() {
        return String.format("DM_V%d", DM_COMPONENT_VERSION);
    }

    @Override
    public String getModelID() {
        return getComponentID();
    }

    @Override
    public void initializeHook(IArgument[] properties) {
        super.initializeHook(properties);
        for (IArgument arg : properties) {
            if (arg.getKey().equals(OVERRIDE_RESULTS.getKey())) {
                if (arg.getValue().equalsIgnoreCase("TRUE")) {
                    setDeleteOldResults(true);
                } else {
                    setDeleteOldResults(false);
                }
            }
        }
    }

    private boolean isDeleteOldResults() {
        return deleteOldResults;
    }

    private void setDeleteOldResults(boolean deleteOldResults) {
        this.deleteOldResults = deleteOldResults;
	}

}
