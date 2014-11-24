/*
 * seamframe: SeamLinkableComponent.java
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.alterra.openmi.sdk.backbone.Argument;
import nl.alterra.openmi.sdk.backbone.Arguments;
import nl.alterra.openmi.sdk.backbone.Event;
import nl.alterra.openmi.sdk.backbone.LinkableComponent;
import nl.alterra.openmi.sdk.backbone.TimeStamp;
import nl.alterra.openmi.sdk.backbone.ValueSet;
import nl.alterra.openmi.sdk.extensions.ILinkEx;

import org.apache.log4j.Logger;
import org.openmi.standard.IArgument;
import org.openmi.standard.IElementSet;
import org.openmi.standard.IEvent;
import org.openmi.standard.IInputExchangeItem;
import org.openmi.standard.ILink;
import org.openmi.standard.IOutputExchangeItem;
import org.openmi.standard.ITime;
import org.openmi.standard.IValueSet;
import org.seamless_ip.core.utilities.filefunctions.Svn;
import org.seamless_ip.core.utilities.filefunctions.Zip;
import org.seamless_ip.ontologies.indi.IIndicatorValue;


/**
 * Seamless Linkable Component extends the OpenMI LinkableComponent.
 *
 * @author Rob Knapen, David Huber, Ioannis N. Athanasiadis, Benny Jonnsson
 */
public abstract class SeamLinkableComponent extends LinkableComponent {
    private static final String SVN_OK = "Experiment committed to %s.";

    private static final String SVN_ERROR = "Something went wrong when commiting zip file to repository %s";

    /**
     * String that defines the general part of the message of the
     * event sent when a model has completed calucating.
     */
    private static String GENERAL_MODEL_COMPLETED_DESCRIPTION = "Completed calculation for model:";

    /**
     * String that defines the attribute for the modelID value
     */
    public static final String MODEL_ID_ATTRIBUTE = "modelId";

    public static String VERSION_PREFIX = "_V";


    private String svnUrl;
    private String svnUser;
    private String svnPasswd;
    //	boolean specifying whether zip files should be made or not
    private boolean makeZip;

    public static final Argument SVNURL = new Argument("SVNURL", null, false, "Url to svn repository");
    public static final Argument SVNUSERNAME = new Argument("SVNUSERNAME", null, false, "svn repository username");
    public static final Argument SVNPASSWD = new Argument("SVNPASSWD", null, false, "svn repository password");
    public static final Argument ZIPMODELIO = new Argument("ZIPMODELIO", "", false, "Boolean: if true makes zip files, one per farmtype");

    protected final Logger logger = Logger.getLogger(this.getClass());

    /**
     * Name of generated Bean class for the Experiment.
     */
    public static final String EXPERIMENT = "org.seamless_ip.ontologies.seamproj.Experiment";

    /**
     * Status enumeration, used internally to track the state of the linkable
     * component.
     */
    private enum Status {
        /**
         * The component is created but not initialised.
         */
        New,

        /**
         * The component is created and initialised, but not computed.
         */
        Initialized,

        /**
         * Component computations have finished.
         */
        Computed,

        /**
         * Component inputs have changed, and must be recomputed.
         */
        Invalidated
    }

    /**
     * Current Status of the linkable component. Used to track when e.g. inputs
     * have changed and calculation of the model is needed.
     */
    private Status status;

    /**
     * Access to DomainManager singleton. It is initialised in the class
     * constructor and can be used to retrieve information from the database and
     * to store data.
     */
    // protected IDomainManager components.dm;

    /**
     * Cache of last used input values. Before the component starts the
     * calculation it retrieves all its required inputs. These values are cached
     * in the inputs hash map.
     */
    @SuppressWarnings("unchecked")
    protected Map<Class, ValueSet> inputs = new HashMap<Class, ValueSet>();

    /**
     * Cache of last returned output values. The calculations by the component
     * produce its outputs values. The last results are stored in the outputs
     * hash map.
     */
    @SuppressWarnings("unchecked")
    protected Map<Class, ValueSet> outputs = new HashMap<Class, ValueSet>();

    /**
     * Deprecated, please see Trac ticket:265.
     */
//	@Deprecated
//	protected Experiment experiment;

    private Long experimentID;

    /**
     * This is the main constructor of a Seamless Linkable Component. In the
     * constructor you should register the model interface using the
     * {@code registerInputExchangeItem} and {@code registerOutputExchangeItem}
     * methods. Example: {@code registerInputExchangeItem(Crop.class)}. By
     * default the constructor will initialise the DomainManager instance for
     * use by the component.
     *
     * @param ID the component name
     */
    public SeamLinkableComponent(String ID) {
        super(ID);
        this.status = Status.New;
        Arguments arguments = new Arguments();
        arguments.add(SVNURL);
        arguments.add(SVNUSERNAME);
        arguments.add(SVNPASSWD);
    }


    /**
     * This is the main constructor of a Seamless Linkable Component. In the
     * constructor you should register the model interface using the
     * {@code registerInputExchangeItem} and {@code registerOutputExchangeItem}
     * methods. Example: {@code registerInputExchangeItem(Crop.class)}
     *
     * @param ID
     *            the component ID
     * @param useDomainManager
     *            True if the component uses the domain manager
     */
//	public SeamLinkableComponent(String ID, boolean useDomainManager)
//	{
//		super(ID);
//		this.status = Status.New;
//		if (useDomainManager)
//			components.dm = DomainManager.initialize();
//	}


    /**
     * Returns the ID of the OpenMI wrapper component. All SEAMLESS linkable
     * components are required to implement this method and return as ID the
     * name of the wrapper and a sequential edition number, separated by an
     * underscore. E.g. SEAMCAP_V1.
     *
     * @return String to identify the wrapper component.
     */
    @Override
    public abstract String getComponentID();


    /**
     * Returns the ID of the wrapped model. All SEAMLESS linkable components are
     * required to implement this method and return as ID the name of the
     * wrapped model and a sequential edition number, separated by an
     * underscore. E.g. CAPRI_V1.
     *
     * @return String to identify the wrapped model.
     */
    @Override
    public abstract String getModelID();


    /**
     * Returns a description of the OpenMI compliant wrapper component. By
     * default it returns the component ID, but should be overwritten to return
     * some information about the component, its version, who created it, and so
     * on.
     *
     * @return String with component description.
     */
    @Override
    public String getComponentDescription() {
        return getComponentID();
    }


    /**
     * Returns a description of the wrapped model. By default it returns the
     * model ID, but should be overwritten to return some information about the
     * model, its version, who created it, and so on.
     *
     * @return String with description of the wrapped model.
     */
    @Override
    public String getModelDescription() {
        return getModelID();
    }


    /**
     * Returns the ID of the Experiment the instance was initialised with. The
     * model is assumed to perform calculations for this experiment.
     *
     * @return Experiment ID
     */
    public Long getExperimentID() {
        return experimentID;
    }


    /**
     * Creates a new input exchange item based on the specified type and
     * registers it as part of the linkable component. This also creates an
     * entry in the cache for the input values.
     *
     * @param aClass Class<I> Ontology aware bean class to be exchanged
     * @return Cached ValueSet created for the exchange item
     */
    @SuppressWarnings("unchecked")
    protected <I> ValueSet registerInputExchangeItem(Class<I> aClass) {
        SeamInputExchangeItem<I> item = new SeamInputExchangeItem<I>(this, aClass);
        inputExchangeItems.add(item);
        ValueSet<I> values = new ValueSet<I>();
        inputs.put(aClass, values);
        return values;
    }


    /**
     * Creates a new output exchange item based on the specified type and
     * registers it as part of the linkable component. This also creates an
     * entry in the cache for the output values.
     *
     * @param aClass Class<I> Ontology aware bean class to be exchanged
     * @return Cached ValueSet created for the exchange item
     */
    @SuppressWarnings("unchecked")
    protected <I> ValueSet registerOutputExchangeItem(Class<I> aClass) {
        SeamOutputExchangeItem<I> item = new SeamOutputExchangeItem<I>(this, aClass);
        outputExchangeItems.add(item);
        ValueSet<I> values = new ValueSet<I>();
        outputs.put(aClass, values);
        return values;
    }


    @SuppressWarnings("unchecked")
    @Override
    public IValueSet getValuesHook(ITime time, ILink link) {
        logger.debug(getID().concat(" getValues called through link ".concat(link.getID())));

        // check all inputs for the component
        for (IInputExchangeItem item : inputExchangeItems) {
            if (item instanceof SeamInputExchangeItem) {
                // find the link for the input
                SeamInputExchangeItem seamitem = (SeamInputExchangeItem) item;
                Class type = seamitem.getOntologyType();
                ILink ilink = this.findLinkForInputExchangeItem(item);

                if (ilink == null) {
                    logger.error(getID() + " missing link for input " + item.toString());
                } else {
                    // get the cached (previous input) values
                    ValueSet values = inputs.get(type);

                    // get the new input values through the link
                    ValueSet result = (ValueSet) ilink.getSourceComponent().getValues(time, ilink.getID());

                    if (result == null) {
                        logger.warn(getID() + " input " + item.toString() + " received NULL, replaced by empty ValueSet!");
                        result = new ValueSet();
                    }

                    // Check if anything has changed
                    // FIXME: this doesn't work due to problems with experiment component-fssim/capri component:
                    // empty data statements are received in CAPRI/FSSIM component, although they are full when
                    // leaving the experiment component (weird stuff). With the lines below, this doesn't work:
                    // it invalidates the model runs as the inputs and outputs of the models somehow are not
                    // equal, e.g. not referring to the same object.
                    if (!values.equals(result)) {
                        inputs.remove(type);
                        inputs.put(type, result);
                        this.status = Status.Invalidated;
                    }
                }
            }
        }

        // get the type of output requested
        ILinkEx olink = (ILinkEx) link;
        SeamOutputExchangeItem seamout = (SeamOutputExchangeItem) olink.getSourceExchangeItem();
        Class type = seamout.getOntologyType();

        // when inputs have changed or output is not available, run the model again
        if ((this.status != Status.Computed) || (outputs.get(type).size() == 0)) {
            // clear all cached outputs
            for (ValueSet v : outputs.values())
                v.clear();

            // perform the calculations
            execute(time, link);
        }

        // return the values that were requested
        logger.info(getID() + " returns " + outputs.get(type).size() + " values");
        return outputs.get(type);
    }


    /**
     * This is the method where the model is executed. The inputs are available
     * in the {@code inputs} Map. The outputs should be stored in the
     * {@code outputs Map}.
     */
    @SuppressWarnings("unchecked")
    private void execute(ITime time, ILink link) throws SeamException {
        // output <-- link.source --- link.target <-- input

        // check if the component is the source of the link
        if (link.getSourceComponent() != this)
            throw new SeamException("Can not execute model for unattached link!");

        // check if the used link is smart enough
        if (!(link instanceof ILinkEx))
            throw new SeamException("Can not handle links that do not implement ILinkV2!");

        // find the output that is being requested
        IOutputExchangeItem outputEI = ((ILinkEx) link).getSourceExchangeItem();
        if (!(outputEI instanceof SeamOutputExchangeItem))
            throw new SeamException("Component has a non Seamless compliant output exchange item!");

        // find the ontology aware bean class type that is being requested
        Class type = ((SeamOutputExchangeItem) outputEI).getOntologyType();

        // create set of id's from the target element set, which must be ID-based
        IElementSet es = link.getTargetElementSet();
        ArrayList<String> ids = createIdListFromElementSet(es);

        if (ids.size() > 0) {
            String msg = "Running model calculation for ID-based selection of " + ids.size() + " instance(s) of " + type.getSimpleName();
            sendEvent(new Event(time, Event.EventType.Informative, this, msg));

            executeHook(time, type, ids);

            // filter output IIndicatorValue based on selected IDs
            // (this is fail-safe when model does not do it)
            if (type == IIndicatorValue.class)
                filterIndicatorValueSetForIdList(outputs.get(type), ids);

            msg = "Completed calculation for " + ids.size() + " instance(s) of " + type.getSimpleName();
            sendEvent(new Event(time, Event.EventType.Informative, this, msg));
        } else {
            String msg = "Running calculation for type " + type.getSimpleName();
            sendEvent(new Event(time, Event.EventType.Informative, this, msg));

            executeHook(time, type);

            msg = "Completed calculation for type " + type.getSimpleName();
            sendEvent(new Event(time, Event.EventType.Informative, this, msg));
        }
        sendCompletedModelEvent();
        this.status = Status.Computed;
    }


    /**
     * Creates a list of IDs from the specified ElementSet, which is expected to
     * be ID-based. When it is not ID-based (ElementType.IDBased) then an empty
     * list will be returned.
     *
     * @param elementSet to get IDs from
     * @return List of IDs, can be empty
     */
    private ArrayList<String> createIdListFromElementSet(IElementSet elementSet) {
        ArrayList<String> ids = new ArrayList<String>();
        if ((elementSet != null) && (elementSet.getElementType().equals(IElementSet.ElementType.IDBased))) {
            for (int i = 0; i < elementSet.getElementCount(); i++) {
                ids.add(elementSet.getElementID(i));
            }
        }

        return ids;
    }


    /**
     * Processes the specified ValueSet with IIndicatorValue instances and
     * removes all items from it that have an ID that is not in the given list
     * of IDs.
     *
     * @param valueSet to be processed
     * @param ids      of items to keep
     * @return filtered value set
     */
    private ValueSet<IIndicatorValue> filterIndicatorValueSetForIdList(
            ValueSet<IIndicatorValue> valueSet, ArrayList<String> ids) {
        if ((valueSet != null) && (valueSet.size() > 0)) {
            ArrayList<Object> removables = new ArrayList<Object>();

            // find the things to be removed
            for (int i = 0; i < valueSet.size(); i++) {
                if (!ids.contains(valueSet.getValue(i).getIndicator().getId().toString()))
                    removables.add(valueSet.getValue(i));
            }

            // remove them from the value set
            for (Object obj : removables)
                valueSet.remove(obj);
        }

        return valueSet;
    }


    /**
     * Model computation hook to be implemented by the class. When it is called
     * the inputs Map is populated with the current values to be used for the
     * calculations (and at least one input has been changed). The model should
     * calculate for the specified time and ontology class. Or, when not
     * optimised, calculate everything. Outputs should be stored in the outputs
     * Map.
     *
     * @param time for which model calculation is requested
     * @param aClass    Ontology class for which model calculation is requested
     * @throws SeamException when calculation is not successful
     */
    protected abstract void executeHook(ITime time, Class<?> aClass) throws SeamException;


    /**
     * Model computation hook to be implemented by the class. When it is called
     * the inputs Map is populated with the current values to be used for the
     * calculations (and at least one input has been changed). The model should
     * calculate for the specified time and ontology class, and also only for
     * the instances with one of the specified ids. Or, when not optimised,
     * calculate everything. Outputs should be stored in the outputs Map.
     *
     * @param time for which model calculation is requested
     * @param aClass    Ontology class for which model calculation is requested
     * @param ids  selecting ontology class instances that are to be calculated
     * @throws SeamException when calculation is not successful
     */
    protected abstract void executeHook(ITime time, Class<?> aClass, List<String> ids)
            throws SeamException;


    /**
     * Called from the initialize() method of the linkable component.
     */
    @Override
    public void initializeHook(IArgument[] properties) {
        for (IArgument arg : properties) {
            if (arg.getKey().equals(EXPERIMENT)) {
                experimentID = Long.valueOf(arg.getValue());
//				if (components.dm != null) {
//					experiment = components.dm.retrieve(Experiment.class, experimentID);
//				}
            }
            if (SVNURL.equalsKey(arg.getKey())) {
                setSvnUrl(arg.getValue());
                // SVNURL = arg;
                continue;
            }
            if (SVNUSERNAME.equalsKey(arg.getKey())) {
                setSvnUser(arg.getValue());
                // SVNUSERNAME = arg;
                continue;
            }
            if (SVNPASSWD.equalsKey(arg.getKey())) {
                setSvnPasswd(arg.getValue());
                // SVNPASSWD = arg;
                continue;
            }
            if (ZIPMODELIO.equalsKey(arg.getKey())) {
                if (arg.getValue().equalsIgnoreCase("TRUE")) {
                    setMakeZip(true);
                } else {
                    setMakeZip(false);
                }
            }

        }

//		if (experiment == null) {
//			experiment = new Experiment();
//			experiment.setId(experimentID);
//		}

        this.status = Status.Initialized;
    }

    /**
     * sends an event to indicate the completion of calculation
     * a the currently completed model.
     * <p/>
     * <p>The attribute "modelId" has the value of the id of the
     * last completed model.</p>
     */
    private void sendCompletedModelEvent() {
        String description;
        Event completedModelEvent;
        String attribute;
        String value;
        String modelDescription;
        String modelID;
        String[] modelIDParts;
        Event.EventType type;

        modelDescription = getModelDescription();
        description = GENERAL_MODEL_COMPLETED_DESCRIPTION + modelDescription;

        type = Event.EventType.GlobalProgress;

        attribute = MODEL_ID_ATTRIBUTE;

        modelID = getModelID();

        // split the ID into a name and version part
        // e.g. APES_V1 --> APES , V1
        modelIDParts = modelID.split("_");
        if (modelIDParts.length > 2) {
            logger.warn("ID " + modelID + " has not been formed according to definition");
            return;
        }
        value = modelIDParts[0];

        completedModelEvent = new Event();
        completedModelEvent.setType(type);
        completedModelEvent.setSender(this);
        completedModelEvent.setAttribute(attribute, value);
        completedModelEvent.setDescription(description);

        sendEvent(completedModelEvent);
    }


    /**
     * indicates progress as % of global time horizon
     *
     * @param eventText Message to include in the event
     */
    protected void sendGlobalEvent(final String eventText) {
        sendEvent(eventText, IEvent.EventType.GlobalProgress);
    }

    /**
     * general information message
     *
     * @param eventText Message to include in the event
     */
    protected void sendInformativeEvent(final String eventText) {
        sendEvent(eventText, IEvent.EventType.Informative);
    }

    /**
     * indicates progress as % of time step
     *
     * @param eventText Message to include in the event
     */
    protected void sendTimeStepProgressEvent(final String eventText) {
        sendEvent(eventText, IEvent.EventType.TimeStepProgress);
    }

    /**
     * indicates changes of data a component
     *
     * @param eventText Message to include in the event
     */
    protected void sendDataChangedEvent(final String eventText) {
        sendEvent(eventText, IEvent.EventType.DataChanged);
    }

    /**
     * general warning message
     *
     * @param eventText Message to include in the event
     */
    protected void sendWarningEvent(final String eventText) {
        sendEvent(eventText, IEvent.EventType.Warning);
    }

    /**
     * Adding Experiment id and sends the event
     *
     * @param eventText text to be sent
     * @param eventType type of event
     */
    private void sendEvent(final String eventText,
                           final IEvent.EventType eventType) {
        String eText;
        eText = String.format("%s (ExperimentId:%d)", eventText,
                getExperimentID());
        sendEvent(new Event(new TimeStamp(), eventType, this, eText));

    }


    /**
     * 1. Zip's the experiment folder
     * 2. Deletes the folders
     * 3. Commit zip to svn (if svn repository exists)
     * 4. Delete the zip file
     *
     * @param modelName
     * @param inputOutputPath
     * @return the name of the zip file
     */
    protected String zipExperiment(final String modelName, final String inputOutputPath) { // NOPMD by benny on 2008-08-28 17:51
        sendInformativeEvent("Start zip experiment");
        String parentDir;
        parentDir = new File(inputOutputPath).getParent(); // NOPMD by benny on 2008-08-28 17:52
        String zipFile;
        for (int version = 1; ; version++) { // NOPMD by benny on 2008-08-28 17:52
            zipFile = String.format("%s\\%s_e%d_r%d.zip", parentDir, modelName,
                    getExperimentID(), version);
            if (!new File(zipFile).exists()) { // NOPMD by benny on 2008-08-28 17:47
                break;
            }
        }

        try {
            Zip.zipDirectory(inputOutputPath, zipFile, String.format(
                    "model result zipped %s", new Date().toString()));
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            throw new SeamException(e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new SeamException(e);
        }
        sendInformativeEvent("model zip experiment zipped");
        Svn svn;
        if (getSvnUrl() != null) {
            try {
                svn = new Svn(getSvnUrl(), getSvnUser(), getSvnPasswd());
                //"http://trac.seamless-ip.org/experimentZip", null,	null);
                svn.addUpdateFile(getExperimentID().toString(), zipFile);
                sendInformativeEvent(String.format(SVN_OK, getSvnUrl()));
            } catch (Exception e) {
                sendWarningEvent(String.format(SVN_ERROR, getSvnUrl()));
            }
        }
        // Delete file
        if (!new File(zipFile).delete())
            logger.warn("Could not delete zipfile: " + zipFile);

        return zipFile;

    }


    public void setSvnUrl(String svnUrl) {
        this.svnUrl = svnUrl;
    }


    public String getSvnUrl() {
        return svnUrl;
    }


    public void setSvnUser(String svnUser) {
        this.svnUser = svnUser;
    }


    public String getSvnUser() {
		return svnUser;
	}


	public void setSvnPasswd(String svnPasswd) {
		this.svnPasswd = svnPasswd;
	}


	public String getSvnPasswd() {
		return svnPasswd;
	}
	

	public void setMakeZip(boolean makeZip) {
		this.makeZip = makeZip;
	}

	
	public boolean isMakeZip() {
		return makeZip;
	}
	
	@Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
	
}
