/*
 * seamframe: SeamException.java
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
package org.seamless_if.processing.sofa;


/**
 * Exception class for the Seamless processing environment and OpenMI
 * additions. Errors handled with exceptions in the models (components that
 * derive from SeamLinkableComponent) and model chains (based on SeamChain)
 * should use this SeamException class.
 *
 * @author Rob Knapen; Alterra, Wageningen UR, NL
 */
public class SeamException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    /**
     * Creates an instance with a nested exception.
     *
     * @param exception Nested exception
     */
    public SeamException(Exception exception) {
        super(exception);
    }

    /**
     * @param throwable
     */
    public SeamException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Creates an instance with a specific message.
     *
     * @param message of the exception
     */
    public SeamException(String message) {
        super(message);
    }


    /**
     * Creates an instance with a give message and nested exception.
     *
     * @param message   of the exception
     * @param exception Nested exception
     */
    public SeamException(String message, Exception exception) {
        super(message, exception);
    }


    /**
     * Creates an instance with a formatted string as message and a nested
     * exception.
     *
     * @param exception Nested exception
     * @param format    string
     * @param args      for the format string
     */
    public SeamException(Exception exception, String format, Object... args)
	{
		super(String.format(format, args), exception);
	}
	
}
