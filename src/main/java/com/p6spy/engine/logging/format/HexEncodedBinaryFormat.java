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
 * Transforms binary data to hex encoded strings.
 */
public class HexEncodedBinaryFormat implements BinaryFormat {
  private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
      'F' };

  /**
   * The space needed for the opening and closing quote character.
   */
  private static final int QUOTE_COUNT = 2;

  @Override
  public String toString(byte[] input) {
    char[] result = new char[QUOTE_COUNT + input.length * 2];
    int i = 0;
    result[i++] = '\''; // add opening quote
    hexEncode(input, result, i);
    result[result.length - 1] = '\''; // add closing quote
    return new String(result);
  }

  /**
   * Hex encodes the supplied input bytes to the supplied output array.
   * Writes two {@code char}s of output for every {@code byte} of input.
   * @param input the input array
   * @param output the output array
   * @param outputOffset the offset of the output array to start writing from
   */
  static void hexEncode(byte[] input, char[] output, int outputOffset) {
    int idx = outputOffset;
    for (byte b : input) {
      int temp = (int) b & 0xFF;
      output[idx++] = HEX_CHARS[temp / 16];
      output[idx++] = HEX_CHARS[temp % 16];
    }
  }
}
