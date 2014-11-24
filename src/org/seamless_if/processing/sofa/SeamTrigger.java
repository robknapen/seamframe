/*
 * seamframe: SeamTrigger.java
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

import java.util.Set;

import nl.alterra.openmi.sdk.backbone.Element;
import nl.alterra.openmi.sdk.backbone.ElementSet;
import nl.alterra.openmi.sdk.backbone.NullValueSet;
import nl.alterra.openmi.sdk.backbone.ValueSet;
import nl.alterra.openmi.sdk.configuration.Trigger;
import nl.alterra.openmi.sdk.extensions.IInputExchangeItemEx;
import nl.alterra.openmi.sdk.extensions.ILinkEx;

import org.openmi.standard.IElementSet;
import org.openmi.standard.IInputExchangeItem;
import org.openmi.standard.ITime;
import org.seamless_ip.ontologies.indi.IIndicator;
import org.seamless_ip.ontologies.indi.IIndicatorValue;
import org.seamless_ip.ontologies.seamproj.Problem;


/**
 * A Seamless Trigger Linkable Component for use in model chains.
 *
 * @author Rob Knapen, Benny Jonnsson
 */
public class SeamTrigger extends Trigger {
    private final static String ERROR_PROBLEM_INPUT_NOT_LINKED = "Trigger with id '%s' has no link for the Problem input exchange item!";
    private final static String ERROR_PROBLEM_INPUT_INVALID = "Trigger with id '%s' received invalid Problem input!";
    private final static String ERROR_INDICATORS_INPUT_NOT_LINKED = "Trigger with id '%s' has no link for the IndicatorValues input exchange item!";
    private final static String ERROR_INDICATOR_HAS_NO_MODEL = "Indicator with id '%s' and name '%s' has no reference to a providing Model!";


    /**
     * Creates an instance of a trigger with the specified ID.
     *
     * @param id of the trigger.
     */
    public SeamTrigger(String id) {
        super(id);
        registerInputExchangeItem(IIndicatorValue.class);
        registerInputExchangeItem(Problem.class);
    }


    @Override
    protected void pull(ITime time) {
        // update the element set of the IndicatorValue input
        Set<IIndicator> indicators = readIndicatorSelectionFromProblemInput();
        updateElementSetFromModelID(indicators);

        // retrieve the indicator values
        IInputExchangeItemEx ieiIndicators = getInputExchangeItem(IIndicatorValue.class.getSimpleName());
        ILinkEx link = findLinkForInputExchangeItem(ieiIndicators);

        // output <-- link.source --- link.target <-- input
        if (link != null) {
            lastCalculatedValues = link.getSourceComponent().getValues(time, link.getID());
        } else
            lastCalculatedValues = new NullValueSet();
    }


    /**
     * Gets the Problem data from the linked component and return the list of
     * selected indicators. Errors will result in a SeamException being thrown.
     *
     * @return Set<IIndicator> with the selected indicators
     */
    @SuppressWarnings("unchecked")
    private Set<IIndicator> readIndicatorSelectionFromProblemInput() {
        // find the input and link for the Problem data
        IInputExchangeItemEx ieiProblem = getInputExchangeItem(Problem.class
                .getSimpleName());
        ILinkEx link = findLinkForInputExchangeItem(ieiProblem);
        if (link == null)
            throw new SeamException(String.format(
                    ERROR_PROBLEM_INPUT_NOT_LINKED, getID()));

        // retrieve the Problem data from the linked component
        ValueSet result = (ValueSet) link.getSourceComponent().getValues(null,
                link.getID());

        if (result.size() == 1) {
            Object obj = result.getValue(0);
            if (obj instanceof Problem) {
                // if indeed we received a Problem, return the selection of
                // indicators
                Problem p = ((Problem) obj);
                return p.getIndicators();
            }
        }

        throw new SeamException(String.format(ERROR_PROBLEM_INPUT_INVALID,
                getID()));
    }


    /**
     * Updates the element set for the IndicatorValues input exchange item with
     * a new ID-based element set. This ID-based element set is created by
     * filtering down the passed set of indicators. For this the model ID of the
     * component linked to the IndicatorValue input is used and matched to the
     * model ID of each IIndicator. When the model ID's match (i.e. the
     * indicator is calculated by the model), an Element will be added to the
     * ElementSet with the ID of the indicator. Finally this ElementSet is
     * assigned to the IndicatorValue input exchange item.
     *
     * @param indicators Set<IIndicator> to be used to create the ElementSet
     */
    private void updateElementSetFromModelID(Set<IIndicator> indicators) {
        // output <-- link.source --- link.target <-- input

        // get the source model component through the input link for
        // IndicatorValues
        IInputExchangeItemEx ieiIndicators = getInputExchangeItem(IIndicatorValue.class
                .getSimpleName());
        ILinkEx link = findLinkForInputExchangeItem(ieiIndicators);
        if (link == null)
            throw new SeamException(String.format(
                    ERROR_INDICATORS_INPUT_NOT_LINKED, getID()));

        // get the source Model ID, which will be used to filter the list of
        // indicators
        String modelId = link.getSourceComponent().getModelID();

        // create an ID-based element set by filtering the selection on model ID
        ElementSet es = new ElementSet();
        es.setElementType(IElementSet.ElementType.IDBased);
        for (IIndicator indi : indicators) {
            if (indi.getModel() == null) {
                throw new SeamException(String.format(
                        ERROR_INDICATOR_HAS_NO_MODEL, indi.getId(), indi.getLabel_en()));
            } else {
                String indiModelId = indi.getModel().getName() + '_' + indi.getModel().getVersion();

                if (modelId.equalsIgnoreCase(indiModelId)) {
                    Element el = new Element();
                    el.setID(indi.getId().toString());
                    es.addElement(el);
                }
            }
        }

        // assign the element set to the IndicatorValue input exchange item
        ieiIndicators.setElementSet(es);
    }


    protected <I> void registerInputExchangeItem(Class<I> aClass) {
        SeamInputExchangeItem<I> item = new SeamInputExchangeItem<I>(this, aClass);
        inputExchangeItems.add(item);
    }


    /**
     * Override the getInputExchangeItem method for handling SeamExhangeItem
     * implementations.
     */
    @SuppressWarnings("unchecked")
    @Override
    public IInputExchangeItemEx getInputExchangeItem(String id) {
        for (IInputExchangeItem item : inputExchangeItems) {
            if (item instanceof SeamInputExchangeItem) {
                if (((SeamInputExchangeItem) item).getID().equals(id))
                    return (IInputExchangeItemEx) item;
            }
        }
        return super.getInputExchangeItem(id);
    }


    @Override
	public String validate() {
		return "";
	}

}
