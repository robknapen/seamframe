/*
 * seamframe: ModelChainInfoTO.java
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
import org.seamless_if.processing.scheduler.ModelChainInfo;

/**
 * Transfer Object for exchanging ModelChainInfo data.
 * 
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class ModelChainInfoTO {

    private String id;
    private String name;
    private String version;


    public ModelChainInfoTO() {
        this(new ModelChainInfo());
    }


    public ModelChainInfoTO(ModelChainInfo modelChainInfo) {
    	ModelChainInfo obj = modelChainInfo;
    	if (obj == null)
    		obj = new ModelChainInfo();
    	
        setId(obj.getId());
        setName(obj.getName());
        setVersion(obj.getVersion());
    }


    public ModelChainInfo toModelChainInfo() {
        ModelChainInfo obj = new ModelChainInfo();
        obj.setId(getId());
        obj.setName(getName());
        obj.setVersion(getVersion());
        return obj;
    }


    public String getId() {
        return id;
    }


    private void setId(String id) {
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
        if (!(o instanceof ModelChainInfoTO)) return false;

        ModelChainInfoTO that = (ModelChainInfoTO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }


    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    
    public Element toXml() {
        Element root = new DOMElement("chain");
        root.addAttribute("id", getId().toString());
        root.add(new DOMElement("name").addText(getName()));
        root.add(new DOMElement("version").addText(getVersion()));
        return root;
    }
    
}
