package com.wbillingsley.tutorials.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;

public class DataAccess {

    protected static EntityManagerFactory entityManagerFactory;
    protected static EntityManager entityManager;
	
	public static void initDataAccess(String persistenceUnitName) {
        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
        entityManager = entityManagerFactory.createEntityManager();		
	}
	
	public static EntityManager getEntityManager() {
		if (entityManager == null) {
			throw new IllegalStateException("DataAccess.initDataAccess has not been (successfully) called yet");
		}
		return entityManager;
	}
	
	public static CriteriaBuilder getCriteriaBuilder() {
		return getEntityManager().getCriteriaBuilder();
	}
	
	public static void shutDown() {
        entityManager.close();
        entityManagerFactory.close();		
	}
}
