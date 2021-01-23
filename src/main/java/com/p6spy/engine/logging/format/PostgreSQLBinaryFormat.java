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

/**
 * Transforms binary data to PostgreSQL hex encoded strings, for example {@code \x8088BAD639F}
 * 
 * @see <a href="https://www.postgresql.org/docs/current/datatype-binary.html#id-1.5.7.12.9">PostgreSQL documentation</a>
 */
public class PostgreSQLBinaryFormat implements BinaryFormat {

  /**
   * Reserve space for the two prefix chars \ and x
   */
  private static final int PREFIX_LENGTH = 2;

  /**
   * The space needed for the opening and closing quote character.
   */
  private static final int QUOTE_COUNT = 2;

  @Override
  public String toString(byte[] input) {
    char[] result = new char[PREFIX_LENGTH + QUOTE_COUNT + input.length * 2];
    int i = 0;
    result[i++] = '\''; // opening quote
    result[i++] = '\\'; // PostgreSQL binary...
    result[i++] = 'x'; //  ...data prefix
    HexEncodedBinaryFormat.hexEncode(input, result, i);
    result[result.length - 1] = '\''; // closing quote
    return new String(result);
  }
}
