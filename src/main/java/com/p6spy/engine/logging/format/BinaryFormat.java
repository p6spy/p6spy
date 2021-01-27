/**
 * P6Spy
 *
 * Copyright (C) 2002 P6Spy
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
package com.p6spy.engine.logging.format;

public interface BinaryFormat {
  /**
   * Transforms the supplied binary data to a string representation.
   * Wraps the value in quotes if the database dialect requires them.
   * 
   * @param input
   *    the binary data input value to convert to {@link String}
   * @return
   *    the {@link String} representation of the given bytes
   */
  public String toString(byte[] input);
}
