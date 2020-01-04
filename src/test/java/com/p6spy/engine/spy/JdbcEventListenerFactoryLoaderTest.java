/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2020 P6Spy
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
package com.p6spy.engine.spy;

import com.p6spy.engine.event.JdbcEventListener;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;


public class JdbcEventListenerFactoryLoaderTest {

  @Test
  public void testLoadViaServiceLoader() {
    assertThat(JdbcEventListenerFactoryLoader.load(), instanceOf(TestJdbcEventListenerFactory.class));
  }

  /**
   * This just wraps the {@link DefaultJdbcEventListenerFactory} as the other tests would fail otherwise.
   * The {@link JdbcEventListenerFactory} gets registered in META-INF/services/com.p6spy.engine.spy.JdbcEventListenerFactory
   * and is used for all unit tests.
   */
  public static class TestJdbcEventListenerFactory implements JdbcEventListenerFactory {

    private final DefaultJdbcEventListenerFactory factory = new DefaultJdbcEventListenerFactory();

    @Override
    public JdbcEventListener createJdbcEventListener() {
      return factory.createJdbcEventListener();
    }
  }
}
