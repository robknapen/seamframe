/*
 * seamframe: Scheduler.java
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

import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.XMLWriter;
import org.seamless_if.processing.sofa.SeamException;

/**
 * Singleton object that coordinates between a set of Workers and list of Jobs
 * in a Queue. It schedules Jobs to Workers, follows progress, and keeps track
 * of the availability of each Worker.
 * <p/>
 * Note: Uses the enum pattern to implement the singleton, access it with
 * Scheduler.INSTANCE.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public enum Scheduler {

    INSTANCE;

    /**
     * Required state update frequency for workers, before they will be
     * considered to have timed out and set to unavailable.
     */
    private static long WORKER_STATE_UPDATE_TIMEOUT_IN_SEC = 30;

    /**
     * Time in seconds that the scheduler will sleep inbetween scheduling
     * jobs in the queue.
     */
    private static long SCHEDULER_SLEEP_TIME_IN_SEC = 10;

    /**
     * Logger for the Scheduler.
     */
    private static Logger logger = Logger.getLogger(Scheduler.class.getName());

    /**
     * Name of file used to persist scheduler data.
     */
    private static String schedulerFileName = null;


    private WorkerInfoList workers = new WorkerInfoList();
    private JobQueue jobQueue = new JobQueue();
    private JobHistory jobHistory = new JobHistory();

    // TODO figure out which methods have to be synchronized


    public void log(String message, boolean fatal) {
        System.out.println("Scheduler: " + message);
        if (fatal) {
            logger.error(message);
            throw new RuntimeException(message);
        } else {
            logger.info(message);
        }
    }


    public WorkerInfo registerWorker(WorkerInfo workerInfo) {
    	// check for duplicate queue entries
        if (getWorker(workerInfo.getId()) != null) {
            log("Cancelling registration of worker [" + workerInfo + "], a worker with the same ID is already registered.", true);
            return null;
        }
        workerInfo.setState(WorkerState.UNKNOWN);
        if (workers.add(workerInfo))
        	return workerInfo;
        else
        	return null;
    }
    
    
    public WorkerInfo registerWorker(String ip, String name) {
    	WorkerInfo worker = new WorkerInfo(ip, name);
    	return registerWorker(worker);
    }


    public List<WorkerInfo> getAllWorkers() {
        return Collections.unmodifiableList(workers);
    }


    public WorkerInfo getWorker(String workerId) {
        return workers.get(workerId);
    }


    public void unregisterWorker(String workerId) {
    	workers.remove(workerId);
    }
    
    
    public ModelChainInfo registerModelChainForWorker(String workerId, String modelChainName, String modelChainVersion) {
    	// check parameters
    	if ((workerId == null) || (modelChainName == null) ||(modelChainVersion == null))
    		return null;
    	
    	// worker with matching id should exist
    	WorkerInfo worker = getWorker(workerId);
    	if (worker == null)
    		return null;
    	
    	// check if model chain with name and version already exists
    	List<ModelChainInfo> current = getAllKnownModelChainsInfo();
    	for (ModelChainInfo info : current) {
    		if (modelChainName.equalsIgnoreCase(info.getName()) && modelChainVersion.equalsIgnoreCase(info.getVersion())) {
    			worker.addAvailableModelChain(info);
    			return info;
    		}
    	}
    	
    	// introduce new model chain
    	ModelChainInfo info = new ModelChainInfo();
    	info.setName(modelChainName);
    	info.setVersion(modelChainVersion);
    	worker.addAvailableModelChain(info);
    	return info;
    }
    
    
    public ModelChainInfo unregisterModelChainForWorker(String workerId, String modelChainId) {
    	// check parameters
    	if ((workerId == null) || (modelChainId == null))
    		return null;
    	
    	// worker with matching id should exist
    	WorkerInfo worker = getWorker(workerId);
    	if (worker == null)
    		return null;
    	
    	return worker.removeAvailableModelChain(modelChainId);
    }
    
    
    public List<ModelChainInfo> getAllKnownModelChainsInfo() {
    	List<ModelChainInfo> result = workers.getCurrentlyKnownModelChainsInfo();
    	return Collections.unmodifiableList(result);
    }
    
    
    public ModelChainInfo getModelChainInfo(String modelChainId) {
    	List<ModelChainInfo> available = workers.getCurrentlyKnownModelChainsInfo();
    	for (ModelChainInfo info : available) {
    		if (info.getId().equals(modelChainId))
    			return info;
    	}
    	return null;
    }
    
    
    public Job addJob(Long experimentId, String modelChainId) {
    	// check for existing model chain
    	Job job = new Job();
    	job.setExperimentId(experimentId);
    	job.setModelChain(getModelChainInfo(modelChainId));
        if (job.getModelChain() == null) {
            log("Cancelling adding [" + job + "] to queue, currently no calculation node provides a model chain with ID: " + modelChainId, true);
            return null;
        }
        return addJob(job);
    }


    public Job addJob(Job job) {
    	// check for duplicate queue entries
        if (getJob(job.getId()) != null) {
            log("Cancelling adding [" + job + "] to queue, a job with the same ID is already queued.", true);
            return null;
        }
        job.setState(JobState.WAITING_UNSCHEDULED);
        if (jobQueue.add(job))
        	return job;
        else
        	return null;
    }


    public Job getJobFromQueue(String jobId) {
        return jobQueue.get(jobId);
    }


    public List<Job> getAllJobsFromQueue() {
        return jobQueue.getAll();
    }


    public Job getJobFromHistory(String jobId) {
        return jobHistory.getJob(jobId);
    }


    public List<Job> getAllJobsFromHistory() {
        return jobHistory.getAll();
    }


    public Job getJob(String jobId) {
        Job job = jobQueue.get(jobId);
        if (job != null) {
            return job;
        }
        return jobHistory.getJob(jobId);
    }


    public synchronized Job getJobForWorker(String workerId) {
        Job job = jobQueue.getFirstJobForWorker(workerId);
        return job;
    }


    public synchronized void updateWorkerState(String workerId, WorkerState newState) {
        if (!newState.canBeSetExternally()) {
            log("A client is not allowed to set worker state to " + newState, true);
            return;
        }

        WorkerInfo workerInfo = workers.get(workerId);
        if (workerInfo == null) {
            log("No worker registered with id: " + workerId, true);
            return;
        }

        workerInfo.setState(newState);
    }


    /**
     * Updates the state of a job, called by a Worker. Based on the newState
     * specified the Scheduler decides what to do with the job, for example
     * remove it from the queue and place it in the history. Note that not
     * all states can be set this way, Workers are only allowed to set part
     * of the possible states.
     *
     * @param jobId
     * @param newState
     */
    public synchronized void updateJobState(String jobId, JobState newState) {
        if (!newState.canBeSetExternally()) {
            log("A client is not allowed to set job state to " + newState, true);
            return;
        }

        Job job = jobQueue.get(jobId);
        if (job == null) {
            log("Queue does not contain a job with id: " + jobId, true);
            return;
        }

        switch (newState) {
            case IN_PROGRESS:
            case ABORTED:
                // do nothing besides updating the state, job stays queued
                job.setState(newState);
                break;

            case COMPLETED_OK:
            case COMPLETED_WITH_WARNINGS:
            case COMPLETED_WITH_ERRORS:
                // job must have been IN_PROGRESS
                if (!job.getState().equals(JobState.IN_PROGRESS))
                        log("Invalid job state change from " + job.getState() + " to " + newState, true);
                job.setState(newState);
                moveJobToHistory(job);
                break;
        }

        // TODO save changes
    }


    private void moveJobToHistory(Job job) {
        jobQueue.remove(job.getId());
        jobHistory.addJob(job);
        // TODO save changes
    }


    public synchronized void scheduleJobs() {

        checkWorkersStateUpdateTimeout();
        updateJobsStateForWorkerAvailability();

        for (WorkerInfo workerInfo : workers) {
            if (workerInfo.getState().equals(WorkerState.IDLE)) {
                if (!jobQueue.jobsAssignedToWorker(workerInfo.getId())) {
                    Job job = jobQueue.findJobForWorker(workerInfo);
                    if (job != null) {
                        job.setAssignedToWorker(workerInfo);
                        job.setState(JobState.WAITING_SCHEDULED);
                    }
                }
            }
        }
    }


    /**
     * Checks all registered workers to see which one did not update its
     * state in time (before the specified timeout constant). When state
     * has not been updated in time it is set to UNKNOWN.
     */
    private synchronized void checkWorkersStateUpdateTimeout() {
        long thresholdTime = System.currentTimeMillis() - WORKER_STATE_UPDATE_TIMEOUT_IN_SEC * 1000;
        for (WorkerInfo workerInfo : workers) {
            if (workerInfo.getLastStateUpdateInMillis() < thresholdTime) {
                workerInfo.setState(WorkerState.UNKNOWN);
            }
        }
    }


    /**
     * Checks current job assignments to workers and when a job is
     * assigned to a worker that is no longer available resets the
     * assignment and set the job state to UNSCHEDULED.
     */
    private synchronized void updateJobsStateForWorkerAvailability() {
        Iterator<Job> iterator = jobQueue.iterator();
        while (iterator.hasNext()) {
            Job queuedJob = iterator.next();
            if ((queuedJob.getAssignedToWorker() != null) && (!queuedJob.getAssignedToWorker().getState().isAvailableState())) {
                queuedJob.setAssignedToWorker(null);
                queuedJob.setState(JobState.WAITING_UNSCHEDULED);
            }
        }
    }


    private static volatile boolean stopRequested = false;
    private static volatile Thread schedulerThread;

    
    /**
     * Runs the scheduling at the specified update frequency as a separate
     * thread.
     */
    public void start() {
        if (schedulerThread != null)
            return;

        schedulerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (!stopRequested) {
                        log("Scheduling jobs...", false);
                        Scheduler.INSTANCE.scheduleJobs();
                        log("Sleeping for " + SCHEDULER_SLEEP_TIME_IN_SEC + " sec.", false);
                        Thread.sleep(SCHEDULER_SLEEP_TIME_IN_SEC * 1000);
                    }
                    log("Scheduler thread stopped", false);
                    schedulerThread = null; // TODO test if we can do this
                } catch (InterruptedException e) {
                    log("Scheduler thread stopped by error", false);
                    e.printStackTrace();
                    schedulerThread = null;
                }
            }
        });
        log("Starting scheduler thread", false);
        schedulerThread.start();
    }


    /**
     * Requests stopping of the background scheduling thread.
     */
    public void stop() {
        stopRequested = true;
    }


    public void clear() {
        if (schedulerThread != null) {
            throw new SeamException("Can not clear the scheduler queue while it is running!");
        } else {
            jobQueue.clear();
            jobHistory.clear();
            workers.clear();
        }
    }


    /**
     * Sets the name of the file to be used to persist scheduler data.
     *
     * @param filename Name of file that holds scheduler information
     */
    public synchronized void setFileName(String filename) {
        if ((filename != null) && (schedulerFileName == null) || (!schedulerFileName.equals(filename))) {
            logger.info("Setting scheduler state filename to: " + filename);
            schedulerFileName = filename;

            // see if we are initializing and need to restore data from the file
            // Scheduling must not be active, queue must still be empty and the file must exist
            if ((schedulerThread == null) && (jobQueue.size() == 0) && (new File(filename).exists())) {
                logger.info("Initialising scheduler state from file: " + filename);
                load();
            }
        }
    }


    /**
     * Returns the name of the file used to persist the scheduler state in.
     *
     * @return filename
     */
    public synchronized String getQueueFileName() {
        return schedulerFileName;
    }


    public Element toXml() {
        Element root = new DOMElement("Scheduler");

        Element jobs = root.addElement("Jobs");
        Iterator<Job> iterQueue = jobQueue.iterator();
        while (iterQueue.hasNext()) {
            jobs.add(iterQueue.next().toXml());
        }
        Iterator<Job> iterHistory = jobHistory.iterator();
        while (iterHistory.hasNext()) {
            jobs.add(iterHistory.next().toXml());
        }

        return root;
    }


    /**
     * Saves the current state of the scheduler.
     */
    public synchronized void save() {
        save(schedulerFileName);
    }


    /**
     * Saves the current state of the scheduler in a file with the specified
     * name.
     *
     * @param filename of file to store scheduler state in
     */
    public synchronized void save(String filename) {
        logger.info("Saving scheduler state to file: " + filename);

        Document document = DocumentHelper.createDocument(toXml());

        try {
            XMLWriter writer = new XMLWriter(new FileWriter(new File(filename), false));
            writer.write(document);
            writer.close();
        }
        catch (Exception ex) {
            throw new SeamException(ex, "Failed to save scheduler state! Error: %s", ex.getMessage());
        }

        logger.info("Saving scheduler state completed");
    }


    /**
     * Restores the state of the queue.
     */
    public synchronized void load() {
        load(schedulerFileName);
    }


    /**
     * Restores the state of the queue from the file with the specified file
     * name.
     *
     * @param filename to restore queue state from
     */
    public synchronized void load(String filename) {
        logger.info("Loading scheduler data from file: " + filename);

        /* TODO
        StringBuilder contents = new StringBuilder();

        try {
            BufferedReader input = new BufferedReader(new FileReader(new File(filename)));
            try {
                String line;
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex) {
            throw new SeamException(ex, "Failed to load queue information! Error: %s", ex
                    .getMessage());
        }

        _expQueue.restoreState(contents.toString());
        */

        logger.info("Loading scheduler data completed");
    }

}
