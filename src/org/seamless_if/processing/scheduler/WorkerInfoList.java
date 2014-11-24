/*
 * seamframe: WorkerList.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A list of Workers, available to process jobs.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class WorkerInfoList extends ArrayList<WorkerInfo> {

	private static final long serialVersionUID = 1L;


	public WorkerInfoList() {
    }


    @Override
    public boolean add(WorkerInfo workerInfo) {
        if (!contains(workerInfo)) {
            workerInfo.setState(WorkerState.UNKNOWN);
            return super.add(workerInfo);
        }
        return false;
    }


    public void remove(String workerId) {
        Iterator<WorkerInfo> iter = iterator();
        while (iter.hasNext()) {
            WorkerInfo workerInfo = iter.next();
            if (workerInfo.getId().equals(workerId)) {
                workerInfo.setState(WorkerState.REMOVED);
                iter.remove();
            }
        }
    }


    public WorkerInfo get(String workerId) {
        for (WorkerInfo workerInfo : this) {
            if (workerInfo.getId().equals(workerId)) {
                return workerInfo;
            }
        }
        return null;
    }
    
    
    public List<ModelChainInfo> getCurrentlyKnownModelChainsInfo() {
    	List<ModelChainInfo> result = new ArrayList<ModelChainInfo>();
    	for (WorkerInfo workerInfo : this) {
    		List<ModelChainInfo> available = workerInfo.getAvailableModelChains();
    		for (ModelChainInfo info : available) {
    			if (!result.contains(info))
    				result.add(info);
    		}
    	}
    	return result;
    }
}
