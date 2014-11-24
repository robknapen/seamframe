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

package org.seamless_if.processing.worker;

import org.seamless_if.processing.scheduler.ModelChainInfo;
import org.seamless_if.processing.scheduler.WorkerInfo;
import org.seamless_if.processing.scheduler.WorkerState;

/**
 * The Worker class co-operates with the Scheduler to get assigned jobs for
 * executing a particular SeamChain model chain for a given Experiment (a set
 * of input parameters). It is a base for further implementation, e.g. it can
 * be used to construct a client-server solution.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class Worker {
	
	private WorkerInfo workerInfo = new WorkerInfo();

	
	public Worker(String name, String ip) {
		workerInfo.setName(name);
		workerInfo.setState(WorkerState.UNKNOWN);
		workerInfo.setIp(ip);
	}
	
	
	public WorkerInfo getWorkerInfo() {
		return workerInfo;
	}
	
	
	public void addAvailableModelChain(ModelChainInfo info) {
		workerInfo.addAvailableModelChain(info);
	}
}

