/*
 * seamframe: Properties.java
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

package org.seamless_ip.core.utilities.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.seamless_if.processing.sofa.SeamException;

/**
 * A properties class that stored data in a file.
 * This configurations are used to abstract paths and file names.
 *
 * @author david
 * @author Hongtao
 */
public class Properties {


    static Logger logger = Logger.getLogger(Properties.class);

    private static final String BUNDLE_NAME = "config.properties";

    /**
     * all the loaded data
     */
    private final Map<String, String> data = Collections.synchronizedMap(new TreeMap<String, String>());

    /**
     * the singleton (only instance of the properties
     */
    private static Properties singleton;

    /**
     * the file contining the properties
     */
    private String file;

    /**
     * save the current data to the file
     */
    public static void save() {
        singleton.saveData();
    }

    /**
     * access an element of the map
     */
    public static String getString(String key) {
        initSingleton();
        if (!singleton.data.containsKey(key)) {

        }
        String value = singleton.data.get(key);
        if (value == null) {
            System.out.println("VALUE for " + key + " IS NULL");
            value = "";
        }
        return value;
    }

    /**
     * set an element of the map
     */
    public static void setString(String key, String value) {
        initSingleton();
        singleton.data.put(key, value);
    }


    /**
     * save the current data to the disk
     */
    private void saveData() {
        FileWriter writer = null;
        try {
            File file = new File(this.file);
            System.out.println(file.delete());

            writer = new FileWriter(this.file);
            for (String key : data.keySet()) {
                writer.append(key);
                writer.append("=");
                writer.append(data.get(key));
                writer.append("\n");
            }
        } catch (IOException e) {
            // Warn?
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
            }
        }
    }


    /**
     * Load the key and values from the file
     */
    private synchronized void loadData() {
        if (!fileExists()) return;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                int pos = line.indexOf("=");
                String key = line.substring(0, pos);
                String value = line.substring(pos + 1);
                data.put(key, value);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            throw new SeamException(ex);
        }
    }

    public static void reloadResource() {
        singleton.loadData();
    }

    private Properties(String filename) {
        this.file = filename;
        ensureContent();
        loadData();
    }

    /**
     * make sure that some keys are in the map and assign default values
     */
    private void ensureContent() {
        data.put("apes.path", "data\\apesData");
        data.put("apes.model", "apes.xml");
        data.put("apes.data", "org\\seamless\\data\\apesData");
        data.put("capri.path", "PATH TO CAPRI");
        data.put("capri.model", "CAPMOD.gms");
        data.put("capri.editor.scenario", "capri_scenario.gms");
        data.put("fssim.model", "FSSIM-Global.gms");
        data.put("fssim.path", "data\\fssim\\");
        data.put("fssim.inputs", "fssim-dm\\INPUTDATA");
        data.put("gams.exe", "gams.exe");
        data.put("gams.path", "PATH OF GAMS");
        data.put("modcom.path", "data\\apesData");
        data.put("modcom.exe", "mrun.exe");
        data.put("gams.license", "absolute path to gams license");
        data.put("expamod.path", "data\\expamod");
        data.put("expamod.model", "expamod.gms");
    }

    /**
     * check if the file exists
     */
    private boolean fileExists() {
        File file = new File(this.file);
        return file.exists();
    }

    /**
     * initialize the signleton and show the editor if new
     */
    private static void initSingleton() {
        initSingleton(true);
    }

    /**
     * package access method to initialize the singleton. This method is used directly by the
     * ConfigEditor.
     *
     * @param show
     */
    static synchronized void initSingleton(boolean show) {
        if (singleton == null) {

            singleton = new Properties(BUNDLE_NAME);
            if (!singleton.fileExists()) {
                singleton.saveData();
                if (show)
                    JOptionPane.showMessageDialog(null, "Created a default configuration file. Will run the Config editor.");
                // if (show) ConfigEditor.showEditor();
            }
        }
    }


    public static Enumeration<String> keys() {
        initSingleton();
	        return Collections.enumeration(singleton.data.keySet());
	    }
	}

