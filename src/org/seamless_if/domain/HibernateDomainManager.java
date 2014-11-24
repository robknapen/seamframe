/*
 * seamframe: DomainManager.java
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
package org.seamless_if.domain;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Implementation of the IDomainManager based on using Hibernate as JPA
 * service provider. It derives from the AbstractDomainManager class and
 * is a singleton.
 *
 * @author Ioannis N. Athanasiadis, Benny Jonsson, Rob Knapen
 */
public class HibernateDomainManager extends AbstractDomainManager {

    private static Object mutex = new Object();
    private static EntityManagerFactory emf = null;

    private static EntityManagerFactory getFactory(String persistenceUnitName, Map<String, String> properties) {
        synchronized (mutex) {
            if (emf == null) {
                emf = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
            }
        }
        return emf;
    }
    

    /**
     * Instantiates a new domain manager.
     *
     * @param persistenceUnitName to access for the ORM mapping
     * @param properties Additional properties to use
     */
    @SuppressWarnings("unchecked")
    private HibernateDomainManager(String persistenceUnitName, Map properties) {
        em = HibernateDomainManager.getFactory(persistenceUnitName, properties).createEntityManager();
    }


    /**
     * Initialize the singleton instance for the specified persistence unit
     * and database properties. When needed the instance is created first,
     * should it already exist then initialisation is skipped. Returns the
     * IDomainManager interface to the instance.
     *
     * Use something like:
     *   persistenceUnitName: seamframe
     *   dbConnection: jdbc:postgresql://trac.seamless-ip.org/seamdbp5dev
     *   dbUserName: seamless
     *   dbPassword: your_password
     *
     * @param persistenceUnitName to access for the ORM mapping
     * @param dbConnection for the database to be used
     * @param dbUserName to use to connect to the database
     * @param dbPassword to use for connecting to the database
     * @return the IDomainManager interface to the initialised instance
     */
    @SuppressWarnings("unchecked")
    public static IDomainManager initialize(String persistenceUnitName, String dbConnection,
                                            String dbUserName, String dbPassword, boolean showSql) {
        if (dm == null) {
            Map properties = new HashMap();
            properties.put("hibernate.hbm2ddl.auto", "update");
            properties.put("hibernate.archive.autodetection", "class, hbm");
            properties.put("hibernate.connection.url", dbConnection);
            properties.put("hibernate.connection.username", dbUserName);
            properties.put("hibernate.connection.password", dbPassword);
            properties.put("hibernate.show_sql", Boolean.toString(showSql));

            // TODO figure out if we have to specify these:
            // <hibernate.dialect>org.hibernate.dialect.PostgreSQLDialect</hibernate.dialect>
            // <hibernate.connection.driver_class>org.postgresql.Driver</hibernate.connection.driver_class>

            dm = new HibernateDomainManager(persistenceUnitName, properties);
        }
        return dm;
    }


}