/*
 * seamframe: IDomainManager.java
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

import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

/**
 * A Domain Manager acts as an intermediate layer between the persistence layer
 * and the code implementation. It hides the entity management and provides
 * CRUD methods (create, retrieve, update and delete) for accessing persistent
 * Entity Beans and a querying facility.
 * <p/>
 * It can be used both in a J2SE and J2EE environments.
 *
 * @author Ioannis N. Athanasiadis, Benny Jonsson, Michiel Rop, Rob Knapen
 */
public interface IDomainManager {

    /**
     * Stores a new object in the persistent storage. For transient objects use
     * the {@update} method
     *
     * @param t the object to be persisted
     * @throws Exception
     */
    public abstract <T> void persist(T t);

    /**
     * Creates a persistent object of a certain class type.
     *
     * @param type the class of the type
     * @return an object of type T
     * @throws Exception
     */
    public abstract <T> T create(Class<T> type);

    /**
     * Retrieves a persistent object of a certain type from its {@code id}.
     *
     * @param c  the class of the object (should be mapped to a table)
     * @param id the object id
     * @return persistent object of type T
     */
    public abstract <T> T retrieve(Class<T> c, Long id);

    /**
     * Update.
     *
     * @param object the object
     */
    public abstract <T> void update(T object);

    /**
     * Deletes an object from the persistent storage.
     *
     * @param object the object
     */
    public abstract <T> void delete(T object);

    /**
     * Returns all available persistent objects of a certain type.
     *
     * @param type the type
     * @return the all
     */
    public abstract <T> List<T> retrieveAll(Class<T> type);

    /**
     * A gateway to {@code javax.persistence.Query} for submitting JPQL (Java
     * Persistence Query language) queries.
     *
     * @param qlString the JPQL query string
     * @return Query instance for the specified qlString
     */
    public abstract Query query(String qlString);

    /**
     * A gateway to {@code javax.persistence.Query} for submitting native
     * SQL queries.
     *
     * @param sqlString the SQL query string
     * @return Query instance for the specified sqlString
     */
    public abstract Query sqlQuery(String sqlString);


    /**
     * Returns an EntityTransaction
     *
     * @return
     */
    public abstract EntityTransaction getTransaction();

    /**
     * Commit an EntityTransaction
     *
     * @param entityTransaction
     */
    public abstract void commitTransaction(EntityTransaction entityTransaction);


    /**
     * Update.
     *
     * @param object            the object
     * @param entityTransaction the transaction
     */
    public abstract <T> void update(T object, EntityTransaction entityTransaction);

    /**
     * Deletes an object from the persistent storage.
     *
     * @param object            the object
     * @param entityTransaction the transaction
     */
    public abstract <T> void delete(T object, EntityTransaction entityTransaction);

    /**
     * Stores a new object in the persistent storage. For transient objects use
     * the {@update} method
     *
     * @param t                 the object to be persisted
     * @param entityTransaction the transaction
     * @throws Exception
     */
    public abstract <T> void persist(T t, EntityTransaction entityTransaction);

    /**
     * Refresh the state of the instance from the database, overwriting changes made to the entity, if any.
     *
     * @param object the object to be refreshed
     */
    public <T> void refresh(T object);

    /**
     * delete a set of rows from a database table
     *
     * @param qlString The JPQL query string to delete records
     * @return number of rows deleted
     */
    public int executeDeleteQuery(String qlString);


    /**
     * Closes the domain manager and allows it to clean up resources. After
     * calling this method no other domain manager methods should be called.
     * Most likely they will result in exceptions.
     */
    public void close();
}