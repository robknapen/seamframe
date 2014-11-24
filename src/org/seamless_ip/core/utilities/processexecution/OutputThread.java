/*
 * seamframe: OutputThread.java
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

import nl.alterra.openmi.sdk.backbone.Event;
import nl.alterra.openmi.sdk.backbone.Publisher;

import org.apache.log4j.Logger;
import org.seamless_if.processing.sofa.SeamException;

/**
 * Thread class for handling the output of a process started by class RunEngine.
 *
 * @author Unknown; modifications by Roelof Oomen
 */
public class OutputThread extends Thread {

    private static final Logger logger = Logger.getLogger(OutputThread.class);
    private final BufferedReader reader;
    private final Publisher publisher;
    private final String filter;

    public OutputThread(BufferedReader reader, Publisher publisher, String filter) {
        super();
        this.reader = reader;
        this.publisher = publisher;
        this.filter = filter;
    }

    @Override
    public void run() {
        try {
            String line = reader.readLine();
            while (line != null) {
                if (publisher != null) {
                    synchronized (publisher) {
                        if (filter != null && line.startsWith(filter)) {
                            publisher.sendEvent(new Event(Event.EventType.TimeStepProgress, line));
                        }
                    }
                }
                line = reader.readLine();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SeamException(e);
        }
    }
}
