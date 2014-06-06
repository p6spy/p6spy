/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2014 P6Spy
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
package com.p6spy.engine.common;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

public class P6UtilTest {

  @Test 
  public void joinNullSafeTest() {
    Assert.assertEquals("", P6Util.joinNullSafe(null, null));
    Assert.assertEquals("", P6Util.joinNullSafe(null, ""));
    Assert.assertEquals("", P6Util.joinNullSafe(null, ","));
    Assert.assertEquals("", P6Util.joinNullSafe(Collections.<String>emptyList(), null));
    Assert.assertEquals("", P6Util.joinNullSafe(Collections.<String>emptyList(), ","));
    Assert.assertEquals("foo", P6Util.joinNullSafe(Arrays.asList("foo"), ","));
    Assert.assertEquals("foobar", P6Util.joinNullSafe(Arrays.asList("foo", "bar"), null));
    Assert.assertEquals("foo,bar", P6Util.joinNullSafe(Arrays.asList("foo", "bar"), ","));
    Assert.assertEquals("foo|bar|aaa", P6Util.joinNullSafe(Arrays.asList("foo", "bar", "aaa"), "|"));
  }
}
