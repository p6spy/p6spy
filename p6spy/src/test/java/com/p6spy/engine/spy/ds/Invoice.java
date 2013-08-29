package com.p6spy.engine.spy.ds;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;

@Entity
public class Invoice implements Serializable
{
   private Long id;

   private BigDecimal amount;

   private Long customerId;

   private Date date;

   public Invoice() {}

   public Invoice(Long customerId, BigDecimal amount, Date date)
   {
      this.customerId = customerId;
      this.amount = amount;
      this.date = date;
   }

   @Id
   @GeneratedValue
   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   @Max(100)
   public BigDecimal getAmount()
   {
      return amount;
   }

   public void setAmount(BigDecimal amount)
   {
      this.amount = amount;
   }

   public Long getCustomerId()
   {
      return customerId;
   }

   public void setCustomerId(Long customerId)
   {
      this.customerId = customerId;
   }

   @Temporal(TemporalType.DATE)
   public Date getDate()
   {
      return date;
   }

   public void setDate(Date date)
   {
      this.date = date;
   }
}
