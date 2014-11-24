/*
 * seamframe: WorkerState.java
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

/**
 * Enumeration defining possible states for a Worker.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public enum WorkerState {
    /**
     * State of worker is not known at the moment.
     */
    UNKNOWN,

    /**
     * Worker is idle and available to work on a suitable Job.
     */
    IDLE,

    /**
     * Worker is busy processing a Job.
     */
    BUSY,

    /**
     * Worker is halted due to some kind of problem which needs to be resolved
     * before processing can continue.
     */
    ERROR,

    /**
     * Worker was removed from the scheduler.
     */
    REMOVED,

    /**
     * Worker is on-line but not available for processing Jobs at the moment.  
     */
    NOT_AVAILABLE;


    /**
     * Checks if the state indicates that a worker is available to work on
     * a job (maybe already busy doing so), or not.
     *
     * @return true if the state indicates worker is available
     */
    public boolean isAvailableState() {
        return (this.equals(IDLE) || this.equals(BUSY));
    }


    /**
     * Checks if the state can be set externally (i.e. by a Worker client)
     * or if it is intended for internal use only (i.e. it can only be set
     * by the Scheduler).
     *
     * @return true if the state is allowed to be set externally
     */
    public boolean canBeSetExternally() {
        return !(this.equals(UNKNOWN) || this.equals(REMOVED));
    }
}
