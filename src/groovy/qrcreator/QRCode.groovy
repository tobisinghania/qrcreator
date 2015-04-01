/*
 * Copyright 2008 ZXing authors
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
package qrcreator

import groovy.transform.CompileStatic

/**
 * @author satorux@google.com (Satoru Takabayashi) - creator
 * @author dswitkin@google.com (Daniel Switkin) - ported from C++
 *
 * Modified for Groovy compatibility
 * @author Tobias Singhania
 */
@CompileStatic
class QRCode {

    static final int NUM_MASK_PATTERNS = 8

    Mode mode
    ErrorCorrectionLevel ECLevel
    Version version
    int maskPattern = -1
    ByteMatrix matrix

    @Override
    String toString() {
        StringBuilder result = new StringBuilder(200)
        result << "<<\n"
        result << " mode: " << mode
        result << "\n ecLevel: " << ECLevel
        result << "\n version: " << version
        result << "\n maskPattern: " << maskPattern
        if (matrix) {
            result << "\n matrix:\n" << matrix
        } else {
            result << "\n matrix: null\n"
        }
        result << ">>\n"
        return result
    }

    // Check if "mask_pattern" is valid.
    static boolean isValidMaskPattern(int maskPattern) {
        return maskPattern >= 0 && maskPattern < NUM_MASK_PATTERNS
    }
}
