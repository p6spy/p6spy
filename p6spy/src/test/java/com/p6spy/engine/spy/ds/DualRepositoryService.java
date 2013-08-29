package com.p6spy.engine.spy.ds;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless
public class DualRepositoryService
{
  
   @EJB
   private GameRepository gameRepository;
   
   @EJB
   private InvoiceRepository invoiceRepository;
   
   @Resource
   private EJBContext ejbContext;
   
   public void succeedFirstFailSecondInTx()
   {
      succeedFirstFailSecondWithoutTx();
   }
   
   @TransactionAttribute(TransactionAttributeType.NEVER)
   public void succeedFirstFailSecondWithoutTx()
   {
      performSucceedFirstFailSecond();
   }
   
   public void insertBothThenRollbackInTx()
   {
      gameRepository.create(new Game("Super Mario Brothers"), true);
      invoiceRepository.create(new Invoice(1L, new BigDecimal(50), new Date()), true);
      ejbContext.setRollbackOnly();
   }
   
   public long getGameCount()
   {
      return gameRepository.getRecordCount();
   }
   
   public long getInvoiceCount()
   {
      return invoiceRepository.getRecordCount();
   }
   
   public void purge()
   {
      gameRepository.purge();
      invoiceRepository.purge();
   }
   
   private void performSucceedFirstFailSecond()
   {
      gameRepository.create(new Game("Super Mario Brothers"), true);
      invoiceRepository.create(new Invoice(1L, new BigDecimal(200), new Date()), true);
   }
}
