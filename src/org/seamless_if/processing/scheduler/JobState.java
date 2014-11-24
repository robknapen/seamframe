/*
 * seamframe: JobState.java
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
 * Enumeration defining valid states of a Job.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public enum JobState {
    /**
     * New job, not in queue yet.
     */
    NEW,

    /**
     * Job was removed from the scheduler. 
     */
    REMOVED,

    /**
     * Job in queue, waiting to be scheduled to a worker.
     */
    WAITING_UNSCHEDULED,

    /**
     * Job in queue, assigned to worker, waiting to be processed.
     */
    WAITING_SCHEDULED,

    /**
     * Job in queue, being processed by a worker.
     */
    IN_PROGRESS,

    /**
     * Job in history, processing was aborted (due to some external condition,
     * like power failure).
     */
    ABORTED,

    /**
     * Job in history, processing completed by worker without problems.
     */
    COMPLETED_OK,

    /**
     * Job in history, processing completed by worker but errors detected.
     */
    COMPLETED_WITH_ERRORS,

    /**
     * Job in history, processing completed by worker, with warnings.
     */
    COMPLETED_WITH_WARNINGS;


    /**
     * Checks if the state can be set externally (i.e. by a Worker client)
     * or if it is intended for internal use only (i.e. it can only be set
     * by the Scheduler).
     *
     * @return true if the state is allowed to be set externally
     */
    public boolean canBeSetExternally() {
        return (this.equals(IN_PROGRESS) || this.equals(ABORTED) || this.equals(COMPLETED_OK)
            || this.equals(COMPLETED_WITH_WARNINGS) || this.equals(COMPLETED_WITH_ERRORS));
    }
}
