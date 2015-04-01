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
 * JAVAPORT: The original code was a 2D array of ints, but since it only ever gets assigned
 * -1, 0, and 1, I'm going to use less memory and go with bytes.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 *
 * Modified for Groovy compatibility
 * @author Tobias Singhania
 */
@CompileStatic
class ByteMatrix {

    /**
     * an internal representation as bytes, in row-major order. array[y][x] represents point (x,y)
     */
    final byte[][] array
    final int width
    final int height

    ByteMatrix(int width, int height) {
        array = new byte[height][width]
        this.width = width
        this.height = height
    }

    byte get(int x, int y) {
        return array[y][x]
    }

    void set(int x, int y, byte value) {
        array[y][x] = value
    }

    void set(int x, int y, int value) {
        array[y][x] = (byte) value
    }

    void set(int x, int y, boolean value) {
        array[y][x] = (byte) (value ? 1 : 0)
    }

    void clear(byte value) {
        height.times { int y ->
            width.times { int x ->
                array[y][x] = value
            }
        }
    }

    @Override
    String toString() {
        StringBuilder result = new StringBuilder(2 * width * height + 2)
        height.times { int y ->
            width.times { int x ->
                switch (array[y][x]) {
                    case 0:  result << " 0"; break
                    case 1:  result << " 1"; break
                    default: result << "  "
                }
            }
            result << '\n'
        }
        return result
    }
}
