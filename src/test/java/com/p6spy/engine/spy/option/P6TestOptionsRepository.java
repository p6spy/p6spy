/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2013 P6Spy
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.p6spy.engine.spy.option;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import com.p6spy.engine.spy.appender.MultiLineFormat;
import com.p6spy.engine.spy.appender.SingleLineFormat;

public class P6TestOptionsRepository {

  private P6OptionsRepository optRepo;

  @Before
  public void setUp() {
    optRepo = new P6OptionsRepository();
  }

  @Test
  public void testParse() {
    Assert.assertEquals("", optRepo.parse(String.class, ""));
    Assert.assertEquals(100, optRepo.parse(Integer.class, 100));
    Assert.assertEquals(100L, optRepo.parse(Long.class, 100L));
    Assert.assertEquals(true, optRepo.parse(Boolean.class, "true"));
    Assert.assertTrue(optRepo.parse(MessageFormattingStrategy.class,
        SingleLineFormat.class.getName()) instanceof MessageFormattingStrategy);
    Assert.assertTrue(optRepo.parse(MessageFormattingStrategy.class,
        MultiLineFormat.class.getName()) instanceof MessageFormattingStrategy);
    Assert.assertTrue(optRepo.parse(Pattern.class,
        "somepattern") instanceof Pattern);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseForCollectionFails() {
    optRepo.parse(Set.class, new HashSet<String>());
    Assert.fail();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseForListFails() {
    optRepo.parse(Set.class, new HashSet<String>());
    Assert.fail();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseForSetFails() {
    optRepo.parse(Set.class, new HashSet<String>());
    Assert.fail();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetPrioToInitCompletedFails() {
    optRepo.set(String.class, "option1", "");
    Assert.assertEquals("", optRepo.get(String.class, "option1"));
  }

  @Test
  public void testSetGetSimple() {
    optRepo.initCompleted();

    optRepo.set(String.class, "option1", "value1");
    Assert.assertEquals("value1", optRepo.get(String.class, "option1"));
  }

  @Test
  public void testSetSetAdditionAndRemoval() {
    optRepo.initCompleted();

    optRepo.setSet(String.class, "option1", "value1,value2");
    Assert.assertEquals(new HashSet<String>(Arrays.asList("value1", "value2")),
        optRepo.get(String.class, "option1"));

    optRepo.setSet(String.class, "option1", "value3,-value2");
    Assert.assertEquals(new HashSet<String>(Arrays.asList("value1", "value3")),
        optRepo.get(String.class, "option1"));
  }
}
