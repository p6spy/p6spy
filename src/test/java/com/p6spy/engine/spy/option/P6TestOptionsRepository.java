/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
 *
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
 */

package com.p6spy.engine.spy.option;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.p6spy.engine.spy.appender.CustomLineFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import com.p6spy.engine.spy.appender.MultiLineFormat;
import com.p6spy.engine.spy.appender.SingleLineFormat;
import com.p6spy.engine.test.BaseTestCase;

public class P6TestOptionsRepository extends BaseTestCase {

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
    Assert.assertTrue(optRepo.parse(MessageFormattingStrategy.class,
        CustomLineFormat.class.getName()) instanceof MessageFormattingStrategy);
    Assert.assertTrue(optRepo.parse(Pattern.class,
        "somepattern") instanceof Pattern);

    // existing categories work
    Assert.assertTrue(optRepo.parse(Category.class,
            "info") instanceof Category);
    // new categories can be added without restriction
    Assert.assertTrue(optRepo.parse(Category.class,
            "new_category") instanceof Category);
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

  @Test(expected = IllegalArgumentException.class)
  public void testSetSetForDeprecatedMinusPrefixOnFirstValueFails() {
	  optRepo.setSet(String.class, "option1", "-value1,value2");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testSetSetForDeprecatedMinusPrefixOnNextValueFails() {
	  optRepo.setSet(String.class, "option1", "value1,-value2");
  }
  
  @Test
  public void testSetSetOverride() {
	  optRepo.initCompleted();

    optRepo.setSet(String.class, "option1", "value1,value2");
    Assert.assertEquals(new HashSet<String>(Arrays.asList("value1", "value2")),
        optRepo.get(String.class, "option1"));

    optRepo.setSet(String.class, "option1", "value3,value4");
    Assert.assertEquals(new HashSet<String>(Arrays.asList("value3", "value4")),
        optRepo.get(String.class, "option1"));
  }
  
  @Test
  public void testSetNullDoesNotModifyValue() {
    optRepo.initCompleted();

    optRepo.set(String.class, "option1", "foo");
    Assert.assertEquals("foo", optRepo.get(String.class, "option1"));
    
    optRepo.set(String.class, "option1", null);
    Assert.assertEquals("foo", optRepo.get(String.class, "option1"));
  }
  
  @Test
  public void testSetSetEmptyStringNullsValue() {
    optRepo.initCompleted();

    optRepo.setSet(String.class, "option1", "foo");
    Assert.assertEquals(new HashSet<String>(Arrays.asList("foo")), 
    		optRepo.get(String.class, "option1"));
    
    optRepo.setSet(String.class, "option1", "");
    Assert.assertNull(optRepo.get(String.class, "option1"));
  }
  
  @Test
  public void testSetReturnsTrueForNonNull() {
    Assert.assertTrue(optRepo.set(String.class, "option1", "foo"));
  }
  
  @Test
  public void testSetReturnsFalseForNull() {
    Assert.assertFalse(optRepo.set(String.class, "option1", null));
  }
  
  @Test
  public void testUnSetUsesDefaultForNullValue() {
    optRepo.initCompleted();

    optRepo.set(String.class, "option1", "foo");
    Assert.assertEquals("foo", optRepo.get(String.class, "option1"));
    
    optRepo.setOrUnSet(String.class, "option1", null, "default");
    Assert.assertEquals("default", optRepo.get(String.class, "option1"));
  }
  
  @Test
  public void testUnSetIgnoresDefaultForValueNotNull() {
    optRepo.initCompleted();

    optRepo.set(String.class, "option1", "foo");
    Assert.assertEquals("foo", optRepo.get(String.class, "option1"));
    
    optRepo.setOrUnSet(String.class, "option1", "bar", "default");
    Assert.assertEquals("bar", optRepo.get(String.class, "option1"));
  }
  
  @Test
  public void testUnSetSetsToNullForDefaultAndValueNull() {
    optRepo.initCompleted();

    optRepo.set(String.class, "option1", "foo");
    Assert.assertEquals("foo", optRepo.get(String.class, "option1"));
    
    optRepo.setOrUnSet(String.class, "option1", null, null);
    Assert.assertNull(optRepo.get(String.class, "option1"));
  }
}
