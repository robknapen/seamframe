/*
 * seamframe: History.java
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A history of Jobs that have been processed.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class JobHistory {

    private ArrayList<Job> items;


    public JobHistory() {
        items = new ArrayList<Job>();
    }


    public void addJob(Job job) {
        if (!items.contains(job)) {
            items.add(job); // adds to the end of the list
        }
    }


    public void removeJob(String jobId) {
        Iterator<Job> iter = items.iterator();
        while (iter.hasNext()) {
            if (iter.next().getId().equals(jobId)) {
                iter.remove();
            }
        }
    }


    public Job getJob(String jobId) {
        for (Job job : items) {
            if (job.getId().equals(jobId)) {
                return job;
            }
        }
        return null;
    }


    public Iterator<Job> iterator() {
        return items.iterator();
    }


    public List<Job> getAll() {
        return Collections.unmodifiableList(items);
    }


    public void clear() {
        items.clear();
    }
}
