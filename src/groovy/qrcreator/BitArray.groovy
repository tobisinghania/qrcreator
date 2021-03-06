/*
 * Copyright 2007 ZXing authors
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
import groovy.transform.EqualsAndHashCode

/**
 * <p>A simple, fast array of bits, represented compactly by an array of ints internally.</p>
 *
 * @author Sean Owen
 * @author modified by Tobias Singhania
 */
@CompileStatic
@EqualsAndHashCode(includeFields=true)
class BitArray implements Cloneable {

    private int[] bits
    final int size

    BitArray() {
        this.size = 0
        this.bits = new int[1]
    }

    BitArray(int size) {
        this.size = size
        this.bits = makeArray(size)
    }

    // For testing only
    BitArray(int[] bits, int size) {
        this.bits = bits
        this.size = size
    }

    int getSizeInBytes() {
        return (int)((size + 7) / 8)
    }

    private void ensureCapacity(int size) {
        if (size > bits.size() * 32) {
            int[] newBits = makeArray(size)
            System.arraycopy(bits, 0, newBits, 0, bits.size())
            this.bits = newBits
        }
    }

    /**
     * @param i bit to get
     * @return true iff bit i is set
     */
    boolean get(int i) {
        return (bits[(int)(i / 32)] & (1 << (i & 0x1F))) != 0
    }

    /**
     * Sets bit i.
     *
     * @param i bit to set
     */
    void set(int i) {
        bits[(int)(i / 32)] |= 1 << (i & 0x1F)
    }

    /**
     * Flips bit i.
     *
     * @param i bit to set
     */
    void flip(int i) {
        bits[(int)(i / 32)] ^= 1 << (i & 0x1F)
    }

    /**
     * @param from first bit to check
     * @return index of first bit that is set, starting from the given index, or size if none are set
     *         at or beyond this given index
     * @see #getNextUnset(int)
     */
    int getNextSet(int from) {
        if (from >= size) {
            return size
        }
        int bitsOffset = (int)(from / 32)
        int currentBits = bits[bitsOffset]
        // mask off lesser bits first
        currentBits &= ~((1 << (from & 0x1F)) - 1)
        while (currentBits == 0) {
            if (++bitsOffset == bits.size()) {
                return size
            }
            currentBits = bits[bitsOffset]
        }
        int result = (bitsOffset * 32) + Integer.numberOfTrailingZeros(currentBits)
        return result > size ? size : result
    }

    /**
     * @param from index to start looking for unset bit
     * @return index of next unset bit, or {@code size} if none are unset until the end
     * @see #getNextSet(int)
     */
    int getNextUnset(int from) {
        if (from >= size) {
            return size
        }
        int bitsOffset = (int)(from / 32)
        int currentBits = ~bits[bitsOffset]
        // mask off lesser bits first
        currentBits &= ~((1 << (from & 0x1F)) - 1)
        while (currentBits == 0) {
            if (++bitsOffset == bits.size()) {
                return size
            }
            currentBits = ~bits[bitsOffset]
        }
        int result = (bitsOffset * 32) + Integer.numberOfTrailingZeros(currentBits)
        return result > size ? size : result
    }

    /**
     * Sets a block of 32 bits, starting at bit i.
     *
     * @param i first bit to set
     * @param newBits the new value of the next 32 bits. Note again that the least-significant bit
     *        corresponds to bit i, the next-least-significant to i+1, and so on.
     */
    void setBulk(int i, int newBits) {
        bits[(int)(i / 32)] = newBits
    }

    /**
     * Sets a range of bits.
     *
     * @param start start of range, inclusive.
     * @param end end of range, exclusive
     */
    void setRange(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException()
        }
        if (end == start) {
            return
        }
        end-- // will be easier to treat this as the last actually set bit -- inclusive
        int firstInt = (int)(start / 32)
        int lastInt = (int)(end / 32)
        for (int i = firstInt; i <= lastInt; i++) {
            int firstBit = i > firstInt ? 0 : start & 0x1F
            int lastBit = i < lastInt ? 31 : end & 0x1F
            int mask
            if (firstBit == 0 && lastBit == 31) {
                mask = -1
            } else {
                mask = 0
                for (int j = firstBit; j <= lastBit; j++) {
                    mask |= 1 << j
                }
            }
            bits[i] |= mask
        }
    }

    /**
     * Clears all bits (sets to false).
     */
    void clear() {
        bits.size().times { int i ->  bits[i] = 0 }
    }

    /**
     * Efficient method to check if a range of bits is set, or not set.
     *
     * @param start start of range, inclusive.
     * @param end end of range, exclusive
     * @param value if true, checks that bits in range are set, otherwise checks that they are not set
     * @return true iff all bits are set or not set in range, according to value argument
     * @throws IllegalArgumentException if end is less than or equal to start
     */
    boolean isRange(int start, int end, boolean value) {
        if (end < start) {
            throw new IllegalArgumentException()
        }
        if (end == start) {
            return true // empty range matches
        }
        end-- // will be easier to treat this as the last actually set bit -- inclusive
        int firstInt = (int)(start / 32)
        int lastInt = (int)(end / 32)
        for (int i = firstInt; i <= lastInt; i++) {
            int firstBit = i > firstInt ? 0 : start & 0x1F
            int lastBit = i < lastInt ? 31 : end & 0x1F
            int mask
            if (firstBit == 0 && lastBit == 31) {
                mask = -1
            } else {
                mask = 0
                for (int j = firstBit; j <= lastBit; j++) {
                    mask |= 1 << j
                }
            }

            // Return false if we're looking for 1s and the masked bits[i] isn't all 1s (that is,
            // equals the mask, or we're looking for 0s and the masked portion is not all 0s
            if ((bits[i] & mask) != (value ? mask : 0)) {
                return false
            }
        }
        return true
    }

    void appendBit(boolean bit) {
        ensureCapacity(size + 1)
        if (bit) {
            bits[(int)(size / 32)] |= 1 << (size & 0x1F)
        }
        size++
    }

    /**
     * Appends the least-significant bits, from value, in order from most-significant to
     * least-significant. For example, appending 6 bits from 0x000001E will append the bits
     * 0, 1, 1, 1, 1, 0 in that order.
     *
     * @param value {@code int} containing bits to append
     * @param numBits bits from value to append
     */
    void appendBits(int value, int numBits) {
        if (numBits < 0 || numBits > 32) {
            throw new IllegalArgumentException("Num bits must be between 0 and 32")
        }
        ensureCapacity(size + numBits)
        for (int numBitsLeft = numBits; numBitsLeft > 0; numBitsLeft--) {
            appendBit(((value >> (numBitsLeft - 1)) & 0x01) == 1)
        }
    }

    void appendBitArray(BitArray other) {
        int otherSize = other.size
        ensureCapacity(size + otherSize)
        otherSize.times { int i -> appendBit(other.get(i)) }
    }

    void xor(BitArray other) {
        if (bits.size() != other.bits.size()) {
            throw new IllegalArgumentException("Sizes don't match")
        }
		  // The last byte could be incomplete (i.e. not have 8 bits in it)
		  // but there is no problem since 0 XOR 0 == 0.
        bits.size().times { int i -> bits[i] ^= other.bits[i] }
    }

    /**
     * @param bitOffset first bit to start writing
     * @param array array to write into. Bytes are written most-significant byte first. This is the opposite
     *        of the internal representation, which is exposed by {@link #getBitArray()}
     * @param offset position in array to start writing
     * @param numBytes how many bytes to write
     */
    void toBytes(int bitOffset, byte[] array, int offset, int numBytes) {
        numBytes.times { int i ->
            int theByte = 0
            8.times { int j ->
                if (get(bitOffset)) {
                    theByte |= 1 << (7 - j)
                }
                bitOffset++
            }
            array[offset + i] = (byte) theByte
        }
    }

    /**
     * @return underlying array of ints. The first element holds the first 32 bits, and the least
     *         significant bit is bit 0.
     */
    int[] getBitArray() {
        return bits
    }

    /**
     * Reverses all bits in the array.
     */
    void reverse() {
        int[] newBits = new int[bits.size()]
        // reverse all int's first
        int len = (int)(((size - 1) / 32))
        int oldBitsLen = len + 1
        oldBitsLen.times { int i ->
            long x = (long) bits[i]
            x = ((x >> 1) & 0x55555555L) | ((x & 0x55555555L) << 1)
            x = ((x >> 2) & 0x33333333L) | ((x & 0x33333333L) << 2)
            x = ((x >> 4) & 0x0f0f0f0fL) | ((x & 0x0f0f0f0fL) << 4)
            x = ((x >> 8) & 0x00ff00ffL) | ((x & 0x00ff00ffL) << 8)
            x = ((x >> 16) & 0x0000ffffL) | ((x & 0x0000ffffL) << 16)
            newBits[len - i] = (int) x
        }
        // now correct the int's if the bit size isn't a multiple of 32
        if (size != oldBitsLen * 32) {
            int leftOffset = oldBitsLen * 32 - size
            int mask = 1
            for (int i = 0; i < 31 - leftOffset; i++) {
                mask = (mask << 1) | 1
            }
            int currentInt = (newBits[0] >> leftOffset) & mask
            for (int i = 1; i < oldBitsLen; i++) {
                int nextInt = newBits[i]
                currentInt |= nextInt << (32 - leftOffset)
                newBits[i - 1] = currentInt
                currentInt = (nextInt >> leftOffset) & mask
            }
            newBits[oldBitsLen - 1] = currentInt
        }
        bits = newBits
    }

    private static int[] makeArray(int size) {
        return new int[(size + 31) / 32]
    }

    @Override
    String toString() {
        StringBuilder result = new StringBuilder(size)
        size.times { int i ->
            if ((i & 0x07) == 0) {
                result << ' '
            }
            result << get(i) ? 'X' : '.'
        }
        return result
    }

    @Override
    BitArray clone() {
        return new BitArray((int[])bits.clone(), size)
    }
}
