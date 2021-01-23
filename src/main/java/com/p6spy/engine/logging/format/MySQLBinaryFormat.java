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
 * Transforms binary data to MySQL hex encoded strings, for example {@code 0x8088BAD639F}.
 * Such strings are not quoted when used in queries.
 * 
 * @see <a href="https://dev.mysql.com/doc/refman/5.6/en/hexadecimal-literals.html">MySQL documentation</a>
 */
public class MySQLBinaryFormat implements BinaryFormat {

  /**
   * Reserve space for the two prefix chars 0 and x
   */
  private static final int PREFIX_LENGTH = 2;

  @Override
  public String toString(byte[] input) {
    
    char[] result = new char[PREFIX_LENGTH + input.length * 2];
    int i = 0;
    result[i++] = '0';
    result[i++] = 'x';
    HexEncodedBinaryFormat.hexEncode(input, result, i);
    return new String(result);
  }
}
