/*
 * seamframe: JobQueue.java
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
 * A queue of Jobs, waiting to be scheduled to Workers for processing.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class JobQueue {

    private ArrayList<Job> items;

    // TODO private static Logger logger = Logger.getLogger(ExperimentQueue.class.getName());

    public JobQueue() {
        items = new ArrayList<Job>();
    }


    public boolean add(Job job) {
        if (!items.contains(job)) {
            job.setState(JobState.WAITING_UNSCHEDULED);
            return items.add(job); // adds to the end of the list
        }
        return false;
    }


    public boolean remove(Job job) {
        return items.remove(job);
    }


    public boolean remove(String jobId) {
    	boolean result = false;
        Iterator<Job> iter = items.iterator();
        while (iter.hasNext()) {
            Job job = iter.next();
            if (job.getId().equals(jobId)) {
                iter.remove();
                result = true;
            }
        }
        return result;
    }


    public Iterator<Job> iterator() {
        return items.iterator();
    }


    public List<Job> getAll() {
        return Collections.unmodifiableList(items);
    }
    

    public Job getFirst() {
        if (items.size() > 0) {
            return items.get(0);
        } else {
            return null;
        }
    }


    public Job getAndRemoveFirst() {
        Job job = getFirst();
        if (job != null) {
            items.remove(0);
        }
        return job;
    }


    public Job get(String jobId) {
        for (Job job : items) {
            if (job.getId().equals(jobId)) {
                return job;
            }
        }
        return null;
    }


    /**
     * Returns the first job in the queue that is assigned to the worker with
     * the specified id.
     *
     * @param workerId
     * @return Job assigned to worker, or null
     */
    public Job getFirstJobForWorker(String workerId) {
        for (Job job : items) {
            if ((job.getAssignedToWorker() != null) && (job.getAssignedToWorker().getId().equals(workerId))) {
                return job;
            }
        }
        return null;
    }


    /**
     * Returns true when there are jobs in the queue assigned to the worker
     * with the specified id.
     *
     * @param workerId
     * @return true when jobs are assigned to the worker
     */
    public boolean jobsAssignedToWorker(String workerId) {
        return (getFirstJobForWorker(workerId) != null);
    }


    /**
     * Finds in the queue the first job that can be processed by the specified
     * worker, and is not already assigned to a worker. Jobs with state of
     * ABORTED are rescheduled first so that they can be completed.
     *
     * @param workerInfo
     * @return Job that can be processed by the worker
     */
    public Job findJobForWorker(WorkerInfo workerInfo) {
        // give priority to ABORTED jobs
        for (Job job : items) {
            if (job.getState().equals(JobState.ABORTED) && workerInfo.hasMatchingModelChain(job.getModelChain())) {
			    return job;
			}
        }

        // check unscheduled jobs
        for (Job job : items) {
            if (job.getState().equals(JobState.WAITING_UNSCHEDULED) && workerInfo.hasMatchingModelChain(job.getModelChain())) {
			    return job;
			}
        }

        return null;
    }


    public void clear() {
        items.clear();
    }


    public int size() {
        return items.size();
    }


/*
    public String toString() {
        StringBuffer output = new StringBuffer();
        output.setLength(0);
        output.append('[');
        Iterator<QueuedExperiment> i = linkList.iterator();
        while (i.hasNext()) {
            output.append(i.next());
            if (i.hasNext())
                output.append(',');
        }
        output.append(']');
        return output.toString();
    }


    public Element toXml() {
        Element root;
        root = new DOMElement("ExperimentQueue");
        Iterator<QueuedExperiment> i = linkList.iterator();
        QueuedExperiment ex;
        while (i.hasNext()) {
            ex = i.next();
            root.add(ex.toXml());
        }
        return root;
    }


    public String createState() {
        org.dom4j.Document document = org.dom4j.DocumentHelper.createDocument();
        org.dom4j.Element root = document.addElement("queue");

        Iterator<QueuedExperiment> i = linkList.iterator();
        QueuedExperiment ex;

        while (i.hasNext()) {
            ex = i.next();
            root.addElement("experiment")
                    .addAttribute("id", ex.getExperimentId())
                    .addAttribute("state", ex.getState().toString())
                    .addAttribute("chain", ex.getChain())
                    .addText(ex.getExperimentTitle());
        }

        return document.asXML();
    }


    @SuppressWarnings("unchecked")
    public void restoreState(String state) {
        clear();

        try {
            org.dom4j.Document document = DocumentHelper.parseText(state);
            org.dom4j.Element root = document.getRootElement();

            for (Iterator elementIterator = root.elementIterator("experiment"); elementIterator.hasNext();) {
                org.dom4j.Element experimentElem = (org.dom4j.Element) elementIterator.next();

                // iterate through attributes of root
                String title = experimentElem.getStringValue();
                String id = null;
                String chain = null;
                String experimentState = null;
                for (Iterator attributeIterator = experimentElem.attributeIterator(); attributeIterator.hasNext();) {
                    org.dom4j.Attribute attribute = (org.dom4j.Attribute) attributeIterator.next();
                    if (attribute.getName().equals("id"))
                        id = attribute.getValue();
                    if (attribute.getName().equals("chain"))
                        chain = attribute.getValue();
                    if (attribute.getName().equals("state"))
                        experimentState = attribute.getValue();
                }

                if (id != null) {
                    QueuedExperiment.State newState = QueuedExperiment.State.Waiting;

                    // when the experiment was running when the queue was
                    // reloaded mark it as aborted. The run loop will later
                    // process it further and remove it from the queue.
                    if (QueuedExperiment.State.Running.toString().equals(experimentState))
                        newState = QueuedExperiment.State.Aborted;

                    add(id, title, chain, newState);
                }
            }
        }
        catch (DocumentException ex) {
            throw new SeamException(ex, "Failed to restore queue state from token: %s", state);
        }
    }

*/
}
