/*
 * seamframe: AbstractDomainManager.java
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

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import org.apache.log4j.Logger;
import org.seamless_if.processing.sofa.SeamException;


/**
 * Abstract base class to derive domain managers from.
 *
 * @author Ioannis N. Athanasiadis, Benny Jonsson, Rob Knapen
 */
public abstract class AbstractDomainManager implements IDomainManager {

    /**
     * The dm.
     */
    static protected IDomainManager dm;

    /**
     * The em.
     */
    protected EntityManager em;

    /**
     * The log.
     */
    private final static Logger logger = Logger.getLogger(AbstractDomainManager.class);


    /**
     * Gets the single instance of AbstractDomainManager.
     *
     * @return single instance of AbstractDomainManager
     */
    public static IDomainManager dm() {
        return getInstance();
    }


    /**
     * Gets the single instance of AbstractDomainManager.
     *
     * @return single instance of AbstractDomainManager
     */
    public static IDomainManager getInstance() {
        return dm;
    }


    /*
    * (non-Javadoc)
    *
    * @see org.seamless_ip.core.domain.DomainManager#create(java.lang.Class)
    */
    public <T> T create(Class<T> c) {
        T t;
        try {
            t = c.newInstance();
            persist(t);
            return t;
        } catch (Exception e) {
            logger.info(e);
        }
        return null;
    }


    /*
      * (non-Javadoc)
      *
      * @see org.seamless_ip.core.domain.DomainManager#getAll(java.lang.Class)
      */
    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> type) {
        return query("select object(o) from " + type.getName() + " as o")
                .getResultList();
    }


    /*
    * (non-Javadoc)
    *
    * @see org.seamless_ip.core.domain.DomainManager#retrieveAll(java.lang.Class)
    */
    @SuppressWarnings("unchecked")
    public <T> List<T> retrieveAll(Class<T> type) {
        return query("select object(o) from " + type.getName() + " as o")
                .getResultList();
    }


    public <T> void persist(T t, EntityTransaction entityTransaction) {
        try {
            em.persist(t);
        } catch (Exception ex) {
            logger.error("Persisting instance failed. Rolling back" + ex.getMessage());
            throw new SeamException(ex, "Persisting failed, Rolling back:" + ex.getMessage());
        }
    }


    /*
    * (non-Javadoc)
    *
    * @see org.seamless_ip.core.domain.DomainManager#persist(T)
    */
    public <T> void persist(T t) {
        EntityTransaction etx = em.getTransaction();
        etx.begin();
        try {
            em.persist(t);
            etx.commit();
        } catch
                (TransactionRequiredException ex) {
            logger.error("Persisting instance failed. Rolling back" + ex.getMessage());
            etx.rollback();
            throw new SeamException(ex, "Persisting failed, Rolling back, no transaction:" + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Persisting instance failed. Rolling back" + ex.getMessage());
            etx.rollback();
            throw new SeamException(ex, "Persisting failed, Rolling back:" + ex.getMessage());
        }
    }


    /*
    * (non-Javadoc)
    *
    * @see org.seamless_ip.core.domain.DomainManager#query(java.lang.String)
    */
    public Query query(String qlString) {
        return em.createQuery(qlString);
    }


    /*
    * (non-Javadoc)
    *
    * @see org.seamless_ip.core.domain.DomainManager#sqlQuery(java.lang.String)
    */
    public Query sqlQuery(String sqlString) {
        return em.createNativeQuery(sqlString);
    }


    public int executeDeleteQuery(String qlString) {
        EntityTransaction etx = em.getTransaction();
        etx.begin();
        int rowsAffected = 0;
        try {
            rowsAffected = em.createQuery(qlString).executeUpdate();
            etx.commit();
        } catch (Exception ex) {
            logger.error("Deleting instance(s) failed. Rolling back:" + ex.getMessage());
            etx.rollback();
            throw new SeamException(ex, "Deleting instance(s) failed. Rolling back:" + ex.getMessage());
        }
        return rowsAffected;
    }


    /*
    * (non-Javadoc)
    *
    * @see org.seamless_ip.core.domain.DomainManager#retrieve(java.lang.Class,
    *      java.lang.Long)
    */
    public <T> T retrieve(Class<T> c, Long id) {
        return em.find(c, id);
    }


    /*
      * (non-Javadoc)
      *
      * @see org.seamless_ip.core.domain.DomainManager#update(T)
      */
    public <T> void update(T object) {
        EntityTransaction etx = em.getTransaction();
        etx.begin();
        try {
            em.merge(object);
            etx.commit();
        } catch (Exception ex) {
            logger.error("Instance updating failed. Rolling back.");
            etx.rollback();
            throw new SeamException(ex, "Instance updating failed. Rolling back." + ex.getMessage());
        }
    }


    public void close() {
        logger.info("Calling the finalization for the EntityManager");
        em.close();
        dm = null;
    }


    /* (non-Javadoc)
    * @see java.lang.Object#finalize()
    */
    protected void finalize() {
        close();
    }


    public void commitTransaction(EntityTransaction entityTransaction) {
        try {
            entityTransaction.commit();
        } catch (Exception ex) {
            logger.error("Persisting instance failed. Rolling back");
            entityTransaction.rollback();
            throw new SeamException(ex, "Persisting instance failed. Rolling back." + ex.getMessage());
        }
    }


    public <T> void update(T object, EntityTransaction entityTransaction) {
        try {
            em.merge(object);
        } catch (Exception ex) {
            logger.error("Instance updating failed. Rolling back.");
            throw new SeamException(ex, "Instance updating failed. Rolling back." + ex.getMessage());
        }
    }


    public <T> void delete(T object, EntityTransaction entityTransaction) {
        em.remove(object);
    }


    /*
      * (non-Javadoc)
      *
      * @see org.seamless_ip.core.domain.DomainManager#delete(T)
      */
    public <T> void delete(T object) {
        EntityTransaction etx = em.getTransaction();
        etx.begin();
        try {
            em.remove(object);
            etx.commit();
        } catch (Exception ex) {
            logger.error("Instance removal failed. Rolling back." + ex.getMessage());
            etx.rollback();
            throw new SeamException(ex, "Instance removal failed. Rolling back." + ex.getMessage());
        }
    }


    public EntityTransaction getTransaction() {
        EntityTransaction etx = em.getTransaction();
        etx.begin();
        return etx;
    }


    /**
     * Refresh the state of the instance from the database, overwriting changes made to the entity, if any.
     *
     * @param object the object to be refreshed
     */
    public <T> void refresh(T object) {
        try {
            em.refresh(object);
        }
        catch (IllegalStateException ise) {
            logger.error("The entityManager has been closed!!!!");
        }
        catch (IllegalArgumentException iae) {
            logger.error("The entity " + object.toString() + " is not managed!!");
        }
        catch (EntityNotFoundException enfe) {
            logger.error(object.toString() + " no longer exists in database!!!");
        }
    }
}
