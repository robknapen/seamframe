/*
 * seamframe: GamsModelRunner.java
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

import java.io.File;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * Running a gams model.
 *
 * @author Benny Jonsson
 */
public class GamsModelRunner {
    Logger logger = Logger.getLogger(this.getClass());
    private RunEngine runGAMS;
    private String modelReturnMessage = "";

    public GamsModelRunner() {
        runGAMS = new RunEngine();
    }

    /**
     * @param model_name  The name of the model (used by the org.apache.log4j.Logger)
     * @param model_path  The path to the gams model the directory where the model execute from (c:\gamsModels\theModel\)
     * @param gams_path   The path to gams (C:\Program Files\GAMS22.x\gams.exe)
     * @param model       The name of the model (myModel.gms)
     * @param gams_pram   Gams parameters
     * @param model_param Model parameters
     * @return returns 0 on normal compelation 1-10 on fail
     * @throws Exception
     * @author Benny Jonsson
     */
    public int runGamsModel(String model_name, String model_path, String gams_path,
                            String model, LinkedList<String> gams_pram,
                            LinkedList<String> model_param) throws Exception {
        int gamsReturnCode = -1;

        logger.debug(model_name);

        if (!new File(model_path).exists()) throw new Exception("model_path: " + model_path + " does not exists!");
        if (!new File(gams_path).exists()) throw new Exception("gams_path: " + gams_path + " does not exists!");

        logger.info(String.format("Start to run %s  model in GAMS:", model_name));
        try {

            LinkedList<String> command = new LinkedList<String>();
            command.add(model);

            if (gams_pram != null) command.addAll(gams_pram);
            if (model_param != null) command.addAll(model_param);

            logger.debug("--**-- runGamsModel --**--");
            logger.debug(model_path);
            logger.debug(gams_path);
            logger.debug(command);
            logger.debug("--**-- runGamsModel --**--");


            // createExequteBatFile(model_path, gams_path, command);

            gamsReturnCode = runGAMS.executeProcess(
                    model_path,
                    gams_path,
                    command);


            modelReturnMessage = GAMSReturnCode(gamsReturnCode) + " " + model_name;

            logger.debug(String.format("Finnished gams model with return code: %s", modelReturnMessage));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return gamsReturnCode;
    }

/*	private void createExequteBatFile(String model_path, String gams_path, LinkedList<String> command) {
        try {
            FileInputStream fileInputStream = new FileInputStream("C:\\Documents and Settings\\benny\\seamless\\Seamframe\\theZip.zip");
            StringBuffer stringBuffer = new StringBuffer();
            byte[] buffer = new byte[4096];
            try {
                while ((fileInputStream.read(buffer)) > 0) {
                    stringBuffer.append(Arrays.toString(buffer));
                }
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
//			aE.setModelZip(stringBuffer.toString());
//
//			components.dm.persist(aE);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
        }

    }*/

    /**
     * @param returnCode GAMS return code
     * @return return a description of GAMS return code
     * @author Benny Jonsson
     */
    public static String GAMSReturnCode(int returnCode) {
        String retString = "GAMS exec error " + returnCode + " ";
        switch (returnCode) {
            case 0:
                retString = "Normal completion of GAMS model";
                break;
            case 1:
                retString += "solver is to be called (the system should never return this number)";
                break;
            case 2:
                retString += "there was a compilation error";
                break;
            case 3:
                retString += "there was an execution error";
                break;
            case 4:
                retString += "system limits were reached";
                break;
            case 5:
                retString += "there was a file error";
                break;
            case 6:
                retString += "there was a parameter error";
                break;
            case 7:
                retString += "there was a licensing error";
                break;
            case 8:
                retString += "there was a GAMS system error";
                break;
            case 9:
                retString += "GAMS could not be started";
                break;
            case 10:
                retString += "user interrupt";
                break;
            default:
                retString += "not a defined GAMS CODE";
                break;
        }
        return retString;
    }

    /**
     * @return String describing model return
     * @author Benny Jonsson
     */
    public String getModelReturnMessage() {
		return modelReturnMessage;
	}

}
