package com.p6spy.engine.spy.ds;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class InvoiceRepository extends AbstractRepository<Invoice>
{
   @PersistenceContext(unitName = "d")
   private EntityManager em;

   @Override
   public EntityManager getPersistenceContext()
   {
      return em;
   }

   @Override
   public String getEntityName()
   {
      return Invoice.class.getSimpleName();
   }
}
