/*
 * seamframe: BusinessUtil.java
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

package org.seamless_ip.core;

import org.seamless_ip.ontologies.seamproj.SpatialScale;

/**
 * @author lorenzo
 */
@Deprecated
public class BusinessUtil {
    public static enum ChainType {
        CAPRI_SIMPLE_FSSIM, FSSIM_INCLUDING_APES, NOT_ASSIGNED
    }

    public static enum BaseLine {
        BL2000, BL2013, BL2020, NOT_ASSIGNED
    }

    /*
    * public static ChainType defineChain(SpatialScale spatialScale)
    *
    * perform some business rules to define the chain type based on a SpatialScale
    *
    * get the chain type of an experiment:
    *  > ChainType c = BusinessUtil.defineChain(experiment.getOfProblem().getSpatialScale());
    *
    */
    public static ChainType defineChain(SpatialScale spatialScale) {
        if (spatialScale == null)
            return ChainType.NOT_ASSIGNED;

        if ((spatialScale.getExtent() == null) || (spatialScale.getResolution() == null)) {
            return ChainType.NOT_ASSIGNED;
        }
        if ((spatialScale.getExtent().equals("Nuts2")) &&
                (spatialScale.getResolution().equals("Farm type"))) {
            return ChainType.CAPRI_SIMPLE_FSSIM;

        }
        if ((spatialScale.getExtent().equals("Farm type")) &&
                (spatialScale.getResolution().equals("Activity*AEnZ"))) {
            return ChainType.FSSIM_INCLUDING_APES;
        } else {
            return ChainType.NOT_ASSIGNED;
        }
    }

}
