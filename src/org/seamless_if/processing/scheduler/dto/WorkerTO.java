/*
 * seamframe: WorkerTO.java
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

import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.seamless_if.processing.scheduler.ModelChainInfo;
import org.seamless_if.processing.scheduler.WorkerInfo;
import org.seamless_if.processing.scheduler.WorkerState;

/**
 * Transfer Object for exchanging Worker data.
 * 
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class WorkerTO {

    private String id;
    private String ip;
    private WorkerState state;
    private long lastStateUpdateInMillis;
    private String name;
    private ArrayList<ModelChainInfoTO> availableModelChains;


    public WorkerTO() {
        this(new WorkerInfo());
    }


    public WorkerTO(WorkerInfo workerInfo) {
    	WorkerInfo obj = workerInfo;
    	if (obj == null)
    		obj = new WorkerInfo();
    	
        setId(obj.getId());
        setIp(obj.getIp());
        setState(obj.getState());
        setLastStateUpdateInMillis(obj.getLastStateUpdateInMillis());
        setName(obj.getName());

        availableModelChains = new ArrayList<ModelChainInfoTO>();
        availableModelChains.clear();
        for (ModelChainInfo mc : obj.getAvailableModelChains()) {
            availableModelChains.add(new ModelChainInfoTO(mc));
        }
    }


    public WorkerInfo toWorker() {
        WorkerInfo obj = new WorkerInfo();
        obj.setId(getId());
        obj.setIp(getIp());
        obj.setState(getState());
        obj.setLastStateUpdateInMillis(getLastStateUpdateInMillis());
        obj.setName(getName());

        for (ModelChainInfoTO mc : getAvailableModelChains()) {
            obj.addAvailableModelChain(mc.toModelChainInfo());
        }
        return obj;
    }


    public String getIp() {
        return ip;
    }


    public void setIp(String ip) {
        this.ip = ip;
    }


    public WorkerState getState() {
        return state;
    }


    public void setState(WorkerState state) {
        this.state = state;
    }


    public long getLastStateUpdateInMillis() {
        return lastStateUpdateInMillis;
    }


    public void setLastStateUpdateInMillis(long lastStateUpdateInMillis) {
        this.lastStateUpdateInMillis = lastStateUpdateInMillis;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }


    private void setId(String id) {
        this.id = id;
    }


    public ArrayList<ModelChainInfoTO> getAvailableModelChains() {
        return availableModelChains;
    }


    public void setAvailableModelChains(ArrayList<ModelChainInfoTO> availableModelChains) {
        this.availableModelChains = availableModelChains;
    }


    public void addAvailableModelChain(ModelChainInfoTO info) {
        if (!availableModelChains.contains(info)) {
            availableModelChains.add(info);
        }
    }


    public void removeAvailableModelChain(String modelChainId) {
        Iterator<ModelChainInfoTO> iter = availableModelChains.iterator();
        while (iter.hasNext()) {
            if (iter.next().getId().equals(modelChainId)) {
                iter.remove();
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkerTO)) return false;

        WorkerTO workerTO = (WorkerTO) o;

        if (id != null ? !id.equals(workerTO.id) : workerTO.id != null) return false;

        return true;
    }


    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    
    public Element toXml() {
        Element root = new DOMElement("worker");
        root.addAttribute("id", getId().toString());
        root.add(new DOMElement("state").addText(state.toString()));
        root.add(new DOMElement("ip").addText(ip));
        root.add(new DOMElement("name").addText(name));
        Element chains = new DOMElement("availableModelChains");
        for (ModelChainInfoTO chain : availableModelChains)
        	chains.add(chain.toXml());
        root.add(chains);
        return root;
    }
    
}
