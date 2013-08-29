package com.p6spy.engine.spy.ds;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class GameRepository extends AbstractRepository<Game> {
  @Resource
  private EJBContext ejbContext;

  @PersistenceContext(unitName = "c")
  private EntityManager em;

  @Override
  public EntityManager getPersistenceContext() {
    return em;
  }

  @Override
  public String getEntityName() {
    return Game.class.getSimpleName();
  }

  @TransactionAttribute(TransactionAttributeType.NEVER)
  public void succeedFirstFailSecondWithoutTx()
  {
    create(new Game("Super Mario Brothers"), true);
  }
  
  public void insertThenRollbackInTx()
  {
     create(new Game("Super Mario Brothers"), true);
     ejbContext.setRollbackOnly();
  }
}
