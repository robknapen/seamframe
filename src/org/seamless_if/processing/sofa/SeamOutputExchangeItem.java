/*
 * seamframe: SeamOutputExchangeItem.java
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

import nl.alterra.openmi.sdk.extensions.IOutputExchangeItemEx;

import org.openmi.standard.IDataOperation;
import org.openmi.standard.IInputExchangeItem;
import org.openmi.standard.ILinkableComponent;


/**
 * Output exchange item based on the SeamExchangeItem. Used to specify class
 * based output of a Seamless linkable component.
 *
 * @author David Huber, Ioannis N. Athanasiadis, Rob Knapen
 * @param <T> Type to be described by the exchange item
 */
public class SeamOutputExchangeItem<T> extends SeamExchangeItem<T> implements IOutputExchangeItemEx {
    private static final long serialVersionUID = 1L;


    /**
     * Creates an instance.
     *
     * @param owner of the exchange item
     * @param type  of output class described by the exchange item
     */
    public SeamOutputExchangeItem(ILinkableComponent owner, Class<T> type) {
        super(owner, type);
    }


    public int getDataOperationCount() {
        return 0;
    }


    public IDataOperation getDataOperation(int dataOperationIndex) {
        throw new SeamException(
                "Output exchange item '" + getID() + "' of component '" +
                        getOwner().getModelID() + "' has no data operation with index " + dataOperationIndex);
    }


    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o instanceof SeamOutputExchangeItem) {
            Class c = ((SeamOutputExchangeItem) o).getOntologyType();
            String sub = ((SeamOutputExchangeItem) o).subid;

            return c == getOntologyType() && ("" + subid).equals("" + sub);
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    public boolean isConnectableWith(IInputExchangeItem itm) {
        if (itm instanceof SeamInputExchangeItem) {
            if (((SeamInputExchangeItem) itm).getOntologyType().equals(this.getOntologyType())) {
                if (getOwner() != null && getOwner().equals(((SeamInputExchangeItem) itm).getOwner())) return false;

                return true;
            }
        }

        return false;
	}
}
