package com.p6spy.engine.spy.ds;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class BookRepository extends AbstractRepository<Book> {
  @Resource
  private EJBContext ejbContext;

  @PersistenceContext(unitName = "a")
  private EntityManager em;

  @Override
  public EntityManager getPersistenceContext() {
    return em;
  }

  @Override
  public String getEntityName() {
    return Book.class.getSimpleName();
  }

  public void insertThenRollbackInTx()
  {
     create(new Book("Book 1"), true);
     ejbContext.setRollbackOnly();
  }
  
  public void insertInTx()
  {
     create(new Book("Book 1"), true);
  }
}
