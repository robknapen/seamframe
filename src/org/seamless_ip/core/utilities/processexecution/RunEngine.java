/*
 * seamframe: RunEngine.java
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

package org.seamless_ip.core.utilities.processexecution;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;

import nl.alterra.openmi.sdk.backbone.Publisher;

import org.apache.log4j.Logger;

/**
 * Executes a native process.
 *
 * @author Unkown; modifications by Roelof Oomen
 */
public class RunEngine {

    private String lastCommand = "";

    /**
     * the location where the outputs can be stored
     */
    protected String outputDir;

    /**
     * The logger
     */
    protected final Logger logger;

    /**
     * The process of the executed engine
     */
    protected Process process;

    /**
     * String with the last executed command line
     */
    public String lastCommand() {
        return lastCommand;
    }

    /**
     * the default constructor
     */
    public RunEngine() {
        logger = Logger.getLogger(this.getClass());
    }

    /**
     * executes a native process using the specified dir and arguments
     *
     * @param dir
     * @param exe
     * @param attribs
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public int executeProcess(String dir, String exe, String... attribs) throws IOException, InterruptedException {
        return executeProcess(dir, exe, new LinkedList<String>(Arrays.asList(attribs)));

    }

    public int executeProcess(String dir, String exe, LinkedList<String> command) throws IOException, InterruptedException {
        return executeProcess(dir, exe, command, null, "");
    }

    /**
     * Executes a process, and returns when it has finished.
     *
     * @param dir
     * @param exe
     * @param params
     * @param publisher
     * @param filter
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public int executeProcess(String dir, String exe, LinkedList<String> params, final Publisher publisher, String filter)
            throws IOException, InterruptedException {

        executeProcessAsynchronous(dir, exe, params, publisher, filter);

        return waitFor();
    }

    /**
     * Executes a process, but does not wait for it to finish.
     *
     * @param dir
     * @param exe
     * @param params
     * @param publisher
     * @param filter
     * @throws IOException
     * @throws InterruptedException
     */
    public void executeProcessAsynchronous(String dir, String exe, LinkedList<String> params, final Publisher publisher, String filter)
            throws IOException, InterruptedException {

        // Save and log what we are about to start
        lastCommand = createCommandLine(exe, params);
        logger.info("Executing Command: " + lastCommand);

        // Now create a new process
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(new File(dir));
        params.addFirst(exe);
        pb.command(params);
        process = pb.start();

        BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
        new OutputThread(out, publisher, filter).start();

        out = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        new OutputThread(out, publisher, filter).start();
    }

    /**
     * @param params
     */
    public String createCommandLine(String exe, LinkedList<String> params) {
        // Make one string of our program and arguments
        String commandLine = exe + ' ';
        for (String s : params) {
            commandLine += '\"' + s + "\" "; // Quote commands as they can be directory names containing spaces
        }
        return commandLine;
    }

    public int waitFor() throws InterruptedException {
        if (process != null)
            return process.waitFor();
        else
            return -1;
    }

    /**
     * modify the process
     */
    protected void setEngine(Process process) {
		this.process = process;
	}
	
	public void stopEngine() {
		this.process.destroy();
	}
}
