/*
 * seamframe: SeamExchangeItem.java
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

import nl.alterra.openmi.sdk.backbone.ExchangeItem;

import org.openmi.standard.ILinkableComponent;
import org.openmi.standard.IQuantity.ValueType;


/**
 * Base class for ontology bean aware exchange items.
 * Base class for the input and output exchange items of Seamless linkable
 * components. These SeamExchangeItems are used to define the ontology based
 * Bean classes that the component can exchange with other components. This
 * addition is needed because the standard OpenMI can only exchange simple
 * quantitative data.
 *
 * @author David Huber, Ioannis N. Athanasiadis, Rob Knapen
 * @param <T> Type to be described by the exchange item
 */
public abstract class SeamExchangeItem<T extends Object> extends ExchangeItem {
    private static final long serialVersionUID = 2785178907559875989L;

    private Class<T> type;
    protected String subid;


    /**
     * Creates an instance. By default the ID of the exchange item will be set
     * to the simple name of the class. The type must be one that is annotated
     * with an ontology reference (ConceptURI).
     *
     * @param owner Linkable component the exchange item belongs to
     * @param type  Described by the exchange item
     */
    public SeamExchangeItem(ILinkableComponent owner, Class<T> type) {
        this(owner, type, "");
    }


    /**
     * Creates an instance. By default the ID of the exchange item will be set
     * to the simple name of the class. The type must be one that is annotated
     * with an ontology reference (ConceptURI).
     *
     * @param owner Linkable component the exchange item belongs to
     * @param type  Described by the exchange item
     * @param subid
     */
    public SeamExchangeItem(ILinkableComponent owner, Class<T> type, String subid) {
        super(owner, type.getSimpleName());

//        if (type.getAnnotation(ConceptURI.class) == null) {
//            throw new SeamException(
//                    "Seamless exchange items can only be used with objects annotated with an ontology!"
//            );
//        }
//        setCaption(type.getAnnotation(ConceptURI.class).toString());

        this.type = type;
        this.subid = subid;

        setID(type.getSimpleName());
        setCaption(type.getCanonicalName());
        setDescription("Default " + getID() + ' ' + subid);
    }


    @Override
    public String getID() {
        return super.getID() + subid;
    }


    @Override
    public void setID(String id) {
        super.setID(id);
        this.subid = "";
    }


    /**
     * Returns the ValueType of the ValueSet described by this exchange item.
     * This is either the input or the output data. Currently the ValueTypes
     * supported by the standard OpenMI are not sufficient for Seamless use.
     * Therefore this method will always return Scalar. Use the added method
     * getOntologyType() to get the ontology bean based class type that is
     * described by the exchange item.
     *
     * @return ValueType.Scalar
     */
    @Override
    public ValueType getValueSetType() {
        return ValueType.Scalar;
    }


    /**
     * Returns the class type of the Seamless Exchange Item.
     *
     * @return Class<T>
     */
    public Class<T> getOntologyType() {
        return type;
    }


    public String toString() {
        String owner = "None";
        if (getOwner() != null)
            owner = getOwner().getComponentID();

        return "SeamExchangeItem '" + getID() + "' for type '" + getOntologyType() + "' of component '" + owner + "\'";
	}

}
