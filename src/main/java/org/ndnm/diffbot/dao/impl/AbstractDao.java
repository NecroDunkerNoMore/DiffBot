package org.ndnm.diffbot.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractDao<PK extends Serializable, T> {

    @PersistenceContext
    EntityManager entityManager;
    private final Class<T> persistentClass;


    @SuppressWarnings("unchecked")
    public AbstractDao() {
        this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }


    protected EntityManager getEntityManager() {
        return this.entityManager;
    }


    protected T getByKey(PK key) {
        return entityManager.find(persistentClass, key);
    }


    protected void persist(T entity) {
        entityManager.persist(entity);
    }


    protected void update(T entity) {
        entityManager.merge(entity);
    }


    protected void delete(T entity) {
        entityManager.remove(entity);
    }

}
