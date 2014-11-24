/*
 * seamframe: Worker.java
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.dom.DOMElement;

/**
 * A Worker represents a computer node that can process Jobs. I.e. it has certain
 * model chains installed and can run them to process experiments.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class WorkerInfo {

    private String ip;
    private WorkerState state;
    private long lastStateUpdateInMillis;
    private String name;
    private String id;
    private ArrayList<ModelChainInfo> availableModelChains;


    public WorkerInfo() {
        // ID is unique over time for this host
        id = new UID().toString();
        ip = "127.0.0.1";
        state = WorkerState.UNKNOWN;
        lastStateUpdateInMillis = System.currentTimeMillis();
        name = "New Worker";
        availableModelChains = new ArrayList<ModelChainInfo>();
    }


    public WorkerInfo(String ip, String name) {
        this();
        this.ip = ip;
        this.name = name;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
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
        // set state update stamp to now
        lastStateUpdateInMillis = System.currentTimeMillis();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkerInfo)) return false;

        WorkerInfo workerInfo = (WorkerInfo) o;

        if (id != null ? !id.equals(workerInfo.id) : workerInfo.id != null) return false;
        if (ip != null ? !ip.equals(workerInfo.ip) : workerInfo.ip != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    public void addAvailableModelChain(ModelChainInfo info) {
        if (!availableModelChains.contains(info)) {
            availableModelChains.add(info);
        }
    }


    public boolean hasMatchingModelChain(ModelChainInfo modelChain) {
        return availableModelChains.contains(modelChain);
    }


    public List<ModelChainInfo> getAvailableModelChains() {
        return Collections.unmodifiableList(availableModelChains);
    }


    public Element toXml() {
        Element root = new DOMElement("WorkerInfo");
        root.addAttribute("id", getId().toString());
        root.add(new DOMElement("ip").addText(getIp()));
        root.add(new DOMElement("Name").addText(getName()));
        root.add(new DOMElement("State").addText(state.toString()));
        root.add(new DOMElement("LastStateUpdateInMillis").addText(String.valueOf(lastStateUpdateInMillis)));

        Element chainInfos = new DOMElement("ModelChainInfos");
        for (ModelChainInfo info : availableModelChains) {
            chainInfos.add(info.toXml());
        }
        root.add(chainInfos);

        return root;
    }


	public ModelChainInfo removeAvailableModelChain(String modelChainId) {
		if (modelChainId == null)
			return null;
		Iterator<ModelChainInfo> iter = availableModelChains.iterator();
		while (iter.hasNext()) {
			ModelChainInfo info = iter.next();
			if (modelChainId.equals(info.getId())) {
				iter.remove();
				return info;
			}
		}
		return null;
	}

}
