package com.p6spy.engine.spy.ds;

import javax.persistence.EntityManager;

public abstract class AbstractRepository<T>
{
   public void create(T entity)
   {
      create(entity, false);
   }
   
   public void create(T entity, boolean flush)
   {
      getPersistenceContext().persist(entity);
      if (flush)
      {
         getPersistenceContext().flush();
      }
   }
   
   public long getRecordCount()
   {
      return (Long) getPersistenceContext().createQuery("select count(e) from " + getEntityName() + " e").getSingleResult();
   }
   
   public void purge()
   {
      getPersistenceContext().createQuery("delete from " + getEntityName()).executeUpdate();
   }
   
   public abstract EntityManager getPersistenceContext();
   public abstract String getEntityName();
}
