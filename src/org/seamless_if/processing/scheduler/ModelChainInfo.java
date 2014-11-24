/*
 * seamframe: ModelChainInfo.java
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
 * Information about a particular model chain. Used to match model chains and the
 * model (versions) included required for a Job to what is available by a Worker.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class ModelChainInfo {

    private String id;
    private String name;
    private String version;


    public ModelChainInfo() {
        // ID is unique over time for this host
        id = new UID().toString();
        name = "New ModelChainInfo";
        version = "1.0.0";
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getVersion() {
        return version;
    }


    public void setVersion(String version) {
        this.version = version;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelChainInfo)) return false;

        ModelChainInfo that = (ModelChainInfo) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }


    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


    public Element toXml() {
        Element root = new DOMElement("ModelChainInfo");
        root.addAttribute("id", getId().toString());
        root.add(new DOMElement("Name").addText(getName()));
        root.add(new DOMElement("Version").addText(getVersion()));
        return root;
    }
    
}
