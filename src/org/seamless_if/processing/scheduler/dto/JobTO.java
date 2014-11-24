/*
 * seamframe: JobTO.java
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
 * ================================================================================
 */

package org.seamless_if.processing.scheduler.dto;

import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.seamless_if.processing.scheduler.Job;
import org.seamless_if.processing.scheduler.JobState;

/**
 * Transfer Object for exchanging Job data. Contains the subset of information
 * that the client is allowed to see.
 * 
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class JobTO {

    private String id;
    private JobState state;
    private ModelChainInfoTO modelChain;
    private Long experimentId;
    private WorkerTO assignedToWorker;
    private String logUrl;

    public JobTO() {
        this(new Job());
    }


    public JobTO(Job job) {
    	Job obj = job;
    	if (obj == null)
    		obj = new Job();
    	
        setId(obj.getId());
        setState(obj.getState());
        setModelChain(new ModelChainInfoTO(obj.getModelChain()));
        setExperimentId(obj.getExperimentId());
        if (obj.getAssignedToWorker() != null)
        	setAssignedToWorker(new WorkerTO(obj.getAssignedToWorker()));
        else
        	setAssignedToWorker(null);
        setLogUrl(obj.getLogUrl());
    }


    public Job toJob() {
        Job obj = new Job();
        obj.setId(getId());
        obj.setState(getState());
        obj.setModelChain(modelChain.toModelChainInfo());
        obj.setExperimentId(getExperimentId());
        if (getAssignedToWorker() != null)
        	obj.setAssignedToWorker(getAssignedToWorker().toWorker());
        else
        	obj.setAssignedToWorker(null);
        obj.setLogUrl(getLogUrl());
        return obj;
    }


    public String getId() {
        return id;
    }


    private void setId(String id) {
        this.id = id;
    }


    public JobState getState() {
        return state;
    }


    public void setState(JobState state) {
        this.state = state;
    }


    public ModelChainInfoTO getModelChain() {
        return modelChain;
    }


    public void setModelChain(ModelChainInfoTO modelChain) {
        this.modelChain = modelChain;
    }


    public Long getExperimentId() {
        return experimentId;
    }


    public void setExperimentId(Long experimentId) {
        this.experimentId = experimentId;
    }


    public WorkerTO getAssignedToWorker() {
        return assignedToWorker;
    }


    public void setAssignedToWorker(WorkerTO assignedToWorker) {
        this.assignedToWorker = assignedToWorker;
    }


    public String getLogUrl() {
        return logUrl;
    }


    public void setLogUrl(String logUrl) {
        this.logUrl = logUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobTO)) return false;

        JobTO jobTO = (JobTO) o;

        if (id != null ? !id.equals(jobTO.id) : jobTO.id != null) return false;

        return true;
    }


    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    
    public Element toXml() {
        Element root = new DOMElement("job");
        root.addAttribute("id", getId().toString());
        root.add(new DOMElement("state").addText(state.toString()));
        root.add(new DOMElement("logUrl").addText(logUrl));

        if (experimentId != null)
            root.add(new DOMElement("experimentId").addText(experimentId.toString()));
        if (modelChain != null)
        	root.add(modelChain.toXml());
        if (assignedToWorker != null)
        	root.add(assignedToWorker.toXml());
        
        return root;
    }
    
}
