/*
 * seamframe: SeamChain.java
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

import java.util.ArrayList;
import java.util.List;

import nl.alterra.openmi.sdk.backbone.Argument;
import nl.alterra.openmi.sdk.backbone.Arguments;
import nl.alterra.openmi.sdk.backbone.LinkableComponent;
import nl.alterra.openmi.sdk.configuration.Composition;
import nl.alterra.openmi.sdk.configuration.SystemDeployer;
import nl.alterra.openmi.sdk.configuration.Trigger;
import nl.alterra.openmi.sdk.extensions.IArguments;

import org.apache.log4j.Logger;
import org.openmi.standard.IEvent;
import org.openmi.standard.ILinkableComponent;
import org.openmi.standard.IListener;
import org.openmi.standard.IEvent.EventType;


/**
 * Class for building Seamless Model Chains. It can either be used as a base
 * class, or used in a composition. It has convenience methods for adding
 * linkable components, triggers and links, as well as for initialising and
 * running the model chain (OpenMI composition).
 *
 * @author David Huber, Ioannis N. Athanasiadis, Rob Knapen
 */
public class SeamChain implements IListener {

    /**
     * OpenMI events of interest.
     */
    final EventType[] ets = new EventType[]
            {
                    EventType.DataChanged, EventType.GlobalProgress,
                    EventType.Informative, EventType.Other, EventType.SourceAfterGetValuesCall,
                    EventType.SourceBeforeGetValuesReturn, EventType.TargetAfterGetValuesReturn,
                    EventType.Warning, EventType.TimeStepProgress,
            };

    /**
     * The OpenMI composition used internally.
     */
    protected Composition composition;


    /**
     * Logger for the instance.
     */
    protected final Logger logger = Logger.getLogger(this.getClass());


    /**
     * Creates an instance.
     *
     * @param id of the model chain
     */
    public SeamChain(String id) {
        composition = new Composition(id);
    }


    /**
     * Returns the OpenMI composition used for the model chain.
     *
     * @return Composition
     */
    public Composition getComposition() {
        return composition;
    }


    /**
     * Adds the specified component to the model chain.
     *
     * @param component to be added
     */
    public void addComponent(SeamLinkableComponent component) {
        composition.addComponent(component);
    }


    /**
     * Adds the specified trigger to the model chain.
     *
     * @param trigger to be added
     */
    public void addComponent(SeamTrigger trigger) {
        composition.addComponent(trigger);
    }


    /**
     * Creates a link between the specified source (output of data) and target
     * (input of the data) components, for the exchange of data of the class
     * type specified. The components must have been added to the model chain
     * and have an input/output exchange item for the specified class type.
     *
     * @param source linkable component to link
     * @param target linkable component to link
     * @param type   of class to be exchange over the link
     */
    public void createLink(SeamLinkableComponent source, SeamLinkableComponent target, Class<?> type) {
        composition.createLink(source, source.getOutputExchangeItem(type.getSimpleName()),
                target, target.getInputExchangeItem(type.getSimpleName()));
    }


    /**
     * Creates a link between the specified source (output of data) component
     * and target (input of the data) trigger, for the exchange of data of
     * the class type specified. The component and trigger must have been
     * added to the model chain and have an input/output exchange item for
     * the specified class type.
     *
     * @param source linkable component to link
     * @param target trigger to link
     * @param type   of class to be exchange over the link
     */
    public void createLink(SeamLinkableComponent source, SeamTrigger target, Class<?> type) {
        composition.createLink(source, source.getOutputExchangeItem(type.getSimpleName()),
                target, target.getInputExchangeItem(type.getSimpleName()));
    }


    /**
     * The default initialisation passes the ID of the Experiment that the model
     * chain is being prepared for to all the components. It is passed as a
     * key-value argument (key = Experiment.class name, value = id).
     *
     * @param experimentID Experiment ID to initialise the model chain with
     */
    public void initialize(Long experimentID) {
        initialize(experimentID, null);
    }


    /**
     * Initialises the model chain for the specified Experiment and the list
     * of additional initialisation arguments (may be null or empty). A key-
     * value argument (key = Experiment.class name, value = experiment id) is
     * created and combined with the additional arguments, which is then
     * passed to all models in the chain in the initialize() call.
     *
     * @param experimentID Experiment ID to initialise the model chain with
     * @param args         List of additional Arguments to pass to the linkable components
     */
    public void initialize(Long experimentID, List<Argument> args) {

        IArguments arguments = new Arguments();

        if (experimentID != null) {
            Argument arg = new Argument(SeamLinkableComponent.EXPERIMENT, experimentID.toString(), true);
            arguments.add(arg);
        }

        arguments.addAll(args);

        composition.initialize(arguments);
    }


    /**
     * Executes the model chain. By default this will run a full calculation by
     * 'pulling' all triggers in the composition. The composition must be
     * initialised first (by calling the initialise() method) to set the e.g.
     * Experiment the models should calculate for.
     */
    public void execute() {
        execute(composition.getTriggers());
    }


    /**
     * Executes the model chain for the trigger with the specified ID. The
     * trigger will be "pulled", which starts the calculation of the models
     * connected to it. The composition must be initialised first (by calling
     * the initialise() method) to set the e.g. Experiment the models should
     * calculate for.
     *
     * @param triggerId the trigger id
     */
    public void execute(String triggerId) {
        ArrayList<Trigger> triggers = new ArrayList<Trigger>();
        for (Trigger t : composition.getTriggers()) {
            if (t.getID().equalsIgnoreCase(triggerId)) {
                triggers.add(t);
                break;
            }
        }

        if (triggers.size() == 0)
            logger.error("No trigger with ID '" + triggerId + "' in the composition, can not execute it!");
        else
            execute(triggers);
    }


    /**
     * Executes the model chain for the specified triggers. All triggers in
     * the list will be "pulled", which starts the calculation of the models
     * connected to them. The composition must be initialised first (by calling
     * the initialise() method) to set the e.g. Experiment the models should
     * calculate for.
     *
     * @param triggers the triggers
     */
    public void execute(ArrayList<Trigger> triggers) {
        SystemDeployer deployer;
        deployer = new SystemDeployer("deployer");
        deployer.setComposition(composition);

        deployer.setTriggers(triggers);
        deployer.setBlocking(true);

        subscribeListeners();
        deployer.start();
        unsubscribeListeners();
    }


    /**
     * Subscribes the instance as listener to the OpenMI composition and all
     * the components in it. This is called before the model chain is run.
     */
    protected void subscribeListeners() {
        composition.subscribe(this, ets);

        for (int i = 0; i < composition.getLinkableComponents().length; i++) {
            logger.info("Subscribing: " + composition.getLinkableComponents()[i]);
            ((LinkableComponent) composition.getLinkableComponents()[i]).subscribe(this, ets);
        }
    }


    /**
     * Unsubscribes the instance as listener to the OpenMI composition and
     * all the components in it. This is after before the model chain has
     * been run.
     */
    protected void unsubscribeListeners() {
        composition.unSubscribe(this, ets);

        for (int i = 0; i < composition.getLinkableComponents().length; i++) {
            logger.info("Unsubscribing: " + composition.getLinkableComponents()[i]);
            ((LinkableComponent) composition.getLinkableComponents()[i]).unSubscribe(this, ets);
        }
    }


    /* (non-Javadoc)
      * @see org.openmi.standard.IListener#getAcceptedEventType(int)
      */
    public EventType getAcceptedEventType(int i) {
        return ets[i];
    }


    /* (non-Javadoc)
      * @see org.openmi.standard.IListener#getAcceptedEventTypeCount()
      */
    public int getAcceptedEventTypeCount() {
        return ets.length;
    }

    /**
     * Logg event.
     *
     * @param event            the event
     * @param eventDescription the event description
     */
    private void loggEvent(IEvent event, String eventDescription) {
        if (event.getType() == EventType.Warning) {
            logger.warn(eventDescription);
        } else {
            logger.info(eventDescription);
        }
    }

    /**
     * Sends an abort signal to the components in the chain.
     */
    public void abort() {
//      This below with the events will not work, as the events are never received by the components, 
//		but only by the chain.  
//		Event event = new Event();
//		event.setType(IEvent.EventType.Other);
//		event.setDescription("ABORT");
        ILinkableComponent[] linkableComponents = this.getComposition().getLinkableComponents();
        for (ILinkableComponent linkableComponent : linkableComponents) {
            linkableComponent.finish();
        }
        throw new SeamException("Execution of experiment aborted");
    }

    /* (non-Javadoc)
      * @see org.openmi.standard.IListener#onEvent(org.openmi.standard.IEvent)
      */
    public void onEvent(IEvent event) {
        if (event.getSender() != null) {
            String eventDescription = "Event: " + event.getSender().toString() + " - " + event.getDescription();
            loggEvent(event, eventDescription);
        } else {
            String eventDescription = "Event: " + " - " + event.getDescription();
            loggEvent(event, eventDescription);
        }

        if (event.getType().equals(EventType.Other) && event.getDescription().contains("ABORT")) {
            abort();
        }
    }
}
