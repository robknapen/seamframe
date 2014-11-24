/*
 * seamframe: SeamInputExchangeItem.java
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

import nl.alterra.openmi.sdk.extensions.IInputExchangeItemEx;

import org.openmi.standard.ILinkableComponent;
import org.openmi.standard.IOutputExchangeItem;


/**
 * Input exchange item based on the SeamExchangeItem. Used to specify class
 * based input of a Seamless linkable component.
 *
 * @author David Huber, Ioannis N. Athanasiadis, Rob Knapen
 * @param <T> Type to be described by the exchange item
 */
public class SeamInputExchangeItem<T> extends SeamExchangeItem<T> implements IInputExchangeItemEx {
    private static final long serialVersionUID = 1L;


    public SeamInputExchangeItem(ILinkableComponent owner, Class<T> type) {
        super(owner, type);
    }


    public SeamInputExchangeItem(ILinkableComponent owner, Class<T> type, String subid) {
        super(owner, type, subid);
    }


    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o instanceof SeamInputExchangeItem) {
            Class c = ((SeamInputExchangeItem) o).getOntologyType();
            String sub = ((SeamInputExchangeItem) o).subid;

            return c == getOntologyType() && ("" + subid).equals("" + sub);
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    public boolean isConnectableWith(IOutputExchangeItem itm) {
        if (itm instanceof SeamOutputExchangeItem) {
            if (((SeamOutputExchangeItem) itm).getOntologyType().equals(this.getOntologyType())) {
                if (getOwner() != null && getOwner().equals(((SeamOutputExchangeItem) itm).getOwner())) return false;

                return true;
            }
        }

        return false;
    }
}
