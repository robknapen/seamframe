/*
 * seamframe: Job.java
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

package org.seamless_if.processing.scheduler;

import java.rmi.server.UID;

import org.dom4j.Element;
import org.dom4j.dom.DOMElement;

/**
 * A Job is a task to run a chain of models for a certain experiment. It can be
 * queued, scheduled and processed by a Worker.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class Job {

    private String id;
    private JobState state;
    private ModelChainInfo modelChain;
    private Long experimentId;
    private WorkerInfo assignedToWorker;
    private String logUrl;


    public Job() {
        // ID is unique in time for this host
        id = new UID().toString();
        state = JobState.NEW;
        experimentId = 0L;
        assignedToWorker = null;
        logUrl = "";
    }


    public Job(ModelChainInfo modelChain, Long experimentId) {
        this();
        this.modelChain = modelChain;
        this.experimentId = experimentId;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public JobState getState() {
        return state;
    }


    public void setState(JobState state) {
        this.state = state;
        if (JobState.WAITING_UNSCHEDULED.equals(state)) {
            assignedToWorker = null;
        }
    }


    public ModelChainInfo getModelChain() {
        return modelChain;
    }


    public void setModelChain(ModelChainInfo modelChain) {
        this.modelChain = modelChain;
    }


    public Long getExperimentId() {
        return experimentId;
    }


    public void setExperimentId(Long experimentId) {
        this.experimentId = experimentId;
    }


    public WorkerInfo getAssignedToWorker() {
        return assignedToWorker;
    }


    public void setAssignedToWorker(WorkerInfo assignedToWorker) {
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
        if (!(o instanceof Job)) return false;

        Job job = (Job) o;

        if (id != null ? !id.equals(job.id) : job.id != null) return false;

        return true;
    }


    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


    public Element toXml() {
        Element root = new DOMElement("Job");
        root.addAttribute("id", getId().toString());
        root.add(new DOMElement("State").addText(state.toString()));
        root.add(new DOMElement("LogUrl").addText(logUrl));
        if (experimentId != null)
            root.add(new DOMElement("ExperimentId").addText(experimentId.toString()));
        if (modelChain != null)
            root.add(modelChain.toXml());
        if (assignedToWorker != null)
            root.add(assignedToWorker.toXml());
        
        return root;
    }

}
