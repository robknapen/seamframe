/*
 * seamframe: SchedulerTest.java
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for the Scheduler class. This is the main API for the scheduling
 * component of the SEAMLESS-IF server.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class SchedulerTest {

    private ModelChainInfo chainA;
    private ModelChainInfo chainB;
    private WorkerInfo workerA;
    private String workerIdA;
    private WorkerInfo workerB;
    private String workerIdB;
    private Job jobA;
    private String jobIdA;
    private Job jobB;
    private String jobIdB;
    
    @Before
    public void setUp() throws Exception {
        // create some model chain info to use
        chainA = new ModelChainInfo();
        chainB = new ModelChainInfo();

        // register worker A with scheduler, runs model chain A and B
        workerA = new WorkerInfo();
        workerA.addAvailableModelChain(chainA);
        workerA.addAvailableModelChain(chainB);
        workerIdA = workerA.getId();
        Scheduler.INSTANCE.registerWorker(workerA);

        // register worker B with scheduler, only runs model chain B
        workerB = new WorkerInfo();
        workerB.addAvailableModelChain(chainB);
        workerIdB = workerB.getId();
        Scheduler.INSTANCE.registerWorker(workerB);

        // add job A to scheduler, needs model chain A
        jobA = new Job();
        jobA.setModelChain(chainA);
        jobIdA = jobA.getId();
        Scheduler.INSTANCE.addJob(jobA);

        // add job B to scheduler, needs model chain B
        jobB = new Job();
        jobB.setModelChain(chainB);
        jobIdB = jobB.getId();
        Scheduler.INSTANCE.addJob(jobB);
    }


    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testRegisterWorker() {
        // create a new default worker
        WorkerInfo workerInfo = new WorkerInfo();
        String workerId = workerInfo.getId();

        // new worker should not already exist
        assertNull(Scheduler.INSTANCE.getWorker(workerId));

        // register the new worker
        Scheduler.INSTANCE.registerWorker(workerInfo);

        // now the new worker should be known
        assertNotNull(Scheduler.INSTANCE.getWorker(workerId));
        assertEquals(workerInfo, Scheduler.INSTANCE.getWorker(workerId));
    }


    @Test
    public void testUnregisterWorker() {
        // create a new default worker
        WorkerInfo workerInfo = new WorkerInfo();
        String workerId = workerInfo.getId();

        // register the new worker
        Scheduler.INSTANCE.registerWorker(workerInfo);

        // now the worker should be known
        assertNotNull(Scheduler.INSTANCE.getWorker(workerId));

        // unregister the worker
        Scheduler.INSTANCE.unregisterWorker(workerId);

        // now the worker should no longer exist
        assertNull(Scheduler.INSTANCE.getWorker(workerId));
    }


    @Test
    public void testGetJobForWorker() {
        // do some scheduling
        Scheduler.INSTANCE.updateWorkerState(workerIdA, WorkerState.IDLE);
        Scheduler.INSTANCE.scheduleJobs();

        // should have assigned job A to worker A
        Job scheduledJob = Scheduler.INSTANCE.getJobForWorker(workerIdA);
        assertNotNull(scheduledJob);
        assertEquals(jobA, scheduledJob);
    }


    @Test
    public void addJob() {
        // create some model chain info to use
        ModelChainInfo chain = new ModelChainInfo();

        // add new job to scheduler
        Job job = new Job();
        job.setModelChain(chain);
        String jobId = job.getId();
        Scheduler.INSTANCE.addJob(job);

        // job should now be known and proper state set
        Job queuedJob = Scheduler.INSTANCE.getJob(jobId);
        assertNotNull(queuedJob);
        assertEquals(JobState.WAITING_UNSCHEDULED, queuedJob.getState());
    }


    @Test
    public void testUpdateJobState() {
        // state must have been updated
        assertEquals(JobState.WAITING_UNSCHEDULED, jobA.getState());

        // should cause an exception
        try {
            Scheduler.INSTANCE.updateJobState(jobIdA, JobState.WAITING_SCHEDULED);
            fail();
        } catch (Exception e) {
            // ok
        }

        // should be possible, job stays in queue
        Scheduler.INSTANCE.updateJobState(jobIdA, JobState.IN_PROGRESS);
        assertEquals(JobState.IN_PROGRESS, jobA.getState());
        assertNotNull(Scheduler.INSTANCE.getJobFromQueue(jobIdA));
        assertNull(Scheduler.INSTANCE.getJobFromHistory(jobIdA));

        // should be possible, job moves to history
        Scheduler.INSTANCE.updateJobState(jobIdA, JobState.COMPLETED_OK);
        assertEquals(JobState.COMPLETED_OK, jobA.getState());
        assertNull(Scheduler.INSTANCE.getJobFromQueue(jobIdA));
        assertNotNull(Scheduler.INSTANCE.getJobFromHistory(jobIdA));
    }


    @Test
    public void testUpdateWorkerState() {
        // state must have been set to unknown by the scheduler
        assertEquals(WorkerState.UNKNOWN, workerA.getState());

        // should cause an exception
        try {
            Scheduler.INSTANCE.updateWorkerState(workerIdA, WorkerState.REMOVED);
            fail();
        } catch (Exception e) {
            // ok
        }

        // should be possible
        Scheduler.INSTANCE.updateWorkerState(workerIdA, WorkerState.BUSY);
        assertEquals(WorkerState.BUSY, workerA.getState());
    }


    @Test
    public void testScheduleJobs() {
        // do some scheduling
        Scheduler.INSTANCE.updateWorkerState(workerIdA, WorkerState.IDLE);
        Scheduler.INSTANCE.updateWorkerState(workerIdB, WorkerState.IDLE);
        Scheduler.INSTANCE.scheduleJobs();

        // should have assigned jobs to workers
        Job scheduledJobWorkerA = Scheduler.INSTANCE.getJobForWorker(workerIdA);
        assertNotNull(scheduledJobWorkerA);
        assertEquals(jobA, scheduledJobWorkerA);
        Job scheduledJobWorkerB = Scheduler.INSTANCE.getJobForWorker(workerIdB);
        assertNotNull(scheduledJobWorkerB);
        assertEquals(jobB, scheduledJobWorkerB);
    }


    @Test
    public void testAbortedWorkerScheduling() {
        Scheduler.INSTANCE.updateWorkerState(workerIdA, WorkerState.IDLE);
        Scheduler.INSTANCE.updateWorkerState(workerIdB, WorkerState.IDLE);
        Scheduler.INSTANCE.updateJobState(jobIdB, JobState.ABORTED);
        Scheduler.INSTANCE.scheduleJobs();

        // Job B should be assigned to worker A, because it gets priority
        Job scheduledJobWorkerA = Scheduler.INSTANCE.getJobForWorker(workerIdA);
        assertNotNull(scheduledJobWorkerA);
        assertEquals(jobB, scheduledJobWorkerA);

        // Worker B should have nothing to do
        Job scheduledJobWorkerB = Scheduler.INSTANCE.getJobForWorker(workerIdB);
        assertNull(scheduledJobWorkerB);

        // Job A should remain waiting
        assertEquals(JobState.WAITING_UNSCHEDULED, jobA.getState());
    }


    @Test
    public void testSchedulingThread() {
        // start the scheduling thread
        Scheduler.INSTANCE.start();

        // wait a while
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // stop the scheduling thread
        Scheduler.INSTANCE.stop();

        // wait another while to allow the thread to finish
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TODO add some checks
    }



    @Test
    public void testSaveState() {
        String expected = "";
        System.out.println(Scheduler.INSTANCE.toXml().asXML());
        assertEquals(expected, Scheduler.INSTANCE.toXml().asXML());
    }

    @Test
    public void testRestoreState() {
//        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<queue><experiment id=\"1\" state=\"Waiting\">Sample Experiment 1</experiment><experiment id=\"2\" state=\"Waiting\">Sample Experiment 2</experiment><experiment id=\"3\" state=\"Waiting\">Sample Experiment 3</experiment></queue>";
//        queue.restoreState(expected);
//        assertEquals(expected, queue.createState());
        fail();
    }

}
